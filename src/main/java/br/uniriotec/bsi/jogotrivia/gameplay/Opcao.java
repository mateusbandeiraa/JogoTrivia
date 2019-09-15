package br.uniriotec.bsi.jogotrivia.gameplay;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Opcao {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String texto;
	private boolean removivel;

	public Opcao() {

	}

	public Opcao(String texto) {
		this.texto = texto;
	}

	public Opcao(int id, String texto) {
		this(texto);
		this.id = id;
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

	public void isRemovivel(boolean removivel) {
		this.removivel = removivel;
	}
}
