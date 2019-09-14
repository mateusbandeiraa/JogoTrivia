package br.uniriotec.bsi.jogotrivia.suporte;

import java.util.List;

import br.uniriotec.bsi.jogotrivia.administrativo.Moderador;
import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;

public class Ticket {
	private String assunto;
	private EstadoTicket estadoAtual;
	private List<Mensagem> mensagens;
	private Usuario solicitante;
	private Moderador solucionador;
}
