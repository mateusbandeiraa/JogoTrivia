package br.uniriotec.bsi.jogotrivia.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.uniriotec.bsi.jogotrivia.gameplay.Palpite;
import br.uniriotec.bsi.jogotrivia.gameplay.Participante;

public class ParticipanteDao extends Dao<Participante> {

	public ParticipanteDao() {
		super(Participante.class);
	}

	public Participante selectPorUsuarioEPartida(Integer idUsuario, Integer idPartida) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Query q = em.createQuery(
				"FROM " + Participante.class.getSimpleName() + " WHERE usuario_id = ?1 AND partida_id = ?2");
		q.setParameter(1, idUsuario);
		q.setParameter(2, idPartida);
		Participante result;
		try {
			result = (Participante) q.getSingleResult();
			for (Palpite p : result.getPalpites()) {

			}
		} catch (NoResultException ex) {
			return null;
		}
		em.getTransaction().commit();
		em.close();
		return result;
	}

	public List<Participante> selectPorUsuario(Integer idUsuario) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Query q = em.createQuery("FROM " + Participante.class.getSimpleName() + " WHERE usuario_id = ?1 ORDER BY datacriacao DESC");
		q.setParameter(1, idUsuario);
		@SuppressWarnings("unchecked")
		List<Participante> result = q.getResultList();
		em.getTransaction().commit();
		em.close();
		return result;
	}
}
