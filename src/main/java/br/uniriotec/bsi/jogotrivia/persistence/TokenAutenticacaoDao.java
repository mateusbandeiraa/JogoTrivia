package br.uniriotec.bsi.jogotrivia.persistence;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.uniriotec.bsi.jogotrivia.administrativo.TokenAutenticacao;
import br.uniriotec.bsi.jogotrivia.administrativo.Usuario;

public class TokenAutenticacaoDao extends Dao<TokenAutenticacao>{

	public TokenAutenticacaoDao() {
		super(TokenAutenticacao.class);
	}
	
	public TokenAutenticacao selectByUser(int idUsuario) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Query q = em.createQuery("FROM " + TokenAutenticacao.class.getSimpleName() + " WHERE usuario_id = ?1");
		q.setParameter(1, idUsuario);
		TokenAutenticacao result;
		try {
			result = (TokenAutenticacao) q.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
		em.getTransaction().commit();
		em.close();
		return result;
	}
	
	public TokenAutenticacao selectByUser(Usuario usuario) {
		return selectByUser(usuario.getId());
	}
	
	public TokenAutenticacao selectByToken(String token) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Query q = em.createQuery("FROM " + TokenAutenticacao.class.getSimpleName() + " WHERE token = ?1");
		q.setParameter(1, token);
		TokenAutenticacao result;
		try {
			result = (TokenAutenticacao) q.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
		em.getTransaction().commit();
		em.close();
		return result;
	}

}
