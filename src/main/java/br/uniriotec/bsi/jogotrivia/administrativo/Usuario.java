package br.uniriotec.bsi.jogotrivia.administrativo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.collection.internal.PersistentBag;
import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;

import br.uniriotec.bsi.jogotrivia.config.CustomJsonProvider;
import br.uniriotec.bsi.jogotrivia.financeiro.Lancamento;
import br.uniriotec.bsi.jogotrivia.persistence.UsuarioDao;
import br.uniriotec.bsi.jogotrivia.service.Views.ViewAutenticado;
import br.uniriotec.bsi.jogotrivia.suporte.Mensagem;
import br.uniriotec.bsi.jogotrivia.suporte.Ticket;
import br.uniriotec.bsi.jogotrivia.view.ViewUsuario;

/**
 * 
 * @author Mateus Bandeira
 *         Criado em 13/09/19
 *
 */
@Entity
public class Usuario {

	public static final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView({ ViewUsuario.Proprio.class, ViewUsuario.Proprio.Parametros.Atualizar.class })
	private int id;

	@Column(nullable = false)
	@JsonView({ ViewUsuario.Proprio.class, ViewUsuario.Proprio.Parametros.Cadastrar.class,
			ViewUsuario.Proprio.Parametros.Atualizar.class })
	private String nome;

	@JsonView({ ViewUsuario.Proprio.Parametros.Cadastrar.class,
			ViewUsuario.Proprio.Parametros.Autenticar.class })
	private transient String senha;

	@Column(nullable = false)
	private String hashSenha;

	@Column(unique = true, nullable = false)
	@JsonView({ ViewUsuario.Proprio.class, ViewUsuario.Proprio.Parametros.Cadastrar.class,
			ViewUsuario.Proprio.Parametros.Atualizar.class,
			ViewUsuario.Proprio.Parametros.Autenticar.class })
	private String email;

	@Column(nullable = false, columnDefinition = "datetime default current_timestamp")
	@JsonView({ ViewUsuario.Proprio.class })
	private Date dataCadastro;

	@Column(nullable = false, columnDefinition = "boolean default true")
	@JsonView({ ViewUsuario.Proprio.class, ViewUsuario.Moderador.Parametros.Atualizar.class })
	private Boolean ativo = true;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false,
			columnDefinition = "ENUM('USUARIO', 'MODERADOR', 'ANFITRIAO') DEFAULT 'USUARIO'")
	@JsonView({ ViewUsuario.Proprio.class, ViewUsuario.Moderador.Parametros.Atualizar.class })
	private Privilegio privilegio;

	@OneToMany(orphanRemoval = true, targetEntity = Lancamento.class)
	private List<Lancamento> lancamentos;

	@Column
	@JsonView({ ViewUsuario.Proprio.class })
	private Integer quantidadeAjudasBomba = 0;

	@Column
	@JsonView({ ViewUsuario.Proprio.class })
	private Integer quantidadeAjudasPopular = 0;

	@Column
	@JsonView({ ViewUsuario.Proprio.class })
	private Integer quantidadeAjudasBonus = 0;

	public Usuario(String nome, String hashSenha, String email, Date dataCadastro, boolean ativo) {
		this(nome, email, dataCadastro, ativo);
		setHashSenha(hashSenha);
	}

	public Usuario(String nome, String email, Date dataCadastro, boolean ativo) {
		this();
		setNome(nome);
		setEmail(email);
		setDataCadastro(dataCadastro);
		setAtivo(ativo);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Usuario) {
			Usuario usuario = (Usuario) obj;
			if (usuario.getId() != 0 && this.getId() != 0) {
				return usuario.getId() == this.getId();
			}
		}
		return super.equals(obj);
	}

	/**
	 * Instancia um objeto TokenAutenticacao caso a senha passada esteja correta.
	 * Retorna null caso a senha esteja incorreta.
	 * 
	 * @param senha
	 *            String contendo a senha em plain-text
	 * @return
	 */
	public TokenAutenticacao autenticar(String senha) {
		if (BCrypt.checkpw(senha, hashSenha)) {
			return new TokenAutenticacao(this);
		} else {
			return null;
		}
	}

	public Ticket abrirTicket(String assunto, String textoMensagem) {
		Ticket t = new Ticket(assunto, this);
		Mensagem m = new Mensagem(textoMensagem, this);
		t.adicionarMensagem(m);
		return t;
	}

	/**
	 * Retorna a classe view apropriada para o privilégio passado.
	 * 
	 * @param privilegio
	 * @return
	 */
	private Class<? extends ViewUsuario> obterViewApropriada(Privilegio privilegio) {
		Class<? extends ViewUsuario> viewClass;
		switch (privilegio) {
		case USUARIO:
			viewClass = ViewUsuario.Proprio.class;
			break;
		case MODERADOR:
		case ANFITRIAO:
			viewClass = ViewUsuario.Moderador.class;
			break;
		default:
			throw new IllegalArgumentException("Privilegio inválido");
		}
		return viewClass;
	}

	/**
	 * Serializa este Usuario, apenas incluindo os campos incluídos na view
	 * apropriada para o privilégio passado.
	 * 
	 * @param privilegio
	 * @return
	 *         String JSON contendo os campos apropriados
	 * @throws JsonProcessingException
	 */
	public String serializar(Privilegio privilegio) throws JsonProcessingException {
		Class<? extends ViewUsuario> viewClass = this.obterViewApropriada(privilegio);
		return CustomJsonProvider.writerWithView(viewClass).writeValueAsString(this);
	}

	/**
	 * Serializa este Usuario, apenas incluindo os campos incluídos na view
	 * apropriada para o privilégio do Usuário passado.
	 * 
	 * @param requerente
	 *            Usuario do qual o privilégio será base para decidir qual view
	 *            deve
	 *            ser usada
	 * @return
	 *         String JSON contendo os campos apropriados
	 * @throws JsonProcessingException
	 */
	public String serializar(Usuario requerente) throws JsonProcessingException {
		return this.serializar(requerente.getPrivilegio());
	}

	/**
	 * Copia os atributos não nulos e default para este objeto.
	 * 
	 * @param fonte
	 *            Objeto usuário de onde os atributos serão copiados.
	 * @throws SenhaInvalidaException
	 */
	public void copiarAtributosNãoDefault(Usuario fonte) throws SenhaInvalidaException {
		if (fonte.getId() != 0) {
			this.setId(fonte.getId());
		}

		if (StringUtils.isNotBlank(fonte.getNome())) {
			this.setNome(fonte.getNome());
		}

		if (StringUtils.isNotBlank(fonte.getSenha())) {
			fonte.gerarHashSenha(fonte.getSenha());
		}

		if (StringUtils.isNotBlank(fonte.getEmail())) {
			this.setEmail(fonte.getEmail());
		}

		if (fonte.getAtivo() != null) {
			this.setAtivo(fonte.getAtivo());
		}

		if (fonte.getPrivilegio() != null) {
			this.setPrivilegio(fonte.getPrivilegio());
		}

		if (fonte.getQuantidadeAjudasBomba() != 0) {
			this.setQuantidadeAjudasBomba(fonte.getQuantidadeAjudasBomba());
		}

		if (fonte.getQuantidadeAjudasBonus() != 0) {
			this.setQuantidadeAjudasBonus(fonte.getQuantidadeAjudasBonus());
		}

		if (fonte.getQuantidadeAjudasPopular() != 0) {
			this.setQuantidadeAjudasPopular(fonte.getQuantidadeAjudasPopular());
		}
	}

	// @XmlElement
	@JsonView(ViewAutenticado.class)
	public BigDecimal getSaldo() {
		BigDecimal saldo = BigDecimal.ZERO;

		for (Lancamento lancamento : this.getLancamentos()) {
			saldo = saldo.add(lancamento.getValorEfetivo());
		}

		return saldo.setScale(2);
	}

	public Usuario() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) throws IllegalArgumentException {
		if (nome == null || nome.isEmpty()) {
			throw new IllegalArgumentException("Nome inválido");
		}
		this.nome = nome;
	}

	public String getSenha() {
		return senha;
	}

	public String getHashSenha() {
		return hashSenha;
	}

	public void setHashSenha(String hashSenha) {
		this.hashSenha = hashSenha;
	}

	public void gerarHashSenha(String senha) throws SenhaInvalidaException {
		if (senha == null || senha.isEmpty()) {
			throw new SenhaInvalidaException();
		}
		this.senha = senha;
		this.setHashSenha(BCrypt.hashpw(senha, BCrypt.gensalt()));
	}

	public class SenhaInvalidaException extends Exception {
		private static final long serialVersionUID = -128714569199582797L;

		public SenhaInvalidaException() {
			super("Senha inválida");
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) throws IllegalArgumentException {
		if (!email.matches(EMAIL_REGEX)) {
			throw new IllegalArgumentException("E-mail inválido");
		}
		this.email = email;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Privilegio getPrivilegio() {
		return privilegio;
	}

	public void setPrivilegio(Privilegio privilegio) {
		this.privilegio = privilegio;
	}

	public List<Lancamento> getLancamentos() {
		if (lancamentos == null || lancamentos instanceof PersistentBag) {
			this.setLancamentos(new UsuarioDao().selectLancamentos(this).getLancamentos());
		}
		return lancamentos;
	}

	public void setLancamentos(List<Lancamento> lancamentos) {
		this.lancamentos = lancamentos;
	}

	public Integer getQuantidadeAjudasBomba() {
		return quantidadeAjudasBomba;
	}

	public void setQuantidadeAjudasBomba(Integer quantidadeAjudasBomba) {
		this.quantidadeAjudasBomba = quantidadeAjudasBomba;
	}

	public void incrementarQuantidadeAjudasBomba(int incremento) {
		this.setQuantidadeAjudasBomba(this.quantidadeAjudasBomba + incremento);
	}

	public Integer getQuantidadeAjudasPopular() {
		return quantidadeAjudasPopular;
	}

	public void setQuantidadeAjudasPopular(Integer quantidadeAjudasPopular) {
		this.quantidadeAjudasPopular = quantidadeAjudasPopular;
	}

	public void incrementarQuantidadeAjudasPopular(int incremento) {
		this.setQuantidadeAjudasPopular(this.quantidadeAjudasPopular + incremento);
	}

	public Integer getQuantidadeAjudasBonus() {
		return quantidadeAjudasBonus;
	}

	public void setQuantidadeAjudasBonus(Integer quantidadeAjudasBonus) {
		this.quantidadeAjudasBonus = quantidadeAjudasBonus;
	}

	public void incrementarQuantidadeAjudasBonus(int incremento) {
		this.setQuantidadeAjudasBonus(this.quantidadeAjudasBonus + incremento);
	}

}
