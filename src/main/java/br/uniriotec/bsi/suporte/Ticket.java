package br.uniriotec.bsi.suporte;

import java.util.List;

public class Ticket {
	private String assunto;
	private EstadoTicket estadoAtual;
	private List<Mensagem> mensagens;
}
