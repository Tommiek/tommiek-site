package tommiek.sitegenerator;

public class GenerateException extends Exception {
	private static final long serialVersionUID = 1L;

	public GenerateException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenerateException(String message) {
		super(message);
	}

}
