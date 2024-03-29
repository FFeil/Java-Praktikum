package de.feil.util;

import de.feil.model.base.Automaton;
import de.feil.view.dialog.AlertHelper;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FileHelper {

    private FileHelper() {}

    public static void createFile(String name){
        try {
            File file = new File("automata/" + name + ".java");

            // .java File existiert bereits
            if (file.exists()) {
                return;
            }

            // Namen ersetzen
            List<String> lines = new ArrayList<>(Arrays.asList(Resources.readResourcesFile(
                    "defaultAutomaton/DefaultAutomaton").split(System.lineSeparator())));
            lines.set(3, lines.get(3).replace("DefaultAutomaton", name));
            lines.set(11, lines.get(11).replace("DefaultAutomaton", name));

            // In File schreiben
            Files.write(Path.of(file.getPath()), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            AlertHelper.showError("Beim Erstellen oder Validieren der Java-Datei des Automaten ist ein " +
                    "Fehler aufgetreten:\n" + e);
        }
    }

    public static Optional<Automaton> loadAutomaton(String name, boolean showError) {
        try {
            File file = new File("automata/" + name + ".java");

            // Klasse kompilieren
            JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
            ByteArrayOutputStream error = new ByteArrayOutputStream();
            boolean success = javac.run(null, null, error, file.getPath()) == 0;

            if (!success) {
                if (showError) {
                    AlertHelper.showError(name, error.toString());
                }

                return Optional.empty();
            } else {
                // Klasse laden
                URL classUrl = file.getParentFile().toURI().toURL();
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classUrl});
                Class<?> newAutomatonClass = Class.forName(name, true, classLoader);

                return Optional.of((Automaton) newAutomatonClass.getDeclaredConstructor().newInstance());
            }
        } catch (Exception e) {
            AlertHelper.showError("Beim laden des Automaten " + name + " ist ein Fehler aufgetreten:\n" + e);
        }

        return Optional.empty();
    }
}
