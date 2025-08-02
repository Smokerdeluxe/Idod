package de.gajd.idod.viewModels;

import de.gajd.idod.models.Mitarbeiter;
import de.gajd.idod.repositories.MitarbeiterRepository;

public class MitarbeiterViewModel extends BaseViewModel<Mitarbeiter> {
	
	public MitarbeiterViewModel() {
		super(new MitarbeiterRepository());
	}
}