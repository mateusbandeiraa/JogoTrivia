package br.uniriotec.bsi.jogotrivia.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class Dao<T> {
	protected static EntityManagerFactory emf;
	private Class<T> persistedClass;

	public Dao(Class<T> persistedClass) {
		this.persistedClass = persistedClass;
		if (emf == null)
			setUp();
	}

	protected static void setUp() {
		String unit = "br.uniriotec.bsi.jogotrivia";
		emf = Persistence.createEntityManagerFactory(unit);
	}

	protected static void tearDown() {
		if (emf != null && emf.isOpen())
			emf.close();
	}

	public T select(int id) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		T result = em.find(persistedClass, id);
		em.getTransaction().commit();
		em.close();
		return result;
	}

	public List<T> selectAll() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		Query q = em.createQuery("FROM " + persistedClass.getSimpleName()); // Não é bonito, mas funciona.
		@SuppressWarnings("unchecked")
		List<T> result = q.getResultList();
		em.getTransaction().commit();
		em.close();
		return result;
	}

	public void insert(T entity) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(entity);
		em.getTransaction().commit();
		em.close();
	}

	public void update(T entity) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.merge(entity);
		em.getTransaction().commit();
		em.close();
	}

	public void delete(T entity) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		if (!em.contains(entity)) {
			entity = em.merge(entity);
		}
		em.remove(entity);
		em.getTransaction().commit();
		em.close();
	}

	public void delete(int id) {
		T entity = select(id);
		delete(entity);
	}
}
