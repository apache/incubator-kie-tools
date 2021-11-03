/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.cms.widget;

import javax.inject.Inject;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Templated
public class PerspectivesExplorerView implements IsElement, PerspectivesExplorer.View {

    @Inject
    @DataField
    Div perspectivesDiv;

    PerspectivesExplorer presenter;

    @Override
    public void init(PerspectivesExplorer presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        DOMUtil.removeAllChildren(perspectivesDiv);
    }

    private DivElement createItemDiv(Element[] items) {
        DivElement mi = Document.get().createDivElement();
        mi.setClassName("list-view-pf-main-info");
        mi.getStyle().setPaddingTop(5, Style.Unit.PX);
        mi.getStyle().setPaddingBottom(5, Style.Unit.PX);
        for (Element item : items) {
            mi.appendChild(item);
        }

        DivElement gi = Document.get().createDivElement();
        gi.setClassName("list-group-item");
        gi.appendChild(mi);
        return gi;
    }

    @Override
    public void addPerspective(String name, Command onClicked) {
        AnchorElement anchor = Document.get().createAnchorElement();
        anchor.getStyle().setCursor(Style.Cursor.POINTER);
        anchor.getStyle().setColor("black");
        anchor.getStyle().setProperty("fontSize", "larger");
        anchor.setInnerText(name);

        Event.sinkEvents(anchor, Event.ONCLICK);
        Event.setEventListener(anchor, event -> {
            if (Event.ONCLICK == event.getTypeInt()) {
                onClicked.execute();
            }
        });

        SpanElement icon = Document.get().createSpanElement();
        icon.getStyle().setMarginRight(10, Style.Unit.PX);
        icon.setClassName("fa fa-file-text-o");
        icon.getStyle().setProperty("fontSize", "larger");

        DivElement gi = createItemDiv(new Element[]{icon, anchor});
        perspectivesDiv.appendChild((Node) gi);
    }

    @Override
    public void showEmpty(String message) {
        SpanElement span = Document.get().createSpanElement();
        span.setInnerText(message);
        DivElement gi = createItemDiv(new Element[]{span});
        perspectivesDiv.appendChild((Node) gi);
    }
}