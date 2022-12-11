package de.feil.util;

import de.feil.model.base.Automaton;
import de.feil.view.dialog.ErrorAlert;

import javax.tools.ToolProvider;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Optional;

public class FileLoader {

    private FileLoader() {}

    public static Optional<Automaton> loadAutomaton(String name, File file) {
        try {
            // Klasse kompilieren
            ToolProvider.getSystemJavaCompiler().run(null, null, null, file.getAbsolutePath());

            // Klasse laden
            URL classUrl = file.getParentFile().toURI().toURL();
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classUrl});
            Class<?> newAutomatonClass = Class.forName(name, true, classLoader);

            return Optional.of((Automaton) newAutomatonClass.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            ErrorAlert.show("Ups, da ist was schief gelaufen:\n" + e);
        }
        return Optional.empty();
    }
}
