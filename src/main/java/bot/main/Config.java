package bot.main;

import bot.main.api.exchangeratesdata.ExchangeRate;
import bot.main.api.exchangeratesdata.ExchangeRatesDataClient;
import bot.main.api.exchangeratesdata.ExchangeRatesDataErrorDecoder;
import bot.main.api.revai.RevAiClient;
import bot.main.telegram.TelegramBotProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.pengrad.telegrambot.TelegramBot;
import feign.Feign;
import feign.codec.StringDecoder;
import feign.gson.GsonDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class Config {

    private final TelegramBotProperties telegramBotProperties;

    @Bean
    public TelegramBot getTelegramBot() {
        return new TelegramBot(telegramBotProperties.getKey());
    }

    @Bean
    public ExchangeRatesDataClient getExchangeRatesDataClient() {
        return Feign.builder()
                .decoder(new StringDecoder())
                .errorDecoder(new ExchangeRatesDataErrorDecoder())
                .target(ExchangeRatesDataClient.class, "https://api.apilayer.com/exchangerates_data");
    }

    @Bean
    public RevAiClient getRevAiClient() {
        return Feign.builder()
                .decoder(new GsonDecoder())
                .target(RevAiClient.class, "https://api.rev.ai/speechtotext/v1");
    }

    @Bean
    public Cache<ExchangeRate, BigDecimal> getExchangeRatesCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
    }
}
