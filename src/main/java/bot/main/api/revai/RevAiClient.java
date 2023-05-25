package bot.main.api.revai;

import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface RevAiClient {

    @RequestLine("POST /jobs")
    @Headers({"Authorization: Bearer {token}",
            "Content-Type: application/json"})
    @Body("%7B\"source_config\": %7B\"url\": \"{fileUrl}\"%7D," +
            "\"language\": \"ru\"," +
            "\"skip_diarization\": true," +
            "\"delete_after_seconds\": 86400%7D")
    MessageSubmitStatus submitFileForTranscription(@Param("token") String token,
                                                   @Param("fileUrl") String fileUrl);

    @RequestLine("GET /jobs/{jobId}/transcript")
    @Headers({"Authorization: Bearer {token}",
            "Accept: application/vnd.rev.transcript.v1.0+json"})
    MessageTranscript getTranscriptedMessage(@Param("jobId") String jobId,
                                             @Param("token") String token);

    @RequestLine("GET /jobs")
    @Headers("Authorization: Bearer {token}")
    List<MessageSubmitStatus> getAllJobs(@Param("token") String token);
}
