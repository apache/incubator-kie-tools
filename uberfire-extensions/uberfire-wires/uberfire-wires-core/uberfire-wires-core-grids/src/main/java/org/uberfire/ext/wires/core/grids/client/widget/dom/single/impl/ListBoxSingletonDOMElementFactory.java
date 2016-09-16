/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.core.grids.client.widget.dom.single.impl;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.ListBox;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.ListBoxDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

/**
 * A DOMElement Factory for single-instance ListBoxes.
 */
public class ListBoxSingletonDOMElementFactory extends BaseSingletonDOMElementFactory<String, ListBox, ListBoxDOMElement> {

    public ListBoxSingletonDOMElementFactory( final GridLienzoPanel gridPanel,
                                              final GridLayer gridLayer,
                                              final GridWidget gridWidget ) {
        super( gridPanel,
               gridLayer,
               gridWidget );
    }

    @Override
    public ListBox createWidget() {
        return new ListBox() {{
            addKeyDownHandler( ( e ) -> e.stopPropagation() );
            addMouseDownHandler( ( e ) -> e.stopPropagation() );
        }};
    }

    @Override
    public ListBoxDOMElement createDomElement( final GridLayer gridLayer,
                                               final GridWidget gridWidget,
                                               final GridBodyCellRenderContext context ) {
        this.widget = createWidget();
        this.e = new ListBoxDOMElement( widget,
                                        gridLayer,
                                        gridWidget );
        widget.addBlurHandler( new BlurHandler() {
            @Override
            public void onBlur( final BlurEvent event ) {
                destroyResources();
                gridLayer.batch();
                gridPanel.setFocus( true );
            }
        } );
        return e;
    }

    @Override
    protected String getValue() {
        if ( widget != null ) {
            return widget.getValue( widget.getSelectedIndex() );
        }
        return null;
    }

}
