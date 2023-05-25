package bot.main.telegram;

import bot.main.message_handlers.TextMessageHandler;
import bot.main.message_handlers.VoiceMessageHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotListener {

    private final VoiceMessageHandler voiceMessageHandler;
    private final TextMessageHandler textMessageHandler;
    private final TelegramBot telegramBot;

    @PostConstruct
    private void setUpListeners() {
        telegramBot.setUpdatesListener(this::handleUpdate,
                e -> {
                    if (e.response() != null) {
                        log.warn(String.valueOf(e.response().errorCode()));
                        log.warn(e.response().description());
                    } else {
                        log.error("Network error");
                    }
                });
    }

    private int handleUpdate(List<Update> updates) {
        for (Update update : updates) {
            if (update.message().voice() != null) {
                voiceMessageHandler.handleVoiceMessage(update.message());
            }
            if (update.message().text() != null) {
                textMessageHandler.handleTextMessage(update.message().chat().id(), update.message().messageId(), update.message().text());
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
