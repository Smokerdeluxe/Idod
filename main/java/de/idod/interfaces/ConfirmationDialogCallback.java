package de.gajd.idod.interfaces;

import de.gajd.idod.models.Kontakt;

public interface ConfirmationDialogCallback {
    void onDecision(boolean confirmed);
}