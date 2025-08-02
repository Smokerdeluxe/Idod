package de.gajd.idod.interfaces;

public interface Identifiable<T> {
	boolean isSameContent(T other);
	Object getChangePayload(T other);
}