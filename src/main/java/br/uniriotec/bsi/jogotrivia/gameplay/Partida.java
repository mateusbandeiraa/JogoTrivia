package br.uniriotec.bsi.jogotrivia.gameplay;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Partida {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@Column(nullable = false)
	private String nome;
	@OneToMany
	private List<Rodada> rodadas;
	@OneToOne
	private Rodada rodadaAtual;
	@Column(nullable = false)
	private Date dataInicio;
	@Column(nullable = false)
	private EstadoPartida estadoAtual;
}
