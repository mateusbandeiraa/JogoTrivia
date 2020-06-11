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
public class User {

	public static final String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonView({ ViewUsuario.Proprio.class, ViewUsuario.Proprio.Parametros.Atualizar.class })
	private int id;

	@Column(nullable = false)
	@JsonView({ ViewUsuario.Proprio.class, ViewUsuario.Proprio.Parametros.Cadastrar.class,
			ViewUsuario.Proprio.Parametros.Atualizar.class })
	private String name;

	@JsonView({ ViewUsuario.Proprio.Parametros.Cadastrar.class,
			ViewUsuario.Proprio.Parametros.Autenticar.class })
	private transient String password;

	@Column(nullable = false)
	private String passwordHash;

	@Column(unique = true, nullable = false)
	@JsonView({ ViewUsuario.Proprio.class, ViewUsuario.Proprio.Parametros.Cadastrar.class,
			ViewUsuario.Proprio.Parametros.Atualizar.class,
			ViewUsuario.Proprio.Parametros.Autenticar.class })
	private String email;

	@Column(nullable = false, columnDefinition = "datetime default current_timestamp")
	@JsonView({ ViewUsuario.Proprio.class })
	private Date registerDate;

	@Column(nullable = false, columnDefinition = "boolean default true")
	@JsonView({ ViewUsuario.Proprio.class, ViewUsuario.Moderador.Parametros.Atualizar.class })
	private Boolean active = true;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false,
			columnDefinition = "ENUM('USUARIO', 'MODERADOR', 'ANFITRIAO') DEFAULT 'USUARIO'")
	@JsonView({ ViewUsuario.Proprio.class, ViewUsuario.Moderador.Parametros.Atualizar.class })
	private Privilegio authorization;

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

	public User(String name, String passwordHash, String email, Date registerDate, boolean active) {
		this(name, email, registerDate, active);
		setPasswordHash(passwordHash);
	}

	public User(String name, String email, Date registerDate, boolean active) {
		this();
		setName(name);
		setEmail(email);
		setRegisterDate(registerDate);
		setActive(active);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			User usuario = (User) obj;
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
	 * @param password
	 *            String contendo a senha em plain-text
	 * @return
	 */
	public TokenAutenticacao authenticate(String password) {
		if (BCrypt.checkpw(password, passwordHash)) {
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
	 * @param authorization
	 * @return
	 */
	private Class<? extends ViewUsuario> getView(Privilegio authorization) {
		Class<? extends ViewUsuario> viewClass;
		switch (authorization) {
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
	public String serialize(Privilegio privilegio) throws JsonProcessingException {
		Class<? extends ViewUsuario> viewClass = this.getView(privilegio);
		return CustomJsonProvider.writerWithView(viewClass).writeValueAsString(this);
	}

	/**
	 * Serializa este Usuario, apenas incluindo os campos incluídos na view
	 * apropriada para o privilégio do Usuário passado.
	 * 
	 * @param requirer
	 *            Usuario do qual o privilégio será base para decidir qual view
	 *            deve
	 *            ser usada
	 * @return
	 *         String JSON contendo os campos apropriados
	 * @throws JsonProcessingException
	 */
	public String serialize(User requirer) throws JsonProcessingException {
		return this.serialize(requirer.getAuthorization());
	}

	/**
	 * Copia os atributos não nulos e default para este objeto.
	 * 
	 * @param origin
	 *            Objeto usuário de onde os atributos serão copiados.
	 * @throws InvalidPasswordException
	 */
	public void copyNonDefaultAttributes(User origin) throws InvalidPasswordException {
		if (origin.getId() != 0) {
			this.setId(origin.getId());
		}

		if (StringUtils.isNotBlank(origin.getName())) {
			this.setName(origin.getName());
		}

		if (StringUtils.isNotBlank(origin.getPassword())) {
			origin.generatePasswordHash(origin.getPassword());
		}

		if (StringUtils.isNotBlank(origin.getEmail())) {
			this.setEmail(origin.getEmail());
		}

		if (origin.getActive() != null) {
			this.setActive(origin.getActive());
		}

		if (origin.getAuthorization() != null) {
			this.setAuthorization(origin.getAuthorization());
		}

		if (origin.getQuantidadeAjudasBomba() != 0) {
			this.setQuantidadeAjudasBomba(origin.getQuantidadeAjudasBomba());
		}

		if (origin.getQuantidadeAjudasBonus() != 0) {
			this.setQuantidadeAjudasBonus(origin.getQuantidadeAjudasBonus());
		}

		if (origin.getQuantidadeAjudasPopular() != 0) {
			this.setQuantidadeAjudasPopular(origin.getQuantidadeAjudasPopular());
		}
	}

	// @XmlElement
	@JsonView(ViewAutenticado.class)
	public BigDecimal getCredits() {
		BigDecimal saldo = BigDecimal.ZERO;

		for (Lancamento lancamento : this.getLancamentos()) {
			saldo = saldo.add(lancamento.getValorEfetivo());
		}

		return saldo.setScale(2);
	}

	public User() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String nome) throws IllegalArgumentException {
		if (nome == null || nome.isEmpty()) {
			throw new IllegalArgumentException("Nome inválido");
		}
		this.name = nome;
	}

	public String getPassword() {
		return password;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public void generatePasswordHash(String password) throws InvalidPasswordException {
		if (password == null || password.isEmpty()) {
			throw new InvalidPasswordException();
		}
		this.password = password;
		this.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
	}

	public class InvalidPasswordException extends Exception {
		private static final long serialVersionUID = -128714569199582797L;

		public InvalidPasswordException() {
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

	public Date getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Privilegio getAuthorization() {
		return authorization;
	}

	public void setAuthorization(Privilegio authorization) {
		this.authorization = authorization;
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
