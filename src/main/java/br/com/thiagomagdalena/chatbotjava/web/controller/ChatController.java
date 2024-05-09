package br.com.thiagomagdalena.chatbotjava.web.controller;

import br.com.thiagomagdalena.chatbotjava.domain.service.ChatBotService;
import br.com.thiagomagdalena.chatbotjava.web.dto.PerguntaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping({"/", "chat"})
public class ChatController {

    private static final String PAGINA_CHAT = "chat";

    private final ChatBotService chatBotService;

    @GetMapping
    public String carregarPaginaChatbot(Model model) {
        var mensagens = chatBotService.carregarHistorico();

        model.addAttribute("historico", mensagens);

        return PAGINA_CHAT;
    }

    @PostMapping
    @ResponseBody
    public String responderPergunta(@RequestBody PerguntaDto dto) {
        return chatBotService.responderPergunta(dto.pergunta());
    }

    @GetMapping("limpar")
    public String limparConversa() {
        chatBotService.limparHistorico();
        return "redirect:/chat";
    }

}
