package org.drools.guvnor.client.editors.enumeditor;

import java.util.HashSet;
import java.util.Set;

import org.drools.guvnor.client.resources.ShowcaseImages;

import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class DeleteButtonCell extends ImageResourceCell {

    private static final ImageResource icon = ShowcaseImages.INSTANCE.deleteItemSmall();

    @Override
    public void render(Context context,
                       ImageResource value,
                       SafeHtmlBuilder sb) {
        super.render( context,
                      icon,
                      sb );
    }

    @Override
    public Set<String> getConsumedEvents() {
        Set<String> consumedEvents = super.getConsumedEvents();
        if ( consumedEvents == null ) {
            consumedEvents = new HashSet<String>();
        }
        consumedEvents.add( "click" );
        return consumedEvents;
    }

    @Override
    public void onBrowserEvent(Context context,
                               Element parent,
                               ImageResource value,
                               NativeEvent event,
                               ValueUpdater<ImageResource> valueUpdater) {
        super.onBrowserEvent( context,
                              parent,
                              value,
                              event,
                              valueUpdater );
        if ( event.getType().equals( "click" ) ) {
            onEnterKeyDown( context,
                            parent,
                            value,
                            event,
                            valueUpdater );
        }
    }

    @Override
    protected void onEnterKeyDown(Context context,
                                  Element parent,
                                  ImageResource value,
                                  NativeEvent event,
                                  ValueUpdater<ImageResource> valueUpdater) {
        if ( valueUpdater != null ) {
            valueUpdater.update( value );
        }
    }

}
