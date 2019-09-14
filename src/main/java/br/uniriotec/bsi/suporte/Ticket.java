package br.uniriotec.bsi.suporte;

import java.util.List;

import br.uniriotec.bsi.administrativo.Moderador;
import br.uniriotec.bsi.administrativo.Usuario;

public class Ticket {
	private String assunto;
	private EstadoTicket estadoAtual;
	private List<Mensagem> mensagens;
	private Usuario solicitante;
	private Moderador solucionador;
}
