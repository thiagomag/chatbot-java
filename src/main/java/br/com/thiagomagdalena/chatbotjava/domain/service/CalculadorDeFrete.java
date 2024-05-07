package br.com.thiagomagdalena.chatbotjava.domain.service;

import br.com.thiagomagdalena.chatbotjava.domain.DadosCalculoFrete;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CalculadorDeFrete {

    public BigDecimal calcular(DadosCalculoFrete dados) {
        //lógica para cálculo de frete aqui...

        return new BigDecimal("3.45").multiply(new BigDecimal(dados.quantidadeProdutos()));
    }

}
