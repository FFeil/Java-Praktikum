package de.feil.view.panel;

import de.feil.controller.references.ReferenceHandler;
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
        methods = new ArrayList<>();

        for (Method method : referenceHandler.getAutomaton().getClass().getDeclaredMethods()) {
            if (method.getParameterCount() == 2
                    && method.getParameterTypes()[0].equals(int.class)
                    && method.getParameterTypes()[1].equals(int.class)
                    && !Modifier.isAbstract(method.getModifiers())
                    && !Modifier.isStatic(method.getModifiers())
                    && method.isAnnotationPresent(Callable.class)) {
                method.setAccessible(true);
                getItems().add(new MenuItem(method.getReturnType().getName() + " "
                        + method.getName() + "(int row, int column)"));
                methods.add(method);
            }
        }
    }

    public List<Method> getMethods() {
        return methods;
    }
}
