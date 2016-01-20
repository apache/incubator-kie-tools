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

package org.uberfire.ext.widgets.common.client.tables;

import com.google.common.base.Strings;
import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.DOM;

import static com.google.gwt.dom.client.BrowserEvents.*;


/**
 * An extension to the normal TextCell that renders a Bootstrap Popover when text overflows.
 */
public class PopoverTextCell extends AbstractSafeHtmlCell<String> {

    public enum Placement {

        LEFT,
        TOP,
        AUTO,
        BOTTOM,
        RIGHT

    }

    private Placement placement;

    public PopoverTextCell( final Placement placement ) {
        super( SimpleSafeHtmlRenderer.getInstance(), MOUSEOVER, MOUSEOUT );
        this.placement = placement;
    }

    public PopoverTextCell() {
        this( Placement.AUTO );
    }

    @Override
    protected void render( Context context, SafeHtml data, SafeHtmlBuilder sb ) {
        hideAllPopover();
        final String content = data.asString();
        if ( Strings.isNullOrEmpty( content ) ) {
            return;
        }

        final Element div = DOM.createDiv();
        div.setId( DOM.createUniqueId() );
        div.setInnerHTML( content );
        div.getStyle().setOverflow( Style.Overflow.HIDDEN );
        div.getStyle().setTextOverflow( Style.TextOverflow.ELLIPSIS );
        div.getStyle().setWhiteSpace( Style.WhiteSpace.NOWRAP );
        final String html = div.getString();
        sb.appendHtmlConstant( html );

        Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                initPopover( div.getId(), placement.name().toLowerCase() );
            }
        } );
    }

    @Override
    public void onBrowserEvent( final Context context, final Element parent, final String value, final NativeEvent event, final ValueUpdater<String> valueUpdater ) {
        super.onBrowserEvent( context, parent, value, event, valueUpdater );

        final Element element = Element.as( event.getEventTarget() );

        if ( DivElement.is( element ) == false ) {
            return;
        }

        if ( MOUSEOVER.equals( event.getType() ) ) {
            Scheduler.get().scheduleDeferred( new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    showPopover( parent.getFirstChildElement().getId() );
                }
            } );
        } else if ( MOUSEOUT.equals( event.getType() ) ) {
            hidePopover( parent.getFirstChildElement().getId() );
        }
    }

    private native void hideAllPopover() /*-{
        $wnd.jQuery('.popover').popover('hide');
    }-*/;

    private native void hidePopover( String id ) /*-{
        $wnd.jQuery('#' + id).popover('hide');
    }-*/;

    private native void showPopover( String id ) /*-{
        $wnd.jQuery('#' + id).popover('show');
    }-*/;

    private native void initPopover( String id, String placement ) /*-{
        var jQueryId = '#' + id;
        var div = $wnd.jQuery(jQueryId);

        div.popover({
            trigger: 'manual',
            placement: placement,
            content: function () {
                var offsetWidth = $wnd.document.getElementById(id).offsetWidth;
                var scrollWidth = $wnd.document.getElementById(id).scrollWidth;
                return offsetWidth < scrollWidth ? div.html() : "";
            },
            container: 'body'
        });
    }-*/;

}