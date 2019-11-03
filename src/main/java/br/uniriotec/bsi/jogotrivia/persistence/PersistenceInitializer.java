package br.uniriotec.bsi.jogotrivia.persistence;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class PersistenceInitializer implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("Inicializando conexão com o banco de dados.");
		Dao.setUp();	
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("Fechando conexão com o banco de dados.");
		Dao.tearDown();
	}

}
