package bot.main.message;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MessageId.class)
@Table(name = "messages")
public class Message {

    @Id
    private Long chatId;
    @Id
    private Integer messageId;
    private String text;
}
