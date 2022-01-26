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

import java.util.function.Consumer;
import java.util.function.Function;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsFunction;
import jsinterop.base.JsPropertyMap;
import org.gwtbootstrap3.client.shared.js.JQuery;
import org.gwtproject.cell.client.AbstractSafeHtmlCell;
import org.gwtproject.cell.client.ValueUpdater;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.dom.client.DivElement;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.NativeEvent;
import org.gwtproject.dom.client.Style;
import org.gwtproject.safehtml.shared.SafeHtml;
import org.gwtproject.safehtml.shared.SafeHtmlBuilder;
import org.gwtproject.text.shared.SimpleSafeHtmlRenderer;
import org.gwtproject.user.client.DOM;

import static org.gwtbootstrap3.client.shared.js.JQuery.$;
import static org.gwtproject.dom.client.BrowserEvents.MOUSEOUT;
import static org.gwtproject.dom.client.BrowserEvents.MOUSEOVER;

/**
 * An extension to the normal TextCell that renders a Bootstrap Popover when text overflows.
 */
public class PopoverTextCell extends AbstractSafeHtmlCell<String> {

    private Placement placement;

    public PopoverTextCell(final Placement placement) {
        super(SimpleSafeHtmlRenderer.getInstance(),
              MOUSEOVER,
              MOUSEOUT);
        this.placement = placement;
    }

    public PopoverTextCell() {
        this(Placement.AUTO);
    }

    @Override
    protected void render(Context context,
                          SafeHtml data,
                          SafeHtmlBuilder sb) {
        hideAllPopover();
        final String content = data.asString();
        if (content == null || content.isEmpty()) {
            return;
        }

        final Element div = DOM.createDiv();
        div.setId(DOM.createUniqueId());
        div.setInnerHTML(content);
        div.getStyle().setOverflow(Style.Overflow.HIDDEN);
        div.getStyle().setTextOverflow(Style.TextOverflow.ELLIPSIS);
        div.getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
        final String html = div.getString();
        sb.appendHtmlConstant(html);

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                initPopover(div.getId(),
                            placement.name().toLowerCase());
            }
        });
    }

    @Override
    public void onBrowserEvent(final Context context,
                               final Element parent,
                               final String value,
                               final NativeEvent event,
                               final ValueUpdater<String> valueUpdater) {
        super.onBrowserEvent(context,
                             parent,
                             value,
                             event,
                             valueUpdater);

        final Element element = Element.as(event.getEventTarget());

        if (DivElement.is(element) == false) {
            return;
        }

        if (MOUSEOVER.equals(event.getType())) {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    showPopover(parent.getFirstChildElement().getId());
                }
            });
        } else if (MOUSEOUT.equals(event.getType())) {
            hidePopover(parent.getFirstChildElement().getId());
        }
    }

    private void hideAllPopover() {
        $(".popover").popover("hide");

    }/*-{
        $wnd.jQuery('.popover').popover('hide');
    }-*/;

    private void hidePopover(String id) {
        $("#" + id).popover("hide");

    }/*-{
        $wnd.jQuery('#' + id).popover('hide');
    }-*/;

    private void showPopover(String id) {
        $("#" + id).popover("show");

    }/*-{
        $wnd.jQuery('#' + id).popover('show');
    }-*/;

    //m_showPopover__java_lang_String_$p_org_uberfire_ext_widgets_common_client_tables_PopoverTextCell

    private void initPopover2(String id,
                                    String placement) {

    }

    private native void initPopover(String id,
                                    String placement);/*-{
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

    @FunctionalInterface
    @JsFunction
    private interface OnContent {
        String apply();
    }

    public enum Placement {

        LEFT,
        TOP,
        AUTO,
        BOTTOM,
        RIGHT

    }
}