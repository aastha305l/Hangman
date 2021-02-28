public class ImproperInputException extends Exception {

    public ImproperInputException() {
        super("Improper input received in the Text Field.\n" +
                "Text either a symbol or a number.");
    }
}
