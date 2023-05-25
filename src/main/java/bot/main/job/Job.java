package bot.main.job;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "jobs")
public class Job {

    @Id
    private String jobId;
    private Long senderChatId;
    private Integer messageId;
    @Column(insertable = false, updatable = false, columnDefinition = "serial")
    private Long orderId;
    public Job(String jobId, Long senderChatId, Integer messageId) {
        this.jobId = jobId;
        this.senderChatId = senderChatId;
        this.messageId = messageId;
    }
}
