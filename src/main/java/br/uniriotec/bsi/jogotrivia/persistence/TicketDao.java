package br.uniriotec.bsi.jogotrivia.persistence;

import java.util.Date;

import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.suporte.Mensagem;
import br.uniriotec.bsi.jogotrivia.suporte.Ticket;

public class TicketDao extends Dao<Ticket> {

	public TicketDao() {
		super(Ticket.class);
	}

	public static void main(String[] args) {
		UsuarioDao ud = new UsuarioDao();
		Usuario mateus = ud.select(1);
		Usuario beto = ud.select(4);

		Ticket ticket = new Ticket("Teste de ticket", beto);
		
		Mensagem mensagem1 = new Mensagem("Hello ticket!", beto, new Date());
		Mensagem mensagem2 = new Mensagem("Goodbye ticket!", mateus, new Date());
		
		ticket.adicionarMensagem(mensagem1);
		ticket.adicionarMensagem(mensagem2);
		
		TicketDao td = new TicketDao();
		td.insert(ticket);

	}
}
