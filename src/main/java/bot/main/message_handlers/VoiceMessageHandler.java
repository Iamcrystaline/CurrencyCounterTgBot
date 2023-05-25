package bot.main.message_handlers;

import bot.main.job.Job;
import bot.main.job.JobRepository;
import bot.main.api.revai.MessageSubmitStatus;
import bot.main.api.revai.RevAiClient;
import bot.main.api.revai.RevAiProperties;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VoiceMessageHandler {

    private final TelegramBot telegramBot;
    private final RevAiClient revAiClient;
    private final RevAiProperties revAiProperties;
    private final JobRepository jobRepository;

    public void handleVoiceMessage(Message message) {
        GetFile getFileRequest = new GetFile(message.voice().fileId());
        GetFileResponse getFileResponse = telegramBot.execute(getFileRequest);
        File voiceFile = getFileResponse.file();
        MessageSubmitStatus submitStatus = revAiClient.submitFileForTranscription(revAiProperties.getKey(),
                telegramBot.getFullFilePath(voiceFile));
        if (submitStatus.getStatus().equals("in_progress")) {
            jobRepository.save(new Job(submitStatus.getId(), message.chat().id(), message.messageId()));
            telegramBot.execute(new SendMessage(message.chat().id(), "Голосовое сообщение отправлено на обработку. Это может занять некоторое время"));
        } else {
            log.error("Message submit failed with status " + submitStatus.getStatus());
            telegramBot.execute(new SendMessage(message.chat().id(), "При отправке голосового сообщения на обработку что-то пошло не так. Пожалуйста, попробуйте повторить попытку позже"));
        }
    }
}
