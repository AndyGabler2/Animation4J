package com.andronikus.animation4j.featuredemo.renderratio;

import javax.swing.Action;
import java.beans.PropertyChangeListener;

public abstract class AbstractAction implements Action {
    @Override
    public Object getValue(String key) {
        return null;
    }

    @Override
    public void putValue(String key, Object value) {

    }

    @Override
    public void setEnabled(boolean b) {}

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {}

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {}
}
