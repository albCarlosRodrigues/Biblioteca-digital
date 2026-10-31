import persistence.BibliotecaArquivo;
import ui.ConsoleApp;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Path arquivo = Path.of(args.length == 0 ? "dados/biblioteca.dat" : args[0]);
        new ConsoleApp(new BibliotecaArquivo(arquivo)).executar();
    }
}
