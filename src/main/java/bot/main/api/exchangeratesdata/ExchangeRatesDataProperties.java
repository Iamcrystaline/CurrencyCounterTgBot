package bot.main.api.exchangeratesdata;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@AllArgsConstructor
@ConfigurationProperties(prefix = "exchangeratesdata.api")
public class ExchangeRatesDataProperties {

    @NotBlank
    private String key;
}
