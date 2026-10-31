import exception.RegraNegocioException;
import exception.LivroIndisponivelException;
import model.Biblioteca;
import model.Livro;
import persistence.BibliotecaArquivo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class BibliotecaTest {
    public static void main(String[] args) throws Exception {
        Biblioteca b = new Biblioteca();
        check(b.listarLivros().isEmpty(), "Biblioteca inicialmente vazia");
        check(b.listarDisponiveis().isEmpty(), "Sem livros disponíveis inicialmente");
        check(b.listarHistoricoEmprestimos().isEmpty(), "Histórico inicialmente vazio");
        falha(IllegalArgumentException.class, () -> b.cadastrarLivro(" ", "Autor", 2000));
        falha(IllegalArgumentException.class, () -> b.cadastrarLivro("Livro", "Autor", 0));
        falha(IllegalArgumentException.class, () -> b.cadastrarUsuario("Ana", "invalido"));
        b.cadastrarUsuario("Ana", " ANA@EXEMPLO.COM ");
        falha(RegraNegocioException.class, () -> b.cadastrarUsuario("Outra", "ana@exemplo.com"));
        b.cadastrarLivro("Zeta", "Autora A", 2020);
        b.cadastrarLivro("Alfa", "Autora B", 2000);
        b.cadastrarLivro("Beta", "Autora A", 2000);
        check(b.listarDisponiveis().size() == 3, "Livros novos disponíveis");
        check(b.buscarLivro(" ALFA ").getTitulo().equals("Alfa"), "Busca por título ignora caixa e espaços");
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
        check(b.listarHistoricoEmprestimos().isEmpty(), "Falha não registra histórico");
        falha(RegraNegocioException.class, () -> b.emprestar("Ausente", "ana@exemplo.com"));
        LocalDateTime inicio = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        b.emprestar(" zeta ", "ANA@EXEMPLO.COM");
        LocalDateTime fim = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        falha(LivroIndisponivelException.class, () -> b.emprestar("Zeta", "ana@exemplo.com"));
        check(b.listarHistoricoEmprestimos().size() == 1, "Só registra empréstimo bem-sucedido");
        var registro = b.listarHistoricoEmprestimos().get(0);
        check(registro.titulo().equals("Zeta") && registro.nomeUsuario().equals("Ana")
                && registro.emailUsuario().equals("ana@exemplo.com"), "Histórico identifica livro e usuário");
        check(!registro.dataHora().isBefore(inicio) && !registro.dataHora().isAfter(fim), "Data e horário do empréstimo");
        falha(UnsupportedOperationException.class, () -> b.listarHistoricoEmprestimos().clear());
        check(b.listarDisponiveis().size() == 2 && b.listarDisponiveis().stream()
                .noneMatch(Livro::isEmprestado), "Disponíveis excluem emprestados");
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
            check(carregada.listarHistoricoEmprestimos().equals(b.listarHistoricoEmprestimos()), "Persistência do histórico e horário");
            carregada.devolver("Zeta");
            check(carregada.listarHistoricoEmprestimos().size() == 1, "Devolução preserva histórico");
            check(carregada.listarDisponiveis().size() == 3, "Devolução libera livro");
            carregada.cadastrarUsuario("Bruno", "bruno@exemplo.com");
            carregada.emprestar("Zeta", "bruno@exemplo.com");
            check(carregada.listarHistoricoEmprestimos().size() == 2, "Segundo empréstimo acrescenta registro");
            check(carregada.listarHistoricoEmprestimos().get(0).nomeUsuario().equals("Ana")
                    && carregada.listarHistoricoEmprestimos().get(1).nomeUsuario().equals("Bruno"), "Histórico preserva cada usuário");
            carregada.devolver("Zeta");
            falha(RegraNegocioException.class, () -> carregada.devolver("Zeta"));
            arquivo.salvar(carregada);
            check(arquivo.carregar().listarEmprestados().isEmpty(), "Persistência da devolução");
            check(arquivo.carregar().listarHistoricoEmprestimos().size() == 2, "Histórico permanece após recarga e devoluções");
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
        if (args.length > 0) {
            Biblioteca antiga = new BibliotecaArquivo(Path.of(args[0])).carregar();
            check(antiga.listarLivros().size() == 1 && antiga.listarEmprestados().size() == 1,
                    "Arquivo anterior preserva cadastro e empréstimo ativo");
            check(antiga.listarHistoricoEmprestimos().isEmpty(), "Arquivo anterior não inventa histórico");
            antiga.devolver("Livro antigo");
            antiga.emprestar("Livro antigo", "ana@exemplo.com");
            check(antiga.listarHistoricoEmprestimos().size() == 1, "Arquivo anterior aceita novos registros");
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
