package ui;

import exception.RegraNegocioException;
import model.Biblioteca;
import persistence.BibliotecaArquivo;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class ConsoleApp {
    private final BibliotecaArquivo arquivo;
    private Biblioteca biblioteca;
    private ConsoleInput input;
    private boolean alterado;

    public ConsoleApp(BibliotecaArquivo arquivo) { this.arquivo = arquivo; }

    public void executar() {
        try { biblioteca = arquivo.carregar(); }
        catch (IOException e) {
            System.out.println("Não foi possível carregar " + arquivo.getArquivo() + ": " + e.getMessage());
            System.out.println("Arquivo preservado. Corrija o problema antes de abrir novamente.");
            return;
        }
        try (Scanner scanner = new Scanner(System.in, "UTF-8")) {
            input = new ConsoleInput(scanner);
            exibirBanner();
            boolean executando = true;
            while (executando) {
                exibirMenu();
                try {
                    int opcao = input.inteiro("Escolha uma opção: ");
                    switch (opcao) {
                        case 1 -> cadastrarLivro();
                        case 2 -> cadastrarUsuario();
                        case 3 -> mostrar(biblioteca.listarLivros());
                        case 4 -> mostrar(biblioteca.listarUsuarios());
                        case 5 -> mostrar(biblioteca.buscarPorAutor(input.texto("Autor (ou parte do nome): ")));
                        case 6 -> ordenarLivros();
                        case 7 -> mostrar(biblioteca.listarEmprestados());
                        case 8 -> emprestar();
                        case 9 -> devolver();
                        case 10 -> executando = !salvar();
                        case 11 -> biblioteca.agruparPorAutor().forEach((autor, livros) -> {
                            System.out.println("Autor: " + autor); mostrar(livros);
                        });
                        case 12 -> System.out.println(biblioteca.buscarUsuario(input.texto("E-mail: ")));
                        default -> System.out.println("Opção inválida.");
                    }
                } catch (RegraNegocioException | IllegalArgumentException e) {
                    System.out.println("Erro: " + e.getMessage());
                } catch (NoSuchElementException e) {
                    salvar();
                    executando = false;
                }
                System.out.println();
            }
            System.out.println("Programa encerrado.");
        }
    }

    private void cadastrarLivro() {
        String titulo = input.texto("Título: ");
        String autor = input.texto("Autor: ");
        int ano = input.inteiro("Ano de publicação: ");
        System.out.println(biblioteca.cadastrarLivro(titulo, autor, ano));
        registrarAlteracao("Livro cadastrado.");
    }

    private void cadastrarUsuario() {
        System.out.println(biblioteca.cadastrarUsuario(input.texto("Nome: "), input.texto("E-mail: ")));
        registrarAlteracao("Usuário cadastrado.");
    }

    private void emprestar() {
        biblioteca.emprestar(input.texto("Título do livro: "), input.texto("E-mail do usuário: "));
        registrarAlteracao("Empréstimo registrado.");
    }

    private void devolver() {
        biblioteca.devolver(input.texto("Título do livro: "));
        registrarAlteracao("Devolução registrada.");
    }

    private void ordenarLivros() {
        int criterio = input.inteiro("Ordenar por 1 - Título | 2 - Ano: ");
        switch (criterio) {
            case 1 -> mostrar(biblioteca.ordenarPorTitulo());
            case 2 -> mostrar(biblioteca.ordenarPorAno());
            default -> System.out.println("Critério inválido.");
        }
    }

    private void mostrar(List<?> itens) {
        if (itens.isEmpty()) System.out.println("Nenhum registro encontrado.");
        else itens.forEach(System.out::println);
    }

    private void registrarAlteracao(String mensagem) {
        alterado = true;
        System.out.println(mensagem);
        salvar();
    }

    private boolean salvar() {
        if (!alterado) return true;
        try {
            arquivo.salvar(biblioteca);
            alterado = false;
            return true;
        } catch (IOException e) {
            System.out.println("Falha ao salvar: " + e.getMessage());
            System.out.println("Alterações seguem em memória. Tente sair novamente para repetir a gravação.");
            return false;
        }
    }

    private static void borda() { System.out.println("+" + "-".repeat(94) + "+"); }
    private static void linha(String texto) { System.out.printf("| %-92s |%n", texto); }

    public static void exibirBanner() {
        borda();
        linha("                    ____  ___ ____  _     ___ ___ _____ _____ ____    _");
        linha("                   | __ )|_ _| __ )| |   |_ _/ _ \\_   _| ____/ ___|  / \\");
        linha("                   |  _ \\ | ||  _ \\| |    | | | | || | |  _|| |     / _ \\");
        linha("                   | |_) || || |_) | |___ | | |_| || | | |__| |___ / ___ \\");
        linha("                   |____/|___|____/|_____|___\\___/ |_| |_____\\____/_/   \\_\\");
        linha("");
        linha("                                  --< SEJA BEM-VINDO! >--");
        linha("                     SISTEMA DE GERENCIAMENTO DE BIBLIOTECA DIGITAL");
        linha("                               ______________________");
        linha("                              /__________/__________/|");
        linha("                             |  LIVROS  | USUARIOS | |");
        linha("                             |__________|__________|/");
        borda();
    }

    public static void exibirMenu() {
        borda();
        linha("                                  < Menu Principal >");
        linha("");
        linha(">  1. Cadastrar livro");
        linha(">  2. Cadastrar usuário");
        linha(">  3. Listar livros");
        linha(">  4. Listar usuários");
        linha(">  5. Buscar livros por autor");
        linha(">  6. Ordenar livros por título ou ano");
        linha(">  7. Mostrar livros emprestados e seus usuários");
        linha(">  8. Emprestar livro");
        linha(">  9. Devolver livro");
        linha("> 10. Sair do sistema");
        linha("> 11. Agrupar livros por autor");
        linha("> 12. Buscar usuário por e-mail");
        linha("");
        borda();
    }
}
