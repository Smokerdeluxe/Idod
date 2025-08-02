package de.gajd.idod.utils;

public class ValidResult {
	public final boolean isValid;
	public final String message;
	
	public ValidResult(boolean isValid, String message) {
		this.isValid = isValid;
		this.message = message;
	}
}