package persistence;

import model.Biblioteca;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class BibliotecaArquivo {
    private final Path arquivo;

    public BibliotecaArquivo(Path arquivo) { this.arquivo = arquivo.toAbsolutePath(); }
    public Path getArquivo() { return arquivo; }

    public Biblioteca carregar() throws IOException {
        if (Files.notExists(arquivo)) return new Biblioteca();
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(arquivo))) {
            entrada.setObjectInputFilter(ObjectInputFilter.Config.createFilter(
                    "maxdepth=20;maxrefs=100000;maxbytes=20000000;maxarray=100000;"
                    + "model.Biblioteca;model.Livro;model.Usuario;java.util.ArrayList;java.lang.Object;java.lang.String;!*"));
            Object objeto = entrada.readObject();
            if (!(objeto instanceof Biblioteca biblioteca)) throw new IOException("Conteúdo incompatível.");
            biblioteca.validarDadosCarregados();
            return biblioteca;
        } catch (ClassNotFoundException | RuntimeException e) {
            throw new IOException("Dados inválidos ou incompatíveis.", e);
        }
    }

    public void salvar(Biblioteca biblioteca) throws IOException {
        Files.createDirectories(arquivo.getParent());
        Path temporario = Files.createTempFile(arquivo.getParent(), "biblioteca-", ".tmp");
        try {
            try (ObjectOutputStream saida = new ObjectOutputStream(Files.newOutputStream(temporario))) {
                saida.writeObject(biblioteca);
            }
            try {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporario);
        }
    }
}
