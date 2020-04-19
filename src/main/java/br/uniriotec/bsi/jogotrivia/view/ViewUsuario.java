package br.uniriotec.bsi.jogotrivia.view;

public class ViewUsuario {
	/**
	 * Campos que o proprio usuario pode acessar
	 */
	public static class Proprio extends ViewUsuario {
		public static class Parametros {
			/**
			 * Campos de entrada no endpoint de cadastro
			 */
			public static class Cadastrar {
			}

			/**
			 * Campos de entrada no endpoint de atualizacao
			 */
			public static class Atualizar {
			}

			/**
			 * Campos de entrada no endpoint de autenticação
			 */
			public static class Autenticar {
			}
		}
	}

	/**
	 * Campos que moderadores podem acessar
	 */
	public static class Moderador extends Proprio {
		public static class Parametros {
			/**
			 * Campos de entrada no endpoint de cadastro
			 */
			public static class Cadastrar extends Proprio.Parametros.Cadastrar {
			}

			/**
			 * Campos de entrada no endpoint de atualizacao
			 */
			public static class Atualizar extends Proprio.Parametros.Atualizar {
			}
		}
	}
}
