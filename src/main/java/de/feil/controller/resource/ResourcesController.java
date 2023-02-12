package de.feil.controller.resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import de.feil.controller.property.PropertiesController;
import de.feil.view.dialog.AlertHelper;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ResourcesController {

	private static final String PROPERTIES_FILE = "automaton.properties";
	private static final ArrayList<String> PROPERTIES_FILE_DEFAULT_CONTENT = new ArrayList<>(List.of("language=de"));
	private static final String PROPERTIES_CLASS = "i18n_resources.text";

	private static ResourcesController resourcesController = null;
	private Locale locale;
	private ResourceBundle bundle;
	private final ObservableResourceFactory resourceFactory;

	public static ResourcesController getResourceController() {
		// automaton.properties existiert nicht
		Path path = Path.of(PROPERTIES_FILE);
		if (!Files.exists(path)) {
			try {
				Files.createFile(path);
				Files.write(path, PROPERTIES_FILE_DEFAULT_CONTENT, StandardCharsets.UTF_8);
			} catch (IOException e) {
				AlertHelper.showError("Fehler beim Erstellen der automaton.properties-Datei:\n" + e);
			}
		}

		if (ResourcesController.resourcesController == null) {
			ResourcesController.resourcesController = new ResourcesController();
		}

		return ResourcesController.resourcesController;
	}

	private ResourcesController() {
		try {
			String localLanguage = PropertiesController.getPropertiesController().getLanguage();

			if (localLanguage == null) {
				this.locale = Locale.getDefault();
			} else {
				this.locale = new Locale.Builder().setLanguage(localLanguage).build();
			}
		} catch (Throwable e) {
			this.locale = Locale.getDefault();
		}

		Locale.setDefault(this.locale);
		this.bundle = ResourceBundle.getBundle(PROPERTIES_CLASS, this.locale);
		this.resourceFactory = new ObservableResourceFactory();
		this.resourceFactory.setResources(this.bundle);
	}

	public String getI18nValue(String key) {
		return this.bundle.getString(key);
	}

	public StringBinding i18n(String value) {
		return this.resourceFactory.getStringBinding(value);
	}

	public Locale getLocale() {
		return this.locale;
	}

	public void setLocale(Locale loc) {
		this.locale = loc;
		Locale.setDefault(this.locale);
		this.bundle = ResourceBundle.getBundle(PROPERTIES_CLASS, this.locale);

		this.resourceFactory.setResources(this.bundle);
	}
}

class ObservableResourceFactory {
	private final ObjectProperty<ResourceBundle> resources = new SimpleObjectProperty<>();

	public ObjectProperty<ResourceBundle> resourcesProperty() {
		return resources;
	}

	public final ResourceBundle getResources() {
		return resourcesProperty().get();
	}

	public final void setResources(ResourceBundle resources) {
		resourcesProperty().set(resources);
	}

	public StringBinding getStringBinding(String key) {
		return new StringBinding() {
			{
				bind(resourcesProperty());
			}

			@Override
			public String computeValue() {
				return getResources().getString(key);
			}
		};
	}
}
