package br.uniriotec.bsi.jogotrivia.gameplay;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonView;

import br.uniriotec.bsi.jogotrivia.service.Views.ViewAnfitriao;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewRodadaEncerrada;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewRodadaAberta;

@Entity
public class Opcao {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView(ViewAnfitriao.class)
	private int id;
	@Column(nullable = false)
	@JsonView({ ViewAnfitriao.class, ViewRodadaAberta.class })
	private String texto;
	// O columnDefinition = "BOOLEAN" é necessário para que o JPA mapeie o campo
	// para TinyInt(1). Caso contrário, o campo seria mapeado para bit, o que causa
	// problemas para visualizar os dados no terminal.
	// stackoverflow.com/a/14249348
	@Column(nullable = false, columnDefinition = "BOOLEAN")
	@JsonView({ ViewAnfitriao.class })
	private boolean removivel;
	@Column(nullable = false, columnDefinition = "BOOLEAN")
	@JsonView({ ViewAnfitriao.class, ViewRodadaEncerrada.class })
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
			return this.getId() == o.getId();
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
