package ir.drax.modal.model;

public class UnsatisfiedParametersException extends Exception {
    public UnsatisfiedParametersException() {
        super("Minimume required parameters not met.\n Check the documentation here: https://github.com/draxdave/androidUtil");
    }
}
