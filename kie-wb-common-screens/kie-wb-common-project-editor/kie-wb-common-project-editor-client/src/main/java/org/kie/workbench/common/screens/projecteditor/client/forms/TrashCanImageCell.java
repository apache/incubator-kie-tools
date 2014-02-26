package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

import static com.google.gwt.dom.client.BrowserEvents.*;

public class TrashCanImageCell extends
                               AbstractCell<ImageResource> {

    interface Templates extends SafeHtmlTemplates {

        @SafeHtmlTemplates.Template("<div style=\"{0}\">{1}</div>")
        SafeHtml cell( SafeStyles style,
                       SafeHtml value );
    }

    private static Templates templates = GWT.create( Templates.class );

    @Override
    public Set<String> getConsumedEvents() {
        Set<String> consumedEvents = new HashSet<String>();
        consumedEvents.add( CLICK );
        return consumedEvents;
    }

    @Override
    public void render( Context context,
                        ImageResource value,
                        SafeHtmlBuilder sb ) {
        //Wrap image in a DIV to allow cursor to change
        SafeStyles style = SafeStylesUtils.fromTrustedString( "float:left;cursor:pointer;" );
        SafeHtml rendered = templates.cell( style,
                                            AbstractImagePrototype.create( value ).getSafeHtml() );
        sb.append( rendered );
    }

    @Override
    public void onBrowserEvent( final Cell.Context context,
                                final Element parent,
                                final ImageResource value,
                                final NativeEvent event,
                                final ValueUpdater<ImageResource> valueUpdater ) {
        switch ( DOM.eventGetType( (Event) event ) ) {
            case Event.ONCLICK:
                valueUpdater.update( value );
                break;

        }
    }
}
