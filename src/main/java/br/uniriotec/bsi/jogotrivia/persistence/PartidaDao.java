package br.uniriotec.bsi.jogotrivia.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.uniriotec.bsi.jogotrivia.gameplay.Partida;

public class PartidaDao extends Dao<Partida> {

	public PartidaDao() {
		super(Partida.class);
	}

	public List<Partida> selectDisponiveis(){
			EntityManager em = emf.createEntityManager();
			em.getTransaction().begin();
			Query q = em.createQuery("FROM Partida WHERE estadoAtual = 'DISPONIVEL'"); // Não é bonito, mas funciona.
			@SuppressWarnings("unchecked")
			List<Partida> result = q.getResultList();
			em.getTransaction().commit();
			em.close();
			return result;
	}

}
