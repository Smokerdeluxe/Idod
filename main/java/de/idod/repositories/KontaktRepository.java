package de.gajd.idod.repositories;

import de.gajd.idod.models.Kontakt;

public class KontaktRepository extends BaseRepository<Kontakt> {
	public KontaktRepository() {
		super("kontakte", Kontakt.class);
	}
}