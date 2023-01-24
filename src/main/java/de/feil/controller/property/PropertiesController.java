package de.feil.controller.property;

import de.feil.view.dialog.AlertHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesController {

	private static final String PROPERTIES_FILE = "automaton.properties";
	private static PropertiesController propertiesController = null;
	private static final String PROPERTY_LANGUAGE = "language";
	private static final String DEF_LANGUAGE = "de";

	private final Properties properties;

	private PropertiesController() {
		this.properties = new Properties();
		try (FileInputStream propertyFile = new FileInputStream(PropertiesController.PROPERTIES_FILE)) {
			this.properties.load(propertyFile);
		} catch (IOException e) {
			AlertHelper.showError("Fehler beim Laden der automaton.properties-Datei:\n" + e);
		}
	}

	public static PropertiesController getPropertiesController() {
		if (PropertiesController.propertiesController == null) {
			PropertiesController.propertiesController = new PropertiesController();
		}

		return PropertiesController.propertiesController;
	}

	public String getLanguage() {
		String language = this.properties.getProperty(PropertiesController.PROPERTY_LANGUAGE);

		return language == null ? DEF_LANGUAGE : language;
	}
}
