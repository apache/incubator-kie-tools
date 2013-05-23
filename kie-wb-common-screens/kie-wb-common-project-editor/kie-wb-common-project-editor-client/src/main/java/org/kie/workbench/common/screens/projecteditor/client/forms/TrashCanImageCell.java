package org.kie.workbench.common.screens.projecteditor.client.forms;


import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import java.util.HashSet;
import java.util.Set;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

public class TrashCanImageCell
        extends ImageResourceCell {

    @Override
    public Set<String> getConsumedEvents() {
        Set<String> consumedEvents = new HashSet<String>();
        consumedEvents.add(CLICK);
        consumedEvents.add(KEYDOWN);
        return consumedEvents;
    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, ImageResource value,
                               NativeEvent event, ValueUpdater<ImageResource> valueUpdater) {
        switch (DOM.eventGetType((Event) event)) {
            case Event.ONCLICK:
                valueUpdater.update(value);
                break;

        }
    }
}
