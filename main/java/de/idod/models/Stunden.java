package de.gajd.idod.models;

public class Stunden {
	
	private String mitarbeiter;
	private String adresse;
	private String inZeit;
	private String ausZeit;	
	private boolean isDrin;
	
	// Leerer Konstruktor für Firebase
	public Stunden() {}
	
	public Stunden(String mitatrbeiter, String adresse, String inZeit, String ausZeit, boolean isDrin) {
		this.mitarbeiter = mitatrbeiter;
		this.adresse = adresse;
		this.inZeit = inZeit;
		this.ausZeit = ausZeit;
		this.isDrin = isDrin;
	}
	
	// Getter und Setter
	public String getMitarbeiter() { return mitarbeiter; }
	public void setMitarbeiter(String mitarbeiter) { this.mitarbeiter = mitarbeiter; }
	public String getAdresse() { return adresse; }
	public void setAdresse(String adresse) { this.adresse = adresse; }
	public String getInZeit() { return inZeit; }
	public void setInZeit(String inZeit) { this.inZeit = inZeit; }
	public String getAusZeit() { return ausZeit; }
	public void setAusZeit(String ausZeit) { this.ausZeit = ausZeit; }
	public boolean getDrin() { return isDrin; }
	public void setDrin(boolean isDrin) { isDrin = isDrin; }
}