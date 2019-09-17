# JogoTrivia

Projeto da disciplina PCS 2019.2, desenvolvido por Gabriel Buzak, Mateus Bandeira e Roberto Oliveira.

## O que é o JogoTrivia?

JogoTrivia é uma plataforma para disputa de quiz multiplayer online, em tempo real, onde os jogadores devem acertar as questões para pontuar. Para jogar, basta estar registrado na plataforma, escolher na lista de próximas partidas disponíveis a que deseja jogar e esperar seu início. Usuários poderão se tornar anfitriões, ou seja, terão a possibilidade de criar uma nova partida e hospedá-la para entreter outros jogadores.

Uma partida é composta por um número variável de questões de múltipla escolha, que aparecem simultaneamente para todos os jogadores, previamente escolhidas ou criadas pelo anfitrião desta partida. Estará disponível, para qualquer possível anfitrião, um repositório com inúmeras perguntas, organizadas por assunto e dificuldade.

Partidas podem ter taxa de inscrição livre ou igual a um valor estipulado pelo anfitrião. O anfitrião pode decidir oferecer premiação aos primeiros colocados, tanto em créditos, como em ajudas para respostas. Dentro deste contexto, a plataforma deve estar preparada também para controlar o fluxo de créditos de cada usuário. 

## Como rodar o projeto

Assumindo que você esteja usando o Eclipse, e já tenha o Apache Tomcat 8.0 na sua máquina.
1. Clonar o repositório no seu workspace
1. No Eclipse: **File -> Import...-> Maven -> Existing Maven Projects -> Next**
1. Selecione o diretório do projeto e clique em **Finish**
1. Clique com o botão direto no projeto, depois em **Properties**
1. Em **Java Build Path -> Libraries**, assegure que o JDK é a versão 1.8
1. Em **Project Facets**, assegure que as seguintes facets estão selecionadas:
    1. **Dynamic Web Module**, versão 3.1
    1. **Java**, versão 1.8
    1. **JavaScript**, versão 1.0
    1. **JAX-RS (REST Web Services)**, versão 2.1
    1. **JPA**, versão 2.1
    - Ao ativar o JPA, o Eclipse pode solicitar mais configurações.
        1. Platform: Generic
        1. JPA Implementation Type: Disable Library Configuration
        1. Connection: none
1. Em ```src/main/java/META-INF```, copie o arquivo ```persistence_model.xml``` e renomeie para ```persistence.xml```
1. Abra o ```persistence.xml``` e edite as informações de conexão do seu banco de dados
```XML
<property name="javax.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/jogotrivia?createDatabaseIfNotExist=true" />
<property name="javax.persistence.jdbc.user" value="username" />
<property name="javax.persistence.jdbc.password" value="password" />
```
9. Clique com o botão direito no projeto, **Run as -> Run on Server**
1. Selecione **Apache Tomcat 8.0 Server**
1. Selecione **Always use this server when running this project**, depois clique em  **Next**
1. Escolha o diretório de instalação do Tomcat, depois clique em **Next**
1. Clique em **Finish**
1. O JogoTrivia deve rodar por padrão no endereço ```localhost:8080/jogotrivia/```. Isso pode ser alterado nas configurações do servidor.
## Diagrama de Classes

![diagrama de classes](https://github.com/mateusbandeiraa/JogoTrivia/raw/master/docs/Diagrama%20de%20classes%20v2.3.png)
