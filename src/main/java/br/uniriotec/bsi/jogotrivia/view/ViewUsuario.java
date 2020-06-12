package br.uniriotec.bsi.jogotrivia.view;

public interface ViewUsuario {
	/**
	 * Campos que o proprio usuario pode acessar
	 */
	public static interface Proprio extends ViewUsuario {
		public static interface Parametros {
			/**
			 * Campos de entrada no endpoint de cadastro
			 */
			public static interface Cadastrar {
			}

			/**
			 * Campos de entrada no endpoint de atualizacao
			 */
			public static interface Atualizar {
			}

			/**
			 * Campos de entrada no endpoint de autenticação
			 */
			public static interface Autenticar {
			}
		}
	}

	/**
	 * Campos que moderadores podem acessar
	 */
	public static interface Moderador extends Proprio {
		public static interface Parametros {
			/**
			 * Campos de entrada no endpoint de cadastro
			 */
			public static interface Cadastrar extends Proprio.Parametros.Cadastrar {
			}

			/**
			 * Campos de entrada no endpoint de atualizacao
			 */
			public static interface Atualizar extends Proprio.Parametros.Atualizar {
			}
		}
	}
}
