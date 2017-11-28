/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.navigation.widget;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.Node;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated
public class NavDropDownWidgetView extends BaseNavWidgetView<NavDropDownWidget>
    implements NavDropDownWidget.View {

    @Inject
    @DataField
    ListItem dropDownItem;

    @Inject
    @DataField
    Anchor dropDownAnchor;

    @Inject
    @DataField
    Span dropDownName;

    @Inject
    @DataField
    UnorderedList dropDownMenu;

    NavDropDownWidget presenter;
    boolean active = false;
    boolean submenu = false;

    @Override
    public void init(NavDropDownWidget presenter) {
        this.presenter = presenter;
        super.navWidget = dropDownMenu;
    }

    @Override
    public void setDropDownName(String name) {
        dropDownName.setTextContent(name);
    }

    private String calculateDropDownClassName() {
        if (submenu) {
            if (active) {
                return "dropdown-submenu active";
            } else {
                return "dropdown-submenu";
            }
        } else {
            if (active) {
                return "dropdown active";
            } else {
                return "dropdown";
            }
        }
    }

    @Override
    public void showAsSubmenu(boolean enabled) {
        submenu = enabled;
        String className = calculateDropDownClassName();
        dropDownItem.setClassName(className);
        if (enabled) {
            DOMUtil.removeAllChildren(dropDownAnchor);
            dropDownAnchor.appendChild(dropDownName);
        }
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        String className = calculateDropDownClassName();
        dropDownItem.setClassName(className);
    }

    @Override
    public void addDivider() {
        LIElement li = Document.get().createLIElement();
        li.setClassName("divider");
        dropDownMenu.appendChild((Node) li);
    }

    @Override
    public void errorNavGroupNotFound() {
        setDropDownName("ERROR: Nav group not found");
    }
}
