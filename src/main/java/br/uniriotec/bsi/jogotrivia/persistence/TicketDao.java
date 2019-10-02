package br.uniriotec.bsi.jogotrivia.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.uniriotec.bsi.jogotrivia.suporte.Ticket;

public class TicketDao extends Dao<Ticket> {

	public TicketDao() {
		super(Ticket.class);
	}

	public List<Ticket> selectByUser(int userID) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Query q = em.createQuery("FROM Ticket WHERE solicitante_id = ?1");
		q.setParameter(1, userID);
		@SuppressWarnings("unchecked")
		List<Ticket> result = q.getResultList();
		em.getTransaction().commit();
		em.close();
		return result;
	}
}
