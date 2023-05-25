package bot.main;

import bot.main.api.exchangeratesdata.ExchangeRatesDataProperties;
import bot.main.api.revai.RevAiProperties;
import bot.main.telegram.TelegramBotProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties({
        TelegramBotProperties.class,
        RevAiProperties.class,
        ExchangeRatesDataProperties.class
})
@EnableScheduling
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
