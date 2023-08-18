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

package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import javax.enterprise.context.ApplicationScoped;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;

@ApplicationScoped
public class TargetDivDragComponent implements LayoutDragComponent {

    public static final String ID_PARAMETER = "ID_PARAMETER";
    public static final String DIV_ID = "divId";

    @Override
    public HTMLElement getShowWidget(RenderingContext ctx) {
        String id = ctx.getComponent().getProperties().get(ID_PARAMETER);
        if (id == null) {
            id = ctx.getComponent().getProperties().get(DIV_ID);
        }
        return createDiv(id);
    }

    private HTMLElement createDiv(String id) {
        var div = DomGlobal.document.createElement("div");
        div.id = id;
        return (HTMLElement) div;
    }

}
