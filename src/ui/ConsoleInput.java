package ui;

import java.util.Scanner;
import java.util.NoSuchElementException;

public final class ConsoleInput {
    private final Scanner scanner;
    public ConsoleInput(Scanner scanner) { this.scanner = scanner; }

    public String texto(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            if (!scanner.hasNextLine()) throw new NoSuchElementException("Entrada encerrada.");
            String texto = scanner.nextLine().strip();
            if (!texto.isEmpty()) return texto;
            System.out.println("Preencha o campo.");
        }
    }

    public int inteiro(String mensagem) {
        while (true) {
            try { return Integer.parseInt(texto(mensagem)); }
            catch (NumberFormatException e) { System.out.println("Digite um número inteiro válido."); }
        }
    }
}
