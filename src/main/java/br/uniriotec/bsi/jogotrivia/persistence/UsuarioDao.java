package br.uniriotec.bsi.jogotrivia.persistence;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;

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
}
