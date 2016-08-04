package org.uberfire.ext.layout.editor.client.widgets;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.infra.DndDataJSONConverter;
import org.uberfire.ext.layout.editor.client.infra.DragComponentEndEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import static org.uberfire.ext.layout.editor.client.infra.CSSClassNameHelper.addClassName;
import static org.uberfire.ext.layout.editor.client.infra.CSSClassNameHelper.removeClassName;
import static org.uberfire.ext.layout.editor.client.infra.HTML5DnDHelper.setDndData;

@Dependent
@Templated
public class LayoutDragComponentWidget implements IsElement {

    @Inject
    @DataField
    private Div dndcomponent;

    @Inject
    @DataField
    Span title;

    private DndDataJSONConverter converter = new DndDataJSONConverter();

    @Inject
    private Event<DragComponentEndEvent> dragComponentEnd;

    public void init( LayoutDragComponent dragComponent ) {
        title.setTextContent( dragComponent.getDragComponentTitle() );
        dndcomponent.setOnmousedown( e -> addClassName( dndcomponent, "le-dndcomponent-selected" ) );
        dndcomponent.setOnmouseup( e -> {
            removeClassName( dndcomponent, "le-dndcomponent-selected" );
            dragComponentEnd.fire( new DragComponentEndEvent() );
        } );
        dndcomponent.setOndragend( e -> {
            removeClassName( dndcomponent, "le-dndcomponent-selected" );
            dragComponentEnd.fire( new DragComponentEndEvent() );
        } );
        dndcomponent.setOndragstart(
                event -> {
                    setDndData( event, converter.generateDragComponentJSON( dragComponent ) );
                } );
    }


}
