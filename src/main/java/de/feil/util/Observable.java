package de.feil.util;

import java.util.ArrayList;

public class Observable extends ArrayList<Observer> {

	protected void notifyObserver() {
		for (Observer obs : this) {
			obs.update();
		}
	}
}
