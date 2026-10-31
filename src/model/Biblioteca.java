package model;

import exception.RegraNegocioException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class Biblioteca implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ArrayList<Livro> livros = new ArrayList<>();
    private final ArrayList<Usuario> usuarios = new ArrayList<>();

    // shortcut: um cadastro por título; adotar ISBN/exemplares se a biblioteca exigir cópias.
    public Livro cadastrarLivro(String titulo, String autor, int ano) {
        Livro livro = new Livro(titulo, autor, ano);
        if (livros.stream().anyMatch(l -> l.getTitulo().equalsIgnoreCase(livro.getTitulo()))) {
            throw new RegraNegocioException("Já existe um livro com esse título.");
        }
        livros.add(livro);
        return livro;
    }

    public Usuario cadastrarUsuario(String nome, String email) {
        Usuario usuario = new Usuario(nome, email);
        if (usuarios.stream().anyMatch(u -> u.getEmail().equals(usuario.getEmail()))) {
            throw new RegraNegocioException("E-mail já cadastrado.");
        }
        usuarios.add(usuario);
        return usuario;
    }

    public List<Livro> listarLivros() { return List.copyOf(livros); }
    public List<Usuario> listarUsuarios() { return List.copyOf(usuarios); }

    public Livro buscarLivro(String titulo) {
        String busca = Usuario.textoObrigatorio(titulo, "Título");
        return livros.stream().filter(l -> l.getTitulo().equalsIgnoreCase(busca))
                .findFirst().orElseThrow(() -> new RegraNegocioException("Livro não cadastrado."));
    }

    public Usuario buscarUsuario(String email) {
        String busca = Usuario.normalizarEmail(email);
        return usuarios.stream().filter(u -> u.getEmail().equals(busca))
                .findFirst().orElseThrow(() -> new RegraNegocioException("Usuário não cadastrado."));
    }

    public List<Livro> buscarPorAutor(String autor) {
        String busca = Usuario.textoObrigatorio(autor, "Autor").toLowerCase(Locale.ROOT);
        return livros.stream().filter(l -> l.getAutor().toLowerCase(Locale.ROOT).contains(busca)).toList();
    }

    public List<Livro> ordenarPorTitulo() { return livros.stream().sorted().toList(); }
    public List<Livro> ordenarPorAno() {
        return livros.stream().sorted(Comparator.comparingInt(Livro::getAnoPublicacao)
                .thenComparing(Comparator.naturalOrder())).toList();
    }

    public Map<String, List<Livro>> agruparPorAutor() {
        return livros.stream().sorted().collect(Collectors.groupingBy(
                Livro::getAutor, LinkedHashMap::new, Collectors.toList()));
    }

    public void emprestar(String titulo, String email) {
        Livro livro = buscarLivro(titulo);
        Usuario usuario = buscarUsuario(email);
        livro.emprestar(usuario);
    }

    public void devolver(String titulo) { buscarLivro(titulo).devolver(); }
    public List<Livro> listarEmprestados() { return livros.stream().filter(Livro::isEmprestado).toList(); }

    public void validarDadosCarregados() {
        if (livros == null || usuarios == null) throw new IllegalArgumentException("Listas ausentes.");
        Biblioteca validada = new Biblioteca();
        for (Usuario u : usuarios) {
            if (u == null) throw new IllegalArgumentException("Usuário nulo.");
            validada.cadastrarUsuario(u.getNome(), u.getEmail());
        }
        for (Livro l : livros) {
            if (l == null) throw new IllegalArgumentException("Livro nulo.");
            validada.cadastrarLivro(l.getTitulo(), l.getAutor(), l.getAnoPublicacao());
            if (l.isEmprestado()) {
                Usuario u = l.getEmprestadoPara();
                Usuario cadastrado = validada.buscarUsuario(u.getEmail());
                if (!cadastrado.getNome().equals(u.getNome())) {
                    throw new IllegalArgumentException("Empréstimo com usuário inconsistente.");
                }
            }
        }
    }
}
