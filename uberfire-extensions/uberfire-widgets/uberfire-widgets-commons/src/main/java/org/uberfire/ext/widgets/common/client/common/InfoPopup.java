/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.common.client.common;

import com.google.gwt.dom.client.Element;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Popover;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.gwtbootstrap3.client.ui.constants.Trigger;

/**
 * This is handy for in-place context help.
 */
public class InfoPopup extends Popover {

    public InfoPopup( final String text ) {
        configure();
        setContent( text );
    }

    public InfoPopup( final String heading,
                      final String text ) {
        configure();
        setTitle( heading );
        setContent( text );
    }

    private void configure() {
        setPlacement( Placement.RIGHT );
        setTrigger( Trigger.HOVER );

        final Icon icon = new Icon( IconType.QUESTION );
        icon.addStyleName( "help-inline" );
        setWidget( icon );

        configurePopoverContainer( this.getWidget().getElement() );
        getWidget().getElement().getStyle().setZIndex( Integer.MAX_VALUE );
    }

    private native void configurePopoverContainer( Element e ) /*-{
        $wnd.jQuery(e).popover({
            container: 'body'
        });
    }-*/;

}