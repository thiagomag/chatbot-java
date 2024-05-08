package br.com.thiagomagdalena.chatbotjava.domain.service;

import br.com.thiagomagdalena.chatbotjava.infra.openai.DadosRequisicaoChatCompletion;
import br.com.thiagomagdalena.chatbotjava.infra.openai.OpenAIClient;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatBotService {

    private final OpenAIClient openAIClient;

    public Flowable<ChatCompletionChunk> responderPergunta(String pergunta) {
        var promptSistema = "Você é um chatbot de atendimento a clientes de um ecommerce e deve responder apenas perguntas relacionadas com o ecommerce";
        var dados = new DadosRequisicaoChatCompletion(promptSistema, pergunta);
        return openAIClient.enviarRequisicaoChatCompletion(dados);

    }
}
