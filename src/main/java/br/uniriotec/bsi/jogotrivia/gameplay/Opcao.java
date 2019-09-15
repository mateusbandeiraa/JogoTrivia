package br.uniriotec.bsi.jogotrivia.gameplay;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Opcao {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(nullable = false)
	private String texto;
	@Column(nullable = false)
	private boolean removivel;
	@Column(nullable = false)
	private boolean correta;

	public Opcao() {

	}

	public Opcao(String texto, boolean removivel) {
		this.texto = texto;
		this.removivel = removivel;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Opcao) {
			Opcao o = (Opcao) obj;
			return this.getId() == o.getId() && this.getTexto().equals(o.getTexto())
					&& this.isRemovivel() == o.isRemovivel();
		}
		return super.equals(obj);
	}

	public int getId() {
		return id;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public boolean isRemovivel() {
		return removivel;
	}

	public void setRemovivel(boolean removivel) {
		this.removivel = removivel;
	}

	public boolean isCorreta() {
		return correta;
	}

	protected void setCorreta(boolean correta) {
		this.correta = correta;
	}

}
