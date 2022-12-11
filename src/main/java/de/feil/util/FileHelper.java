package de.feil.util;

import de.feil.model.base.Automaton;
import de.feil.view.dialog.ErrorAlert;

import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class FileHelper {

    private FileHelper() {}

    public static void createFile(String name){
        try {
            Path path = Path.of("automata/" + name + ".java");
            Files.createFile(path);

            List<String> lines = Files.readAllLines(Paths.get("automata/DefaultAutomaton"),
                    StandardCharsets.UTF_8);
            lines.set(3, lines.get(3).replace("DefaultAutomaton", name));
            lines.set(11, lines.get(11).replace("DefaultAutomaton", name));
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            ErrorAlert.show("Ups, da ist was schief gelaufen:\n" + e);
        }
    }

    public static Optional<Automaton> loadAutomaton(String name) {
        try {
            File file = new File("automata/" + name + ".java");

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
