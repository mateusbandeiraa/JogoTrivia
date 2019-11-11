package br.uniriotec.bsi.jogotrivia.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;
import br.uniriotec.bsi.jogotrivia.financeiro.Lancamento;

public class UsuarioDao extends Dao<Usuario> {

	public UsuarioDao() {
		super(Usuario.class);
	}

	public Usuario selectByEmail(String email) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Query q = em.createQuery("FROM " + Usuario.class.getSimpleName() + " WHERE email = ?1");
		q.setParameter(1, email);
		Usuario result;
		try {
			result = (Usuario) q.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
		em.getTransaction().commit();
		em.close();
		return result;
	}

	public Usuario selectLancamentos(Usuario u) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Query q = em.createQuery("FROM Lancamento WHERE usuario.id = ?1");
		q.setParameter(1, u.getId());
		@SuppressWarnings("unchecked")
		List<Lancamento> lancamentos = q.getResultList();
		u.setLancamentos(lancamentos);
		em.getTransaction().commit();
		em.close();
		return u;
	}
}
