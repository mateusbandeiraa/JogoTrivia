package br.uniriotec.bsi.jogotrivia.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.uniriotec.bsi.jogotrivia.gameplay.Questao;

public class QuestaoDao extends Dao<Questao> {

	public QuestaoDao() {
		super(Questao.class);
	}

	public List<Questao> selectByAutor(Integer id) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Query q = em.createQuery("FROM " + Questao.class.getSimpleName() + " WHERE autor_id = ?1");
		q.setParameter(1, id);
		@SuppressWarnings("unchecked")
		List<Questao> result = q.getResultList();
		em.getTransaction().commit();
		em.close();
		return result;
	}

}
