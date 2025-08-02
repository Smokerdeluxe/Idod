package de.gajd.idod.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.gajd.idod.interfaces.Identifiable;
import java.util.Objects;

public class Mitarbeiter implements Identifiable<Mitarbeiter> {
	private String key;
	private String name;
	private String nummer;
	private String email;
	private String funktion;
	private String status;
	private String profi;
	
	// Standardkonstruktor für Firebase
	public Mitarbeiter() {}
	
	// Erweiterter Konstruktor mit optionalem Key
	public Mitarbeiter(@Nullable String key, @NonNull String name, @NonNull String nummer,
	@NonNull String email, @NonNull String funktion, @NonNull String status, @NonNull String profi) {
		this.key = key;
		this.name = name;
		this.nummer = nummer;
		this.email = email;
		this.funktion = funktion;
		this.status = status;
		this.profi = profi;
	}
	
	// Getter und Setter
	public String getKey() { return key; }
	public void setKey(String key) { this.key = key; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getPhoneNumber() { return nummer; }
	public void setPhoneNumber(String nummer) { this.nummer = nummer; }
	
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
		
	public String getFunktion() { return funktion; }
	public void setFunktion(String funktion) { this.funktion = funktion; }
	
	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
	
	public String getProfi() { return profi; }
	public void setProfi(String profi) { this.profi = profi; }
		
	// equals() und hashCode() für sauberen Vergleich und DiffUtil
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Mitarbeiter that = (Mitarbeiter) obj;
		return Objects.equals(key, that.key);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(key);
	}
	
	// toString() für Debugging
	@Override
	public String toString() {
		return "Mitarbeiter{" +
		"key='" + key + '\'' +
		", name='" + name + '\'' +
		", nummer='" + nummer + '\'' +
		", email='" + email + '\'' +
		", funktion='" + funktion + '\'' +
		", status='" + status + '\'' +
		", profi='" + profi + '\'' +
		'}';
	}
	
	// Implementierung von Identifiable
	@Override
	public boolean isSameContent(Mitarbeiter other) {
		if(other == null) return false;
		return Objects.equals(name, other.name) &&
		Objects.equals(nummer, other.nummer) &&
		Objects.equals(email, other.email) &&
		Objects.equals(funktion, other.funktion) &&
		Objects.equals(status, other.status) &&
		Objects.equals(profi, other.profi);
	}
	
	@Override
	public Object getChangePayload(Mitarbeiter other) {
		return null; // Falls nötig, hier differenzierte Änderungen zurückgeben
	}
}