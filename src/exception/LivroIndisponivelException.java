package exception;

public final class LivroIndisponivelException extends RegraNegocioException {
    private static final long serialVersionUID = 1L;

    public LivroIndisponivelException() {
        super("Livro indisponível: já está emprestado.");
    }
}
