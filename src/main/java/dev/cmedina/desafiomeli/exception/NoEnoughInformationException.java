package dev.cmedina.desafiomeli.exception;

public class NoEnoughInformationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NoEnoughInformationException() {
		super("Não há informação suficiente");
	}

}
