/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.client.widget;

import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A utility class to calculate the width of a Cell
 */
public class WidthCalculator<V> {

    //Hidden DIV used to hold the cell content
    private static Element div = DOM.createDiv();

    {
        DOM.appendChild( RootPanel.getBodyElement(),
                         div );
        div.getStyle().setPosition( Position.ABSOLUTE );
        div.getStyle().setVisibility( Visibility.HIDDEN );
        div.getStyle().setProperty( "width",
                                    "auto" );
        div.getStyle().setProperty( "height",
                                    "auto" );
        div.getStyle().setPadding( 1,
                                   Unit.PX );
        div.getStyle().setMargin( 2,
                                  Unit.PX );
    }

    private Cell<V>        cell;

    private Integer        minWidth;

    /**
     * Constructor
     * 
     * @param cell
     *            The Cell used to render content
     */
    public WidthCalculator( Cell<V> cell ) {
        this.cell = cell;
    }

    /**
     * Set the minimum width
     * 
     * @param minWidth
     */
    public void setMinimumWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    /**
     * Get the widest Cell from a list of content
     * 
     * @param values
     * @return
     */
    public int getMaximumElementWidth(List< ? extends V> values) {
        int maximumWidth = 0;
        for ( V value : values ) {
            int w = getElementWidth( value );
            if ( w > maximumWidth ) {
                maximumWidth = w;
            }
        }
        if ( minWidth == null ) {
            return maximumWidth;
        }
        return minWidth > maximumWidth ? minWidth : maximumWidth;
    }

    /**
     * Get the width of a single item
     * 
     * @param value
     * @return
     */
    public int getElementWidth(V value) {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();
        cell.render( null,
                      value,
                      sb );
        div.setInnerHTML( sb.toSafeHtml().asString() );
        int width = div.getClientWidth();
        if ( minWidth == null ) {
            return div.getClientWidth();
        }
        return minWidth > width ? minWidth : width;
    }

}
