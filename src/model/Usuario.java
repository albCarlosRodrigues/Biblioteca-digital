package model;

import java.io.Serializable;
import java.util.Locale;

public final class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String nome;
    private final String email;

    public Usuario(String nome, String email) {
        this.nome = textoObrigatorio(nome, "Nome");
        this.email = normalizarEmail(email);
        if (!this.email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
    }

    static String textoObrigatorio(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        return texto.strip();
    }

    public static String normalizarEmail(String email) {
        return textoObrigatorio(email, "E-mail").toLowerCase(Locale.ROOT);
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }

    @Override public String toString() { return nome + " | " + email; }
}
