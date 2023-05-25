package bot.main.api.exchangeratesdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;

public class ExchangeRatesDataErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder errorDecoder = new Default();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Exception decode(String s, Response response) {
        if (response.status() == 400) {
            BadRequestErrorBody errorBody;
            try (InputStream bodyIs = response.body().asInputStream()) {
                errorBody = mapper.readValue(bodyIs, BadRequestErrorBody.class);
            } catch (IOException e) {
                return new Exception(e.getMessage());
            }
            if (errorBody.getError().getCode().equals("invalid_currency_codes") ||
                    errorBody.getError().getCode().equals("invalid_base_currency")) {
                return new InvalidCurrencyCodeException(errorBody.getError().getMessage());
            }
        }
        return errorDecoder.decode(s, response);
    }
}
