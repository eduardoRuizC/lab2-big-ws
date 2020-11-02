package translator.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import translator.Application;
import translator.domain.TranslatedText;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TranslatorServiceTest {

  @Autowired
  TranslatorService translatorService;

  @Test
  public void translateTest() {
    Exception exception = assertThrows(RuntimeException.class, () -> {
      TranslatedText translatedText = translatorService.translate("en", "es", "This is a test of translation service");
      translatedText.getTranslation();
    });
    assertTrue(exception.getMessage().contains("I don't know how to translate from en to es the text 'This is a test of translation service'"));
  }

}
