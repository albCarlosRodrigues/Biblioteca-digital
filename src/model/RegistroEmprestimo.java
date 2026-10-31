package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public record RegistroEmprestimo(String titulo, String nomeUsuario, String emailUsuario,
                                LocalDateTime dataHora) implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public RegistroEmprestimo {
        titulo = Usuario.textoObrigatorio(titulo, "Título");
        // Reutiliza a validação do cadastro e conserva os dados de quem retirou o livro.
        Usuario usuario = new Usuario(nomeUsuario, emailUsuario);
        nomeUsuario = usuario.getNome();
        emailUsuario = usuario.getEmail();
        Objects.requireNonNull(dataHora, "Data e horário são obrigatórios.");
    }

    @Override public String toString() {
        return titulo + " | Usuário: " + nomeUsuario + " | " + emailUsuario
                + " | Empréstimo em: " + dataHora.format(FORMATO);
    }
}
