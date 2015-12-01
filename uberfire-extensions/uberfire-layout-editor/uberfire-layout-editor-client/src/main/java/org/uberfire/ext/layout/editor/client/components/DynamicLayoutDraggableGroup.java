/*
* Copyright 2015 JBoss Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.uberfire.ext.layout.editor.client.components;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.uberfire.ext.layout.editor.client.dnd.DragGridElement;


public class DynamicLayoutDraggableGroup extends Composite {
    private Map<String, DragGridElement> elements = new HashMap<String, DragGridElement>(  );

    interface DynamicLayoutDraggableGroupBinder
            extends
            UiBinder<Widget, DynamicLayoutDraggableGroup> {

    }

    private static DynamicLayoutDraggableGroupBinder uiBinder = GWT.create(DynamicLayoutDraggableGroupBinder.class);

    @UiField
    Anchor anchor;

    @UiField
    PanelCollapse collapse;

    @UiField
    PanelBody content;

    public DynamicLayoutDraggableGroup( ) {
        initWidget(uiBinder.createAndBindUi(this));
        anchor.setDataTargetWidget( collapse );
    }

    public void setName( String name ) {
        anchor.setText( name );
    }

    public void addDraggable( String id, DragGridElement gridElement) {
        content.add( gridElement );
        elements.put( id, gridElement );
    }

    public void removeDraggable( String id ) {
        DragGridElement element = elements.remove( id );
        if ( element != null ) {
            content.remove( element );
        }
    }

}
