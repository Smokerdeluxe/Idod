package de.gajd.idod.repositories;

import de.gajd.idod.models.Mitarbeiter;

public class MitarbeiterRepository extends BaseRepository<Mitarbeiter> {	
	public MitarbeiterRepository() {
		super("mitarbeiter", Mitarbeiter.class);
	}
}