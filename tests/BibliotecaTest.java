import exception.RegraNegocioException;
import model.Biblioteca;
import model.Livro;
import persistence.BibliotecaArquivo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class BibliotecaTest {
    public static void main(String[] args) throws Exception {
        Biblioteca b = new Biblioteca();
        check(b.listarLivros().isEmpty(), "Biblioteca inicialmente vazia");
        falha(IllegalArgumentException.class, () -> b.cadastrarLivro(" ", "Autor", 2000));
        falha(IllegalArgumentException.class, () -> b.cadastrarLivro("Livro", "Autor", 0));
        falha(IllegalArgumentException.class, () -> b.cadastrarUsuario("Ana", "invalido"));
        b.cadastrarUsuario("Ana", " ANA@EXEMPLO.COM ");
        falha(RegraNegocioException.class, () -> b.cadastrarUsuario("Outra", "ana@exemplo.com"));
        b.cadastrarLivro("Zeta", "Autora A", 2020);
        b.cadastrarLivro("Alfa", "Autora B", 2000);
        b.cadastrarLivro("Beta", "Autora A", 2000);
        falha(RegraNegocioException.class, () -> b.cadastrarLivro(" zeta ", "Outro", 2020));
        check(b.buscarPorAutor("AUTORA A").size() == 2, "Filtro de autor");
        check(b.buscarPorAutor("inexistente").isEmpty(), "Busca sem resultados");
        check(b.agruparPorAutor().get("Autora A").size() == 2, "Agrupamento");
        check(b.ordenarPorTitulo().get(0).getTitulo().equals("Alfa"), "Ordem por título");
        check(b.ordenarPorAno().get(0).getTitulo().equals("Alfa"), "Ordem por ano e desempate");
        check(b.listarLivros().get(0).getTitulo().equals("Zeta"), "Ordenação não altera cadastro");
        falha(UnsupportedOperationException.class, () -> b.listarLivros().clear());
        falha(RegraNegocioException.class, () -> b.emprestar("Zeta", "ausente@exemplo.com"));
        check(!b.buscarLivro("Zeta").isEmprestado(), "Falha não altera empréstimo");
        falha(RegraNegocioException.class, () -> b.emprestar("Ausente", "ana@exemplo.com"));
        b.emprestar(" zeta ", "ANA@EXEMPLO.COM");
        falha(RegraNegocioException.class, () -> b.emprestar("Zeta", "ana@exemplo.com"));
        check(b.listarEmprestados().size() == 1, "Listagem de empréstimos");
        Path pasta = Files.createTempDirectory("biblioteca-test-");
        Path caminho = pasta.resolve("biblioteca.dat");
        Path bloqueio = pasta.resolve("arquivo-em-vez-de-pasta");
        try {
            BibliotecaArquivo arquivo = new BibliotecaArquivo(caminho);
            check(arquivo.carregar().listarUsuarios().isEmpty(), "Arquivo ausente");
            arquivo.salvar(b);
            Biblioteca carregada = arquivo.carregar();
            check(carregada.listarLivros().size() == 3, "Persistência dos livros");
            Livro livro = carregada.buscarLivro("Zeta");
            check(livro.getEmprestadoPara().getEmail().equals("ana@exemplo.com"), "Persistência do vínculo");
            carregada.devolver("Zeta");
            falha(RegraNegocioException.class, () -> carregada.devolver("Zeta"));
            arquivo.salvar(carregada);
            check(arquivo.carregar().listarEmprestados().isEmpty(), "Persistência da devolução");
            byte[] corrompido = {1, 2, 3, 4};
            Files.write(caminho, corrompido);
            falha(IOException.class, arquivo::carregar);
            check(Arrays.equals(Files.readAllBytes(caminho), corrompido), "Arquivo inválido preservado");
            Files.writeString(bloqueio, "bloqueio");
            falha(IOException.class, () -> new BibliotecaArquivo(bloqueio.resolve("dados.dat")).salvar(b));
            check(Files.readString(bloqueio).equals("bloqueio"), "Falha de gravação preserva destino");
        } finally {
            Files.deleteIfExists(caminho);
            Files.deleteIfExists(bloqueio);
            Files.deleteIfExists(pasta);
        }
        System.out.println("OK: cadastro, validações, consultas, ordenação, empréstimo, devolução e persistência.");
    }

    private static void check(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }

    private static void falha(Class<? extends Throwable> tipo, Acao acao) throws Exception {
        try { acao.executar(); }
        catch (Throwable e) {
            if (tipo.isInstance(e)) return;
            throw new AssertionError("Esperava " + tipo.getSimpleName() + ", recebeu " + e, e);
        }
        throw new AssertionError("Esperava " + tipo.getSimpleName());
    }

    @FunctionalInterface private interface Acao { void executar() throws Exception; }
}
