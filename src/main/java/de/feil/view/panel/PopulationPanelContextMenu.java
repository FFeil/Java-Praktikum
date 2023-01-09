package de.feil.view.panel;

import de.feil.controller.references.ReferenceHandler;
import de.feil.model.base.Automaton;
import de.feil.model.base.Callable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class PopulationPanelContextMenu extends ContextMenu {

    private final List<Method> methods;

    public PopulationPanelContextMenu(ReferenceHandler referenceHandler) {
        methods = getValidMethods(referenceHandler.getAutomaton());

        for (Method method : methods) {
            getItems().add(new MenuItem(method.getName()));
        }
    }

    public List<Method> getMethods() {
        return methods;
    }

    private List<Method> getValidMethods(Automaton automaton) {
        List<Method> result = new ArrayList<>();
        for (Method method : automaton.getClass().getDeclaredMethods()) {
            if (method.getParameterCount() == 2
                    && method.getParameterTypes()[0].equals(int.class)
                    && method.getParameterTypes()[1].equals(int.class)
                    && !Modifier.isAbstract(method.getModifiers())
                    && !Modifier.isStatic(method.getModifiers())
                    && method.isAnnotationPresent(Callable.class)) {
                method.setAccessible(true);
                result.add(method);
            }
        }

        return result;
    }
}
