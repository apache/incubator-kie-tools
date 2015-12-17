/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.kie.workbench.common.widgets.decoratedgrid.client.resources.GridResources;

/**
 * A context menu that does nothing
 */
public abstract class AbstractContextMenu extends PopupPanel {

    //Container for items
    private VerticalPanel vp = new VerticalPanel();

    /**
     * Construct a ContextMenu
     * @param eventBus So menu items can raise events
     */
    public AbstractContextMenu() {
        super( true );
        setStyleName( GridResources.INSTANCE.style().contextMenu() );
        add( vp );
    }

    public void addContextMenuItem( ContextMenuItem item ) {
        vp.add( item );
    }

    /**
     * An item in a context menu
     */
    public static class ContextMenuItem extends HorizontalPanel {

        private boolean isEnabled;
        private Label label = new Label();

        public ContextMenuItem( String text,
                                boolean enabled,
                                final ClickHandler handler ) {
            add( label );
            label.setText( text );
            setEnabled( enabled );
            setStyleName( GridResources.INSTANCE.style().contextMenuItem() );

            //Wrap handler to prevent it being invoked when disabled
            addDomHandler( new ClickHandler() {

                public void onClick( ClickEvent event ) {
                    if ( isEnabled ) {
                        handler.onClick( event );
                    }
                }

            },
                           ClickEvent.getType() );
        }

        public void setEnabled( boolean isEnabled ) {
            this.isEnabled = isEnabled;
            if ( isEnabled ) {
                label.setStyleName( GridResources.INSTANCE.style().contextMenuItemEnabled() );
            } else {
                label.setStyleName( GridResources.INSTANCE.style().contextMenuItemDisabled() );
            }
        }

    }

}
