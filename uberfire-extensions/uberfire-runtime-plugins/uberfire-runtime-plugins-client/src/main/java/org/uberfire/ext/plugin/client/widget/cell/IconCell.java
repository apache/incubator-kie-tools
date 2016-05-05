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

package org.uberfire.ext.plugin.client.widget.cell;

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.Window;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;

import static com.google.gwt.dom.client.BrowserEvents.*;

public class IconCell extends AbstractSafeHtmlCell<String> {

    private IconType iconType;
    private IconSize iconSize;
    private String tooltip;

    /**
     * Construct a new {@link IconCell} with the specified icon type
     * @param iconType
     */
    public IconCell( IconType iconType ) {
        this( iconType, IconSize.NONE );
    }

    /**
     * Construct a new {@link IconCell} with the specified icon type and icon size
     * @param iconType
     * @param iconSize
     */
    public IconCell( IconType iconType,
                     IconSize iconSize ) {
        super( SimpleSafeHtmlRenderer.getInstance(), CLICK, KEYDOWN );
        this.iconType = iconType;
        this.iconSize = iconSize;
    }

    public IconType getIconType() {
        return iconType;
    }

    public void setIconType( IconType iconType ) {
        this.iconType = iconType;
    }

    public IconSize getIconSize() {
        return iconSize;
    }

    public void setIconSize( IconSize iconSize ) {
        this.iconSize = iconSize;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip( String tooltip ) {
        this.tooltip = tooltip;
    }

    @Override
    protected void render( Context context,
                           SafeHtml data,
                           SafeHtmlBuilder sb ) {
        Icon icon = GWT.create( Icon.class );
        if ( tooltip != null ) {
            icon.setTitle( tooltip );
        }
        icon.setSize( iconSize );
        icon.setType( iconType );
        sb.appendHtmlConstant( icon.getElement().getString());

    }

    @Override
    public void onBrowserEvent( final Context context,
                                final Element parent,
                                final String value,
                                final NativeEvent event,
                                final ValueUpdater<String> valueUpdater ) {
        super.onBrowserEvent( context, parent, value, event, valueUpdater );
        if ( CLICK.equals( event.getType() ) ) {
            EventTarget eventTarget = event.getEventTarget();
            if ( !Element.is( eventTarget ) ) {
                return;
            }
            if ( parent.getFirstChildElement().isOrHasChild( Element.as( eventTarget ) ) ) {
                // Ignore clicks that occur outside of the main element.
                onEnterKeyDown( context, parent, value, event, valueUpdater );
            }
        }
    }

    @Override
    protected void onEnterKeyDown( final Context context,
                                   final Element parent,
                                   final String value,
                                   final NativeEvent event,
                                   final ValueUpdater<String> valueUpdater ) {
        if ( valueUpdater != null ) {
            valueUpdater.update( value );
        }
    }

}