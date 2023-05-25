package bot.main.api.exchangeratesdata;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface ExchangeRatesDataClient {

    @RequestLine("GET /latest?base={fromCurrency}&symbols={toCurrency}")
    @Headers("apiKey: {apiKey}")
    String getExchangeRate(@Param("fromCurrency") String fromCurrency,
                                 @Param("toCurrency") String toCurrency,
                                 @Param("apiKey") String apiKey);
}
