/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.widgets;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.infra.DndDataJSONConverter;
import org.uberfire.ext.layout.editor.client.infra.DragComponentEndEvent;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;
import static org.jboss.errai.common.client.dom.DOMUtil.removeCSSClass;
import static org.uberfire.ext.layout.editor.client.infra.HTML5DnDHelper.setDndData;

@Dependent
@Templated
public class LayoutDragComponentWidget implements IsElement {

    @Inject
    @DataField
    Span title;

    @Inject
    @DataField
    private Span icon;

    @Inject
    @DataField
    private Div dndcomponent;
    private DndDataJSONConverter converter = new DndDataJSONConverter();

    @Inject
    private Event<DragComponentEndEvent> dragComponentEnd;

    public void init(LayoutDragComponent dragComponent) {
        title.setTextContent(dragComponent.getDragComponentTitle());
        icon.setClassName(dragComponent.getDragComponentIconClass() + " le-icon");
        dndcomponent.setOnmousedown(e -> addCSSClass(dndcomponent,
                                                     "le-dndcomponent-selected"));
        dndcomponent.setOnmouseup(e -> {
            removeCSSClass(dndcomponent,
                           "le-dndcomponent-selected");
            dragComponentEnd.fire(new DragComponentEndEvent());
        });
        dndcomponent.setOndragend(e -> {
            removeCSSClass(dndcomponent,
                           "le-dndcomponent-selected");
            dragComponentEnd.fire(new DragComponentEndEvent());
        });
        dndcomponent.setOndragstart(
                event -> {
                    setDndData(event,
                               converter.generateDragComponentJSON(dragComponent));
                });
    }
}
