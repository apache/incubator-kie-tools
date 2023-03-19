/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.widgets.common.client.menu;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@MenuItemWithIcon
public class MenuItemWithIconView implements MenuItemView {

    @Inject
    @DataField
    Div icon;

    @Inject
    @DataField
    Span caption;

    @Inject
    @DataField
    ListItem listItem;

    private ClickHandler clickHandler;

    public void setCaption(final String caption) {
        this.caption.setTextContent(caption);
    }

    public void setClickHandler(final ClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public void setEnabled(final boolean enabled) {
        if (enabled) {
            DOMUtil.removeCSSClass(listItem,
                                   "disabled");
        } else {
            DOMUtil.addCSSClass(listItem,
                                "disabled");
        }
    }

    public void setIconType(final IconType type) {
        if (type == null) {
            DOMUtil.removeEnumStyleNames(icon,
                                         IconType.class);
        } else {
            DOMUtil.addUniqueEnumStyleName(icon,
                                           IconType.class,
                                           type);
        }
    }

    @EventHandler("listItem")
    @SuppressWarnings("unused")
    public void onClickListItem(final ClickEvent event) {
        if (clickHandler != null) {
            clickHandler.onClick(event);
        }
    }
}
