package de.gajd.idod.viewModels;

import de.gajd.idod.models.Kontakt;
import de.gajd.idod.repositories.KontaktRepository;

public class KontaktViewModel extends BaseViewModel<Kontakt> {
	
	public KontaktViewModel() {
		super(new KontaktRepository());
	}
}