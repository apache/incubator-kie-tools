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

import com.google.gwt.cell.client.AbstractSafeHtmlCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.DOM;

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

    public PopoverTextCell(final Placement placement ) {
        super( SimpleSafeHtmlRenderer.getInstance() );
        this.placement = placement;
    }

    public PopoverTextCell() {
        this( Placement.AUTO );
    }

    @Override
    protected void render(Context context, SafeHtml data, SafeHtmlBuilder sb) {
        final Element div = DOM.createDiv();
        div.setId( DOM.createUniqueId() );
        div.setInnerHTML( data.asString() );
        div.getStyle().setOverflow( Style.Overflow.HIDDEN );
        div.getStyle().setTextOverflow( Style.TextOverflow.ELLIPSIS );
        div.getStyle().setWhiteSpace( Style.WhiteSpace.NOWRAP );
        final String html = div.getString();
        sb.appendHtmlConstant(html);

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                initPopover( div.getId(), placement.name().toLowerCase() );
            }
        });
    }

    private native void initPopover( String id, String placement ) /*-{
        var jQueryId = '#' + id;
        var div = $wnd.jQuery( jQueryId );

        div.popover({
            trigger: 'hover',
            placement: placement,
            content: function (){
                var offsetWidth = $wnd.document.getElementById( id ).offsetWidth;
                var scrollWidth = $wnd.document.getElementById( id ).scrollWidth;
                return offsetWidth < scrollWidth ? div.html() : "";
            },
            container: 'body'
        });
    }-*/;

}