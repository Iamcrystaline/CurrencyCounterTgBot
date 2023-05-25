package bot.main.api.exchangeratesdata;

public class InvalidCurrencyCodeException extends RuntimeException {

    public InvalidCurrencyCodeException(String message) {
        super(message);
    }
}
