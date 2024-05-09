package br.com.thiagomagdalena.chatbotjava.infra.openai;

import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.runs.RunCreateRequest;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.threads.ThreadRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Component
public class OpenAIClient {

    private final String apiKey;
    private final OpenAiService service;
    private final String assistantId;
    private String threadId;


    public OpenAIClient(@Value("${app.openai.api.key}") String apiKey,
                        @Value("${app.openai.assistant.id}") String assistantId) {
        this.apiKey = apiKey;
        this.service = new OpenAiService(apiKey, Duration.ofSeconds(60));
        this.assistantId = assistantId;
    }

    public String enviarRequisicaoChatCompletion(DadosRequisicaoChatCompletion dados) {
        var messageRequest = MessageRequest
                .builder()
                .role(ChatMessageRole.USER.value())
                .content(dados.promptUsuario())
                .build();

        if (this.threadId == null) {
            var threadRequest = ThreadRequest
                    .builder()
                    .messages(Arrays.asList(messageRequest))
                    .build();

            var thread = service.createThread(threadRequest);
            this.threadId = thread.getId();
        } else {
            service.createMessage(this.threadId, messageRequest);
        }

        var runRequest = RunCreateRequest
                .builder()
                .assistantId(assistantId)
                .build();
        var run = service.createRun(threadId, runRequest);

        try {
            while (!run.getStatus().equalsIgnoreCase("completed")) {
                Thread.sleep(1000 * 10);
                run = service.retrieveRun(threadId, run.getId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var mensagens = service.listMessages(threadId);
        return mensagens
                .getData()
                .stream().max(Comparator.comparing(Message::getCreatedAt))
                .get().getContent().get(0).getText().getValue();
    }

    public List<String> carregarHistoricoDeMensagens() {
        var mensagens = new ArrayList<String>();

        if (this.threadId != null) {
            mensagens.addAll(
                    service
                            .listMessages(this.threadId)
                            .getData()
                            .stream()
                            .sorted(Comparator.comparingInt(Message::getCreatedAt))
                            .map(m -> m.getContent().get(0).getText().getValue())
                            .toList()
            );
        }

        return mensagens;
    }

    public void apagarThread() {
        if (this.threadId != null) {
            service.deleteThread(this.threadId);
            this.threadId = null;
        }
    }

}
