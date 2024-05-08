package br.com.thiagomagdalena.chatbotjava.web.controller;

import br.com.thiagomagdalena.chatbotjava.domain.service.ChatBotService;
import br.com.thiagomagdalena.chatbotjava.web.dto.PerguntaDto;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import io.reactivex.Flowable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@Controller
@RequiredArgsConstructor
@RequestMapping({"/", "chat"})
public class ChatController {

    private static final String PAGINA_CHAT = "chat";

    private final ChatBotService chatBotService;

    @GetMapping
    public String carregarPaginaChatbot() {
        return PAGINA_CHAT;
    }

    @PostMapping
    @ResponseBody
    public ResponseBodyEmitter responderPergunta(@RequestBody PerguntaDto dto) {
        var fluxoResponsta = chatBotService.responderPergunta(dto.pergunta());
        var emitter = new ResponseBodyEmitter();
        fluxoResponsta.subscribe(chunck -> {
            var token = chunck.getChoices().get(0).getMessage().getContent();
            if (token != null) {
                emitter.send(token);
            }
        }, emitter::completeWithError, emitter::complete);
        return emitter;
    }

    @GetMapping("limpar")
    public String limparConversa() {
        return PAGINA_CHAT;
    }

}
