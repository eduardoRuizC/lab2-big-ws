package translator.web.ws;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ClassUtils;
import org.springframework.ws.client.WebServiceTransportException;
import org.springframework.ws.client.core.WebServiceTemplate;

import translator.Application;
import translator.web.ws.schema.GetTranslationRequest;
import translator.web.ws.schema.GetTranslationResponse;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = Application.class)
public class TranslatorEndpointTest {

  private final Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

  @LocalServerPort
  private int port;

  @Before
  public void init() throws Exception {
    marshaller.setPackagesToScan(ClassUtils.getPackageName(GetTranslationRequest.class));
    marshaller.afterPropertiesSet();
  }

  @Test
  public void testSendAndReceive() {
    GetTranslationRequest request = new GetTranslationRequest();
    request.setLangFrom("en");
    request.setLangTo("es");
    request.setText("This is a test of translation service");
    Exception exception = assertThrows(RuntimeException.class, () -> {
      Object response = new WebServiceTemplate(marshaller).marshalSendAndReceive("http://localhost:" + port + "/ws", request);
      assertNotNull(response);
      assertThat(response, instanceOf(GetTranslationResponse.class));
      GetTranslationResponse translation = (GetTranslationResponse) response;
      translation.getTranslation();
    });
    assertTrue(exception.getMessage().contains("I don't know how to translate from en to es the text 'This is a test of translation service'"));
  }

  @Test
  public void testWebServiceNotFound() {
    GetTranslationRequest request = new GetTranslationRequest();
    request.setLangFrom("en");
    request.setLangTo("es");
    request.setText("This is a test of translation service");
    Exception exception = assertThrows(WebServiceTransportException.class, () -> {
      new WebServiceTemplate(marshaller).marshalSendAndReceive("http://localhost:" + port + "/wsFail", request);
    });
    assertTrue(exception.getMessage().contains("[404]"));
  }
}
