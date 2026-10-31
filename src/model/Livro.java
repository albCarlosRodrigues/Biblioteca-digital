package model;

import exception.RegraNegocioException;
import exception.LivroIndisponivelException;
import java.io.Serializable;
import java.time.Year;
import java.util.Comparator;

public final class Livro implements Serializable, Comparable<Livro> {
    private static final long serialVersionUID = 1L;
    private final String titulo;
    private final String autor;
    private final int anoPublicacao;
    private Usuario emprestadoPara;

    public Livro(String titulo, String autor, int anoPublicacao) {
        this.titulo = Usuario.textoObrigatorio(titulo, "Título");
        this.autor = Usuario.textoObrigatorio(autor, "Autor");
        if (anoPublicacao < 1 || anoPublicacao > Year.now().getValue()) {
            throw new IllegalArgumentException("Ano deve estar entre 1 e o ano atual.");
        }
        this.anoPublicacao = anoPublicacao;
    }

    public void emprestar(Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("Usuário é obrigatório.");
        if (isEmprestado()) throw new LivroIndisponivelException();
        emprestadoPara = usuario;
    }

    public void devolver() {
        if (!isEmprestado()) throw new RegraNegocioException("O livro não está emprestado.");
        emprestadoPara = null;
    }

    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getAnoPublicacao() { return anoPublicacao; }
    public boolean isEmprestado() { return emprestadoPara != null; }
    public Usuario getEmprestadoPara() { return emprestadoPara; }

    @Override public int compareTo(Livro outro) {
        return Comparator.comparing(Livro::getTitulo, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Livro::getAutor, String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(Livro::getAnoPublicacao).compare(this, outro);
    }

    @Override public String toString() {
        return titulo + " | " + autor + " | " + anoPublicacao + " | "
                + (isEmprestado() ? "Emprestado para: " + emprestadoPara : "Disponível");
    }
}
