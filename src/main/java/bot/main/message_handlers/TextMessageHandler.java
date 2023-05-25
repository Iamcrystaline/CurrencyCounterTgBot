package bot.main.message_handlers;

import bot.main.api.exchangeratesdata.ExchangeRate;
import bot.main.api.exchangeratesdata.ExchangeRatesDataClient;
import bot.main.api.exchangeratesdata.ExchangeRatesDataProperties;
import bot.main.api.exchangeratesdata.InvalidCurrencyCodeException;
import bot.main.message.MessageRepository;
import com.github.benmanes.caffeine.cache.Cache;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class TextMessageHandler {

    private final MessageRepository messageRepository;
    private final TelegramBot telegramBot;
    private final Pattern currencyExchangePattern = Pattern.compile("(\\d+(\\.\\d+)?) (\\w{3}) -> (\\w{3})");
    private final ExchangeRatesDataClient exchangeRatesDataClient;
    private final ExchangeRatesDataProperties exchangeRatesDataProperties;
    private final Cache<ExchangeRate, BigDecimal> exchangeRateCache;

    public void handleTextMessage(Long chatId, Integer messageId, String messageText) {
        messageRepository.save(new bot.main.message.Message(chatId, messageId, messageText));
        if (messageText.equals("/start")) {
            telegramBot.execute(new SendMessage(chatId, "Я могу пересчитать деньги из одной валюты в другую. Для этого отправьте сообщение в формате\n1.11 USD -> KZT"));
        } else {
            Matcher exchangeMatcher = currencyExchangePattern.matcher(messageText);
            if (exchangeMatcher.matches()) {
                BigDecimal fromAmount = new BigDecimal(exchangeMatcher.group(1));
                String fromCurrency = exchangeMatcher.group(3);
                String toCurrency = exchangeMatcher.group(4);
                BigDecimal cachedExchangeRate = exchangeRateCache.getIfPresent(new ExchangeRate(fromCurrency, toCurrency));
                if (cachedExchangeRate != null) {
                    BigDecimal toAmount1 = fromAmount.multiply(cachedExchangeRate);
                    telegramBot.execute(new SendMessage(chatId, fromAmount + " " + fromCurrency + " = " + toAmount1 + " " + toCurrency));
                    return;
                }
                BigDecimal reversedExchangeRate = exchangeRateCache.getIfPresent(new ExchangeRate(toCurrency, fromCurrency));
                if (reversedExchangeRate != null) {
                    BigDecimal toAmount1 = fromAmount.multiply(new BigDecimal(1).divide(reversedExchangeRate, 6, RoundingMode.HALF_DOWN));
                    telegramBot.execute(new SendMessage(chatId, fromAmount + " " + fromCurrency + " = " + toAmount1 + " " + toCurrency));
                    return;
                }
                try {
                    String stringExchangeRates = exchangeRatesDataClient.getExchangeRate(
                            fromCurrency,
                            toCurrency,
                            exchangeRatesDataProperties.getKey()
                    );
                    JSONObject jsonExchangeRates = new JSONObject(stringExchangeRates);
                    JSONObject rates = jsonExchangeRates.getJSONObject("rates");
                    BigDecimal exchangeRate = rates.getBigDecimal(toCurrency.toUpperCase());
                    exchangeRateCache.put(new ExchangeRate(fromCurrency, toCurrency), exchangeRate);
                    BigDecimal toAmount = fromAmount.multiply(exchangeRate);
                    telegramBot.execute(new SendMessage(chatId, fromAmount + " " + fromCurrency + " = " + toAmount + " " + toCurrency));
                } catch (InvalidCurrencyCodeException e) {
                    telegramBot.execute(new SendMessage(chatId, "Неизвестный код валюты. Вводите валюту в формате ISO 4217"));
                }
            } else {
                telegramBot.execute(new SendMessage(chatId, "Неизвестная команда. Чтобы перевести деньги из одной валюты в другую, отправьте сообщение в формате\n1.11 USD -> KZT"));
            }
        }
    }

}
