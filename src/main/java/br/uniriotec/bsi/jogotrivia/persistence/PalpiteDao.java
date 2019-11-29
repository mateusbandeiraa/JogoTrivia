package br.uniriotec.bsi.jogotrivia.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.uniriotec.bsi.jogotrivia.gameplay.Palpite;

public class PalpiteDao extends Dao<Palpite> {

	public PalpiteDao() {
		super(Palpite.class);
	}
	
	public List<Palpite> selectByParticipante(Integer idParticipante){
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Query q = em.createQuery("FROM Palpite WHERE participante_id = ?1");
		q.setParameter(1, idParticipante);
		@SuppressWarnings("unchecked")
		List<Palpite> result = q.getResultList();
		em.getTransaction().commit();
		em.close();
		return result;
	}

}
