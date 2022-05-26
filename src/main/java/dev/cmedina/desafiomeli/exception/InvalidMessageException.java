package dev.cmedina.desafiomeli.exception;

public class InvalidMessageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2581334030944351050L;

	public InvalidMessageException() {
		super("Não foi possível determinar a mensagem.");
	}

	
}
