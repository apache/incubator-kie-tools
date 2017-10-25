/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.client.widget;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Styles;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.EventListener;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Window;
import org.uberfire.mvp.Command;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;

@JsType(isNative = true, name = "HTMLAnchorElement", namespace = JsPackage.GLOBAL)
public abstract class CustomGroupItem implements Anchor {

    @JsOverlay
    public static CustomGroupItem createAnchor(final String text,
                                               final IconType iconType,
                                               final Command command) {
        final CustomGroupItem anchor = (CustomGroupItem) Window.getDocument().createElement("a");
        anchor.setClassName(Styles.LIST_GROUP_ITEM);
        if (iconType != null) {
            final HTMLElement icon = Window.getDocument().createElement("i");
            icon.getClassList().add("fa");
            icon.getClassList().add(iconType.getCssName());
            anchor.setInnerHTML(icon.getOuterHTML() + " " + text);
        } else {
            anchor.setTextContent(checkNotEmpty("text",
                                                text));
        }
        anchor.setHref("#");
        //can't use lambda here; GWT limitation (bug!)!
        anchor.setOnclick(new EventListener() {
            @Override
            public void call(final Event event) {
                command.execute();
            }
        });
        return anchor;
    }

    @JsOverlay
    public final void setActive(boolean active) {
        if (active) {
            getClassList().add("active");
        } else {
            getClassList().remove("active");
        }
    }
}
