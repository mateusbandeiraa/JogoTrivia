package br.uniriotec.bsi.jogotrivia.gameplay;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Rodada {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	@ManyToOne(optional = false)
	private Questao questao;
	@Column(nullable = false)
	private int tempoExtra; // Em segundos
	@Column
	private Date dataInicio;
	// List<Palpite>
}
