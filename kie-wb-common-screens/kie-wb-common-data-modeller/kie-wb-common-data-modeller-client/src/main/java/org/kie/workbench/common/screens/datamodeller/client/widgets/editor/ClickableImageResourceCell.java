/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.editor;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;

import static com.google.gwt.dom.client.BrowserEvents.*;

public class ClickableImageResourceCell extends ImageResourceCell {

    private boolean asAnchor = false;

    private int minWidth = -1;

    public ClickableImageResourceCell(boolean asAnchor) {
        this.asAnchor = asAnchor;
    }

    public ClickableImageResourceCell( boolean asAnchor, int minWidth ) {
        this.asAnchor = asAnchor;
        this.minWidth = minWidth;
    }

    @Override
    public Set<String> getConsumedEvents() {
        Set<String> consumedEvents = new HashSet<String>();
        consumedEvents.add(CLICK);
        consumedEvents.add(KEYDOWN);
        return consumedEvents;
    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, ImageResource value,
                               NativeEvent event, ValueUpdater<ImageResource> valueUpdater) {
        switch (DOM.eventGetType((Event) event)) {
            case Event.ONCLICK:
                valueUpdater.update(value);
                break;

        }
    }

    @Override
    public void render(Context context, ImageResource value, SafeHtmlBuilder sb) {

        if (value != null) {
            SafeHtml startAnchor = null;
            SafeHtml endAnchor = null;
            if (asAnchor) {
                startAnchor = new SafeHtml() {
                    @Override
                    public String asString() {
                        String minWidthStyle = minWidth > 0 ? " min-width:"+minWidth+"px;" : "";

                        return "<div style=\"cursor: pointer;"+minWidthStyle+"\">";
                    }
                };

                endAnchor = new SafeHtml() {
                    @Override
                    public String asString() {
                        return "</div>";
                    }
                };

                sb.append(startAnchor);
            }
            super.render(context, value, sb);
            if (asAnchor) {
                sb.append(endAnchor);
            }
        }
    }
}