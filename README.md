# Biblioteca Digital

```text
+----------------------------------------------------------------------------------------------+
|                     ____  ___ ____  _     ___ ___ _____ _____ ____    _                      |
|                    | __ )|_ _| __ )| |   |_ _/ _ \_   _| ____/ ___|  / \                     |
|                    |  _ \ | ||  _ \| |    | | | | || | |  _|| |     / _ \                    |
|                    | |_) || || |_) | |___ | | |_| || | | |__| |___ / ___ \                   |
|                    |____/|___|____/|_____|___\___/ |_| |_____\____/_/   \_\                  |
|                                                                                              |
|                                   --< SEJA BEM-VINDO! >--                                    |
|                      SISTEMA DE GERENCIAMENTO DE BIBLIOTECA DIGITAL                          |
|                                ______________________                                        |
|                               /__________/__________/|                                       |
|                              |  LIVROS  | USUARIOS | |                                       |
|                              |__________|__________|/                                        |
+----------------------------------------------------------------------------------------------+
```

Sistema de gerenciamento de biblioteca digital desenvolvido em Java para execução no console. O projeto permite cadastrar livros e usuários, consultar o acervo, ordenar e agrupar livros, registrar empréstimos e realizar devoluções.

O sistema utiliza conceitos de Programação Orientada a Objetos, exceções personalizadas, coleções genéricas, `Comparable`, `Comparator`, Streams, Lambdas, serialização e manipulação de arquivos, com separação de responsabilidades entre domínio, persistência e interface de console.

## Sobre o projeto

A Biblioteca Digital representa um sistema simplificado de gerenciamento de acervo e controle de empréstimos.

Cada livro possui:

* título;
* autor;
* ano de publicação;
* referência ao usuário que está com o livro, quando emprestado.

Cada usuário possui:

* nome;
* e-mail.

O título identifica o livro nas operações de empréstimo e devolução. O e-mail identifica o usuário e não pode se repetir no cadastro.

O sistema permite consultar quais livros estão disponíveis e quais estão emprestados, mostrando também o usuário responsável por cada empréstimo.

Os dados são mantidos em memória durante a execução e salvos em arquivo para serem recuperados na próxima abertura do programa.

---

## Objetivos acadêmicos

O projeto aplica os seguintes conceitos:

* classes e objetos;
* encapsulamento;
* atributos privados e `final`;
* construtores e validação de dados;
* métodos de domínio;
* sobrescrita de `toString()` e `compareTo()`;
* implementação das interfaces `Serializable` e `Comparable<Livro>`;
* Generics e coleções tipadas;
* `ArrayList`, `List`, `Map` e `LinkedHashMap`;
* cópias de listas com `List.copyOf()`;
* ordenação com `Comparable` e `Comparator`;
* Streams e expressões Lambda;
* referências de método;
* filtros com `filter()`;
* buscas com `findFirst()` e `orElseThrow()`;
* verificação de duplicidade com `anyMatch()`;
* agrupamento com `Collectors.groupingBy()`;
* tratamento de exceções e exceção personalizada;
* serialização e desserialização de objetos;
* manipulação de arquivos com `Files` e `Path`;
* `ObjectInputStream` e `ObjectOutputStream`;
* filtro de desserialização com `ObjectInputFilter`;
* records imutáveis e `LocalDateTime` para o histórico;
* fechamento de recursos com `try-with-resources`;
* entrada de dados com `Scanner`;
* laço de repetição e `switch`;
* separação das responsabilidades entre modelo, persistência e interface;
* verificações automatizadas sem dependências externas.

---

# Funcionalidades

## Cadastro de livro

O sistema permite cadastrar um livro informando:

* título;
* autor;
* ano de publicação.

Exemplo:

```text
Título: Dom Casmurro
Autor: Machado de Assis
Ano de publicação: 1899
```

Resultado:

```text
Dom Casmurro | Machado de Assis | 1899 | Disponível
Livro cadastrado.
```

O título e o autor são obrigatórios. O ano deve estar entre `1` e o ano atual. O sistema impede o cadastro de outro livro com o mesmo título, desconsiderando maiúsculas, minúsculas e espaços nas extremidades.

---

## Cadastro de usuário

O cadastro de usuário solicita nome e e-mail.

Exemplo:

```text
Nome: Ana Silva
E-mail: ana@exemplo.com
```

Resultado:

```text
Ana Silva | ana@exemplo.com
Usuário cadastrado.
```

O e-mail é armazenado em minúsculas e deve ser único. Usuários diferentes podem ter o mesmo nome, desde que seus e-mails sejam diferentes.

---

## Listagem de livros

Apresenta todos os livros cadastrados, na ordem de cadastro, incluindo sua disponibilidade.

Exemplo:

```text
Dom Casmurro | Machado de Assis | 1899 | Disponível
O Cortiço | Aluísio Azevedo | 1890 | Emprestado para: Ana Silva | ana@exemplo.com
```

Caso não existam livros cadastrados:

```text
Nenhum registro encontrado.
```

---

## Listagem de usuários

Apresenta o nome e o e-mail de todos os usuários cadastrados.

Exemplo:

```text
Ana Silva | ana@exemplo.com
Bruno Souza | bruno@exemplo.com
```

Quando o cadastro estiver vazio, o sistema informa que nenhum registro foi encontrado.

---

## Busca de livros por autor

A pesquisa aceita o nome completo ou parte do nome do autor, sem diferenciar maiúsculas e minúsculas.

Exemplo:

```text
Autor (ou parte do nome): machado
```

Resultado, considerando as obras cadastradas:

```text
Dom Casmurro | Machado de Assis | 1899 | Disponível
Memórias Póstumas de Brás Cubas | Machado de Assis | 1881 | Disponível
```

Se nenhum livro corresponder à pesquisa, a aplicação apresenta `Nenhum registro encontrado.` e continua em execução.

---

## Ordenação de livros

A opção de ordenação permite escolher:

```text
Ordenar por 1 - Título | 2 - Ano:
```

### Por título

Utiliza a ordem natural definida em `Comparable<Livro>`, sem diferenciar maiúsculas e minúsculas no título. Autor e ano são critérios posteriores de desempate.

### Por ano

Utiliza `Comparator` para ordenar do menor para o maior ano de publicação. Livros do mesmo ano seguem a ordem natural.

Exemplo de ordenação por ano:

```text
Memórias Póstumas de Brás Cubas | Machado de Assis | 1881 | Disponível
O Cortiço | Aluísio Azevedo | 1890 | Disponível
Dom Casmurro | Machado de Assis | 1899 | Disponível
```

As consultas ordenadas não alteram a ordem original do cadastro.

---

## Listagem de livros emprestados

Apresenta somente os livros que possuem um empréstimo ativo, incluindo o usuário que está com cada obra.

Exemplo:

```text
Dom Casmurro | Machado de Assis | 1899 | Emprestado para: Ana Silva | ana@exemplo.com
```

Se não houver empréstimos ativos:

```text
Nenhum registro encontrado.
```

---

## Empréstimo de livro

O empréstimo solicita o título do livro e o e-mail do usuário.

Exemplo:

```text
Título do livro: Dom Casmurro
E-mail do usuário: ana@exemplo.com
```

Resultado:

```text
Empréstimo registrado.
```

Antes de registrar, o sistema verifica:

* se o livro está cadastrado;
* se o usuário está cadastrado;
* se o livro está disponível.

Um livro já emprestado não pode receber outro empréstimo enquanto não for devolvido.

---

## Devolução de livro

A devolução utiliza o título do livro.

Exemplo:

```text
Título do livro: Dom Casmurro
```

Resultado:

```text
Devolução registrada.
```

O sistema verifica se o livro existe e se está emprestado. Ao concluir a operação, remove a associação com o usuário e torna o livro disponível novamente.

---

## Agrupamento de livros por autor

Reúne as obras pela grafia do autor armazenada no cadastro.

Exemplo:

```text
Autor: Machado de Assis
Dom Casmurro | Machado de Assis | 1899 | Disponível
Memórias Póstumas de Brás Cubas | Machado de Assis | 1881 | Disponível
```

A consulta utiliza `Collectors.groupingBy()` e retorna um `Map<String, List<Livro>>`. A ordenação natural é aplicada antes do agrupamento.

Como a chave é o texto armazenado, grafias diferentes, inclusive diferenças de maiúsculas, formam grupos distintos.

---

## Busca de livro por título

A opção `13` solicita o título completo e consulta o cadastro usando Stream e `equalsIgnoreCase()`. Maiúsculas, minúsculas e espaços externos não alteram a busca. Quando não existe correspondência, informa `Erro: Livro não cadastrado.`.

```text
Escolha uma opção: 13
Título do livro: DOM CASMURRO
Dom Casmurro | Machado de Assis | 1899 | Disponível
```

---

## Listagem de livros disponíveis

A opção `14` apresenta somente os livros disponíveis para empréstimo, usando `listarDisponiveis()` e um filtro por `!isEmprestado()`. Um empréstimo retira o livro dessa consulta; a devolução o inclui novamente. Sem resultados, exibe `Nenhum registro encontrado.`.

---

## Histórico de empréstimos

A opção `15` apresenta cada empréstimo bem-sucedido, em ordem de registro, incluindo título, nome e e-mail do usuário e data e horário da retirada. Os horários seguem `America/Sao_Paulo` e são exibidos em `dd/MM/yyyy HH:mm:ss`.

```text
Dom Casmurro | Usuário: Ana Silva | ana@exemplo.com | Empréstimo em: 31/10/2026 10:30:00
```

A data acima é ilustrativa. O registro real é criado automaticamente no momento da retirada. Devolver o livro não apaga o registro; um novo empréstimo acrescenta outro item, mesmo que seja para o mesmo usuário. Tentativas rejeitadas não criam entradas no histórico.

O histórico é salvo junto da biblioteca em `dados/biblioteca.dat`. Arquivos antigos continuam aceitos, com histórico inicialmente vazio: cadastros e empréstimos ativos são preservados, mas não é possível reconstruir retiradas ou horários que nunca foram armazenados.

---

## Busca de usuário por e-mail

Localiza um usuário pelo e-mail cadastrado.

Exemplo:

```text
E-mail: ANA@EXEMPLO.COM
```

Resultado:

```text
Ana Silva | ana@exemplo.com
```

A entrada é normalizada em minúsculas antes da busca. Caso o e-mail não esteja cadastrado, o sistema apresenta uma mensagem de erro.

---

## Encerramento

A opção `10` encerra a aplicação. Caso existam alterações que ainda não foram gravadas, o sistema tenta salvá-las antes de sair.

```text
Programa encerrado.
```

Se a gravação falhar durante essa tentativa, a aplicação informa o problema e mantém o menu aberto para uma nova tentativa.

---

# Menu principal

```text
+----------------------------------------------------------------------------------------------+
|                                   < Menu Principal >                                         |
|                                                                                              |
| >  1. Cadastrar livro                                                                        |
| >  2. Cadastrar usuário                                                                      |
| >  3. Listar livros                                                                          |
| >  4. Listar usuários                                                                        |
| >  5. Buscar livros por autor                                                                |
| >  6. Ordenar livros por título ou ano                                                       |
| >  7. Mostrar livros emprestados e seus usuários                                             |
| >  8. Emprestar livro                                                                        |
| >  9. Devolver livro                                                                         |
| > 10. Sair do sistema                                                                        |
| > 11. Agrupar livros por autor                                                               |
| > 12. Buscar usuário por e-mail                                                              |
| > 13. Buscar livro por título                                                                |
| > 14. Listar livros disponíveis                                                              |
| > 15. Consultar histórico de empréstimos                                                     |
|                                                                                              |
+----------------------------------------------------------------------------------------------+
```

O menu permanece em execução até o usuário selecionar a saída. Uma opção inexistente apresenta:

```text
Opção inválida.
```

---

# Regras de empréstimo e devolução

| Situação | Comportamento |
|---|---|
| Livro e usuário cadastrados, livro disponível | Registra o empréstimo |
| Livro não cadastrado | Rejeita a operação |
| Usuário não cadastrado | Rejeita o empréstimo |
| Livro já emprestado | Rejeita um novo empréstimo |
| Livro existente e emprestado | Permite a devolução |
| Livro existente, mas disponível | Rejeita a devolução |
| Devolução concluída | Remove o vínculo com o usuário e libera o livro |

O estado do empréstimo é derivado da referência ao usuário:

```text
public boolean isEmprestado() {
    return emprestadoPara != null;
}
```

Assim, o sistema mantém uma única informação para representar a disponibilidade e o responsável pelo empréstimo.

---

# Validações implementadas

## Título, autor e nome

Esses campos:

* não podem ser nulos;
* não podem ficar vazios;
* não podem conter somente espaços;
* têm os espaços das extremidades removidos.

Exemplo:

```text
"   Dom Casmurro   " → "Dom Casmurro"
```

## Duplicidade de livros

O título é a identificação do livro nesta versão. O sistema considera equivalentes:

```text
Dom Casmurro
dom casmurro
DOM CASMURRO
```

Tentativas de repetir o título são rejeitadas mesmo quando o autor ou o ano informado é diferente.

```text
Erro: Já existe um livro com esse título.
```

## Ano de publicação

Deve ser um número inteiro entre `1` e o ano atual, obtido por `Year.now().getValue()`.

```text
Erro: Ano deve estar entre 1 e o ano atual.
```

## E-mail

O e-mail:

* é obrigatório;
* tem espaços externos removidos;
* é convertido para minúsculas com `Locale.ROOT`;
* passa por uma validação básica de formato;
* não pode repetir um e-mail já cadastrado.

A validação de formato verifica a estrutura do texto; ela não consulta a existência do endereço.

Exemplos de mensagens:

```text
Erro: Informe um e-mail válido.
Erro: E-mail já cadastrado.
```

## Número inteiro

Campos numéricos rejeitam letras, números decimais e valores fora do intervalo de `int`, solicitando uma nova entrada.

```text
Digite um número inteiro válido.
```

## Consultas sem resultado

Listagens e a busca por autor usam a mensagem `Nenhum registro encontrado.` quando a lista retornada está vazia.

A busca direta de livro ou usuário exige um cadastro correspondente e lança uma exceção de negócio quando ele não existe.

---

# Tratamento de exceções

O projeto utiliza a exceção personalizada:

```text
RegraNegocioException
```

`LivroIndisponivelException` estende `RegraNegocioException` e é lançada especificamente ao tentar emprestar um livro já emprestado. A UI continua tratando essa falha pela classe base.

A classe base também representa situações como:

* título de livro já cadastrado;
* e-mail já cadastrado;
* livro não encontrado;
* usuário não encontrado;
* tentativa de emprestar um livro indisponível;
* tentativa de devolver um livro que não está emprestado.

Na interface, as exceções de negócio e os argumentos inválidos são capturados para mostrar uma mensagem e continuar a execução:

```text
catch (RegraNegocioException | IllegalArgumentException e) {
    System.out.println("Erro: " + e.getMessage());
}
```

Falhas de leitura e gravação são tratadas separadamente como `IOException`.

---

# Estrutura do projeto

```text
Biblioteca_Digital
├── src
│   ├── Main.java
│   ├── exception
│   │   ├── RegraNegocioException.java
│   │   └── LivroIndisponivelException.java
│   ├── model
│   │   ├── Livro.java
│   │   ├── Usuario.java
│   │   ├── Biblioteca.java
│   │   └── RegistroEmprestimo.java
│   ├── persistence
│   │   └── BibliotecaArquivo.java
│   └── ui
│       ├── ConsoleApp.java
│       └── ConsoleInput.java
├── tests
│   └── BibliotecaTest.java
├── Biblioteca_Digital.iml
├── MENU.txt
├── README.md
└── .gitignore
```

Durante o uso são geradas as pastas:

```text
out/                     Classes compiladas
dados/biblioteca.dat     Dados persistidos da biblioteca
```

---

# Arquitetura do projeto

A interface coordena as operações do domínio e a persistência:

```text
             Main
               │
               ▼
           ConsoleApp ───────► ConsoleInput
               │                  │
               │                  ▼
               │               Scanner
               │
               ├─────────────► Biblioteca
               │                  ├── Livro
               │                  └── Usuario
               │
               └─────────────► BibliotecaArquivo
                                  │
                                  ▼
                          dados/biblioteca.dat
```

O domínio concentra as regras de cadastro e empréstimo. A camada de persistência concentra o acesso ao arquivo. O console apresenta as opções, lê dados e informa o resultado das operações.

---

# Classes do projeto

## `Main.java`

Ponto de entrada da aplicação. Define o caminho do arquivo de dados, cria `BibliotecaArquivo` e inicia `ConsoleApp`.

```text
Path arquivo = Path.of(args.length == 0 ? "dados/biblioteca.dat" : args[0]);
new ConsoleApp(new BibliotecaArquivo(arquivo)).executar();
```

O primeiro argumento de execução permite escolher outro arquivo de dados.

---

## `Livro.java`

Representa um livro do acervo.

Principais atributos:

```text
private final String titulo;
private final String autor;
private final int anoPublicacao;
private Usuario emprestadoPara;
```

Responsabilidades:

* validar título, autor e ano;
* manter os dados de identificação imutáveis;
* registrar o usuário que está com o livro;
* impedir empréstimo quando o livro está indisponível;
* impedir devolução quando não há empréstimo;
* informar a disponibilidade com `isEmprestado()`;
* definir a ordem natural com `compareTo()`;
* apresentar os dados com `toString()`.

Implementa:

```text
Serializable, Comparable<Livro>
```

---

## `Usuario.java`

Representa um usuário cadastrado.

Principais atributos:

```text
private final String nome;
private final String email;
```

Responsabilidades:

* validar os campos obrigatórios;
* normalizar o e-mail;
* validar o formato básico do endereço;
* disponibilizar nome e e-mail;
* apresentar os dados com `toString()`.

Implementa `Serializable`. Nome e e-mail são imutáveis após a construção do objeto.

---

## `Biblioteca.java`

Concentra o gerenciamento dos cadastros e as consultas do domínio.

Armazenamento:

```text
private final ArrayList<Livro> livros = new ArrayList<>();
private final ArrayList<Usuario> usuarios = new ArrayList<>();
```

Principais operações:

```text
cadastrarLivro(titulo, autor, ano)
cadastrarUsuario(nome, email)
listarLivros()
listarUsuarios()
buscarLivro(titulo)
buscarUsuario(email)
buscarPorAutor(autor)
ordenarPorTitulo()
ordenarPorAno()
agruparPorAutor()
emprestar(titulo, email)
devolver(titulo)
listarEmprestados()
listarDisponiveis()
listarHistoricoEmprestimos()
validarDadosCarregados()
```

A classe impede duplicidades, localiza livro e usuário antes de emprestar e coordena as consultas com Streams. Também valida os dados recuperados do arquivo.

As listagens de cadastros retornam cópias não modificáveis da estrutura das listas. Os objetos `Livro` continuam representando entidades com operações de empréstimo e devolução.

---

## `RegistroEmprestimo.java`

Record imutável e serializável com `titulo`, `nomeUsuario`, `emailUsuario` e `dataHora` (`LocalDateTime`). Conserva os dados de cada retirada mesmo após a devolução. O construtor valida os campos e `toString()` formata o registro para o console.

---

## `LivroIndisponivelException.java`

Exceção específica para a tentativa de emprestar um livro indisponível. Herda de `RegraNegocioException`, preservando o tratamento de erros existente na interface.

---

## `BibliotecaArquivo.java`

Responsável pela leitura e gravação da biblioteca.

Principais métodos:

```text
Biblioteca carregar() throws IOException
void salvar(Biblioteca biblioteca) throws IOException
```

No carregamento:

* inicia uma biblioteca vazia quando o arquivo não existe;
* aplica um filtro de desserialização;
* verifica o tipo do objeto recuperado;
* revalida os dados do domínio.

Na gravação:

* cria a pasta de destino quando necessário;
* escreve em um arquivo temporário na mesma pasta;
* substitui o arquivo de destino após concluir a escrita;
* usa movimentação atômica quando suportada pelo sistema de arquivos;
* remove o temporário remanescente.

---

## `ConsoleApp.java`

Responsável pelo fluxo principal da aplicação.

A classe:

* carrega os dados ao iniciar;
* exibe o banner e o menu;
* direciona a opção escolhida;
* solicita os dados de cada operação;
* chama os métodos da biblioteca;
* apresenta resultados e mensagens de erro;
* solicita gravação após alterações;
* controla alterações pendentes e o encerramento.

O fluxo principal utiliza um `while` com `switch`. Cadastro de livro, cadastro de usuário, empréstimo e devolução acionam o salvamento.

---

## `ConsoleInput.java`

Responsável pela leitura e conversão das entradas.

Possui os métodos:

```text
String texto(String mensagem)
int inteiro(String mensagem)
```

`texto()` rejeita entradas vazias e remove espaços externos. `inteiro()` repete a leitura quando o valor não pode ser convertido para `int`.

Essa separação mantém o tratamento básico de entrada fora das regras de cadastro e empréstimo.

---

## `RegraNegocioException.java`

Exceção que estende `RuntimeException`. Permite distinguir uma operação rejeitada pelas regras do sistema de uma falha de arquivo ou de conversão de entrada.

---

## `BibliotecaTest.java`

Executa verificações automatizadas por meio de um método `main()`, sem JUnit ou outras bibliotecas externas.

Verifica:

* biblioteca inicialmente vazia;
* campos obrigatórios, ano e e-mail inválidos;
* títulos e e-mails duplicados;
* busca por autor e agrupamento;
* ordenação e desempate;
* preservação da ordem de cadastro;
* listas de consulta não modificáveis;
* livro ou usuário ausente;
* empréstimo duplicado;
* devolução e rejeição de devolução repetida;
* persistência de livros, usuários e vínculo do empréstimo;
* persistência da devolução;
* filtro de disponíveis, histórico preservado e datas das retiradas;
* rejeição com `LivroIndisponivelException`;
* preservação de arquivo corrompido;
* falha de gravação diante de destino inválido.

---

# Streams, Lambdas e Generics

## Coleções tipadas

O sistema utiliza tipos genéricos para representar cada coleção:

```text
List<Livro>
List<Usuario>
Map<String, List<Livro>>
```

A apresentação de listas utiliza `List<?>`, permitindo exibir diferentes tipos de registro sem usar coleções sem tipo.

## Busca por autor

O nome pesquisado é normalizado antes do filtro:

```text
String busca = Usuario.textoObrigatorio(autor, "Autor")
        .toLowerCase(Locale.ROOT);

return livros.stream()
        .filter(l -> l.getAutor().toLowerCase(Locale.ROOT).contains(busca))
        .toList();
```

## Ordenação por título

```text
return livros.stream().sorted().toList();
```

`sorted()` utiliza a implementação de `Comparable<Livro>`.

## Ordenação por ano

```text
return livros.stream()
        .sorted(Comparator.comparingInt(Livro::getAnoPublicacao)
                .thenComparing(Comparator.naturalOrder()))
        .toList();
```

## Agrupamento por autor

```text
return livros.stream()
        .sorted()
        .collect(Collectors.groupingBy(
                Livro::getAutor,
                LinkedHashMap::new,
                Collectors.toList()));
```

## Filtro de empréstimos

```text
return livros.stream().filter(Livro::isEmprestado).toList();
```

---

# Armazenamento e persistência

Durante a execução, os dados ficam nas coleções da classe `Biblioteca`. A persistência serializa a biblioteca inteira, mantendo juntos livros, usuários, os vínculos dos empréstimos atuais e o histórico de retiradas.

Arquivo padrão:

```text
dados/biblioteca.dat
```

O caminho é relativo à pasta de trabalho da execução. Ao executar pela raiz do projeto, o arquivo fica dentro da pasta `dados` do projeto.

## Salvamento automático

Uma alteração bem-sucedida marca o estado como pendente e solicita gravação. Se a gravação funcionar, essa marcação é removida.

Se houver falha, a aplicação avisa que as alterações seguem em memória. Ao selecionar a saída, tenta gravar novamente antes de encerrar.

## Recuperação dos dados

Ao iniciar, a aplicação carrega o arquivo e verifica sua estrutura e seus dados. Quando o arquivo não existe, começa com uma biblioteca vazia.

Se o arquivo estiver corrompido ou for incompatível, o programa informa o problema e interrompe a abertura, preservando o arquivo existente.

## Limites da persistência

* O arquivo serializado é destinado a esta versão da aplicação.
* Não existe banco de dados.
* Não há coordenação entre várias instâncias usando o mesmo arquivo.
* Fechar o processo antes de uma gravação bem-sucedida pode perder alterações pendentes.

---

# Exemplo completo de uso

O exemplo abaixo apresenta uma sequência de operações, omitindo a repetição visual do menu entre as etapas.

## 1. Cadastrar o livro

```text
Escolha uma opção: 1
Título: Dom Casmurro
Autor: Machado de Assis
Ano de publicação: 1899
Dom Casmurro | Machado de Assis | 1899 | Disponível
Livro cadastrado.
```

## 2. Cadastrar o usuário

```text
Escolha uma opção: 2
Nome: Ana Silva
E-mail: ana@exemplo.com
Ana Silva | ana@exemplo.com
Usuário cadastrado.
```

## 3. Registrar o empréstimo

```text
Escolha uma opção: 8
Título do livro: Dom Casmurro
E-mail do usuário: ana@exemplo.com
Empréstimo registrado.
```

## 4. Consultar os empréstimos

```text
Escolha uma opção: 7
Dom Casmurro | Machado de Assis | 1899 | Emprestado para: Ana Silva | ana@exemplo.com
```

## 5. Devolver o livro

```text
Escolha uma opção: 9
Título do livro: Dom Casmurro
Devolução registrada.
```

## 6. Consultar o acervo

```text
Escolha uma opção: 3
Dom Casmurro | Machado de Assis | 1899 | Disponível
```

## 7. Encerrar

```text
Escolha uma opção: 10
Programa encerrado.
```

Os cadastros permanecem no arquivo e são recuperados na próxima abertura usando o mesmo caminho de dados.

---

# Regras de funcionamento

* todo livro possui título, autor e ano de publicação;
* título de livro não pode se repetir no cadastro;
* a comparação de títulos ignora maiúsculas e minúsculas;
* todo usuário possui nome e e-mail;
* nomes iguais são permitidos;
* e-mails são normalizados e não podem se repetir;
* a pesquisa por autor aceita parte do nome;
* livros podem ser ordenados por título ou ano, em ordem crescente;
* agrupamentos usam a grafia armazenada do autor;
* empréstimos exigem livro e usuário cadastrados;
* cada livro possui, no máximo, um empréstimo ativo;
* livros emprestados ficam indisponíveis até a devolução;
* é possível listar somente livros disponíveis;
* cada retirada bem-sucedida acrescenta um registro persistente ao histórico;
* a devolução não apaga retiradas anteriores;
* a devolução remove o vínculo com o usuário;
* listagens de empréstimos mostram livro e usuário responsável;
* falhas de negócio retornam ao menu com mensagem;
* dados são carregados ao iniciar e salvos após alterações;
* o sistema tenta novamente salvar alterações pendentes ao selecionar sair.

---

# Requisitos

Para executar o projeto é necessário:

* JDK 17 ou superior;
* IntelliJ IDEA, Eclipse, NetBeans ou outro ambiente compatível, ou terminal com `javac` e `java` disponíveis;
* terminal configurado para texto UTF-8 para exibir os caracteres em português.

O projeto utiliza recursos como `Stream.toList()`, pattern matching com `instanceof` e a sintaxe de `switch` com setas. Não depende de Maven, Gradle ou bibliotecas externas.

---

# Como executar no IntelliJ IDEA

## 1. Abrir o projeto

Abra o IntelliJ IDEA e selecione a pasta:

```text
Biblioteca_Digital
```

## 2. Configurar o JDK

Acesse:

```text
File → Project Structure → Project
```

Selecione um JDK 17 ou superior.

## 3. Conferir as pastas

Configure `src` como `Sources Root` e, se necessário, `tests` como `Test Sources Root`. O arquivo `Biblioteca_Digital.iml` descreve essas pastas.

## 4. Executar

Abra `src/Main.java` e execute:

```text
public static void main(String[] args)
```

Também é possível clicar com o botão direito em `Main.java` e selecionar `Run 'Main.main()'`.

Use a raiz do projeto como pasta de trabalho para manter `dados/biblioteca.dat` dentro dele.

---

# Como executar pelo terminal

Execute os comandos a partir da raiz do projeto, com o JDK disponível no `PATH`.

## Windows PowerShell

Compilar:

```powershell
$fontes = Get-ChildItem src -Recurse -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -d out $fontes
```

Executar:

```powershell
java '-Dfile.encoding=UTF-8' -cp out Main
```

## Linux ou macOS

```bash
javac -encoding UTF-8 -d out src/Main.java src/exception/*.java src/model/*.java src/persistence/*.java src/ui/*.java
java -Dfile.encoding=UTF-8 -cp out Main
```

## Escolher outro arquivo de dados

No PowerShell:

```powershell
java '-Dfile.encoding=UTF-8' -cp out Main 'dados/outra-biblioteca.dat'
```

O primeiro argumento substitui o caminho padrão. Caminhos com espaços devem ser informados entre aspas.

---

# Como executar as verificações

No PowerShell, a partir da raiz:

```powershell
$fontes = Get-ChildItem src, tests -Recurse -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -Xlint:all -d out $fontes
java '-Dfile.encoding=UTF-8' -cp out BibliotecaTest
```

No Linux ou macOS:

```bash
javac -encoding UTF-8 -Xlint:all -d out src/Main.java src/exception/*.java src/model/*.java src/persistence/*.java src/ui/*.java tests/BibliotecaTest.java
java -Dfile.encoding=UTF-8 -cp out BibliotecaTest
```

Os testes utilizam arquivos temporários e não acessam o arquivo de dados padrão da aplicação. Uma falha gera uma exceção; quando todas as verificações passam, o programa apresenta uma mensagem iniciada por `OK:`.

---

# Status do projeto

Projeto acadêmico funcional para gerenciamento simplificado de uma biblioteca digital pelo console.

O sistema demonstra cadastro e consulta de entidades, regras de empréstimo e devolução, tratamento de erros, processamento de coleções e persistência de objetos em arquivo.

Esta versão inclui o histórico de empréstimos. Não inclui múltiplos exemplares por título, ISBN, multas, prazos de devolução, autenticação ou controle de uso simultâneo por várias instâncias.
