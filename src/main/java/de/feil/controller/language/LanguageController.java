package de.feil.controller.language;

import de.feil.controller.references.ReferenceHandler;
import de.feil.controller.resource.ResourcesController;

import java.util.Locale;

public class LanguageController {

	public LanguageController(ReferenceHandler referenceHandler) {
		referenceHandler.getMainController().getLanguageGroup().selectedToggleProperty()
				.addListener((observable, oldValue, newValue) -> {
					if (newValue == null) {
						return;
					}

					if (newValue == referenceHandler.getMainController().getLanguageEnglishRadioMenuItem()) {
						ResourcesController.getResourceController().setLocale(Locale.ENGLISH);
					} else if (newValue == referenceHandler.getMainController().getLanguageGermanRadioMenuItem()) {
						ResourcesController.getResourceController().setLocale(Locale.GERMAN);
					}
				});
	}
}
