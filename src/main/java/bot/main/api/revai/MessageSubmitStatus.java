package bot.main.api.revai;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MessageSubmitStatus {

    private String id;
    private String status;
    private String failure_detail;
}
