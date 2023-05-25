package bot.main.job;

import bot.main.message_handlers.TextMessageHandler;
import bot.main.api.revai.MessageSubmitStatus;
import bot.main.api.revai.MessageTranscript;
import bot.main.api.revai.RevAiClient;
import bot.main.api.revai.RevAiProperties;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobStatusChecker {

    private final RevAiClient revAiClient;
    private final RevAiProperties revAiProperties;
    private final JobRepository jobRepository;
    private final TextMessageHandler textMessageHandler;
    private final TelegramBot telegramBot;

    @Scheduled(fixedRate = 300_000)
    public void checkJobs() {
        List<MessageSubmitStatus> jobStatuses = revAiClient.getAllJobs(revAiProperties.getKey());
        for (MessageSubmitStatus jobStatus : jobStatuses) {
            switch (jobStatus.getStatus()) {
                case "transcribed" -> getAndHandleTranscript(jobStatus);
                case "failed" -> logExceptionAndWarnUser(jobStatus);
                case "in_progress" -> {
                }
                default -> log.error("Unknown job status. Check RevAi API documentation for more info");
            }
        }
    }

    @Transactional
    protected void logExceptionAndWarnUser(MessageSubmitStatus jobStatus) {
        jobRepository.findById(jobStatus.getId())
                .ifPresent(job -> {
                    telegramBot.execute(new SendMessage(job.getSenderChatId(), "Перевод голосовго сообщения в текстовый формат не удался. Пожалуйста, попробуйте позже"));
                    log.error(jobStatus.getFailure_detail());
                });
        jobRepository.deleteById(jobStatus.getId());
    }

    @Transactional
    protected void getAndHandleTranscript(MessageSubmitStatus jobStatus) {
        jobRepository.findById(jobStatus.getId())
                .ifPresent(job -> {
                    MessageTranscript messageTranscript = revAiClient.getTranscriptedMessage(job.getJobId(), revAiProperties.getKey());
                    String textMessage = messageTranscript.getMonologues()
                            .get(0)
                            .getElements()
                            .stream()
                            .map(MessageTranscript.Monologue.Element::getValue)
                            .collect(Collectors.joining(""));
                    textMessageHandler.handleTextMessage(job.getSenderChatId(), job.getMessageId(), textMessage);
                });
        jobRepository.deleteById(jobStatus.getId());
    }
}
