package bot.main.api.exchangeratesdata;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BadRequestErrorBody {

    private BadRequestError error;

    @Getter
    @NoArgsConstructor
    public class BadRequestError {

        private String code;
        private String message;
    }
}
