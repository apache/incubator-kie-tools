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

package org.uberfire.ext.properties.editor.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.InputGroup;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.TextBox;
import org.uberfire.ext.properties.editor.model.PropertyEditorEvent;

public class PropertyEditorWidget extends Composite {

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    @UiField
    PanelGroup propertyMenu;
    PropertyEditorEvent originalEvent;
    @UiField
    InputGroup filterGroup;
    @UiField
    TextBox filterBox;
    @UiField
    Button reload;
    List<String> expandedCategories = new ArrayList<>();

    public PropertyEditorWidget() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @PostConstruct
    public void init() {
        propertyMenu.setId(DOM.createUniqueId());
    }

    public void handle(PropertyEditorEvent event) {
        if (PropertyEditorHelper.validade(event)) {
            this.originalEvent = event;
            this.filterBox.setText("");
            PropertyEditorHelper.extractEditorFrom(this,
                                                   propertyMenu,
                                                   event);
        }
    }

    @UiHandler("reload")
    void onReload(ClickEvent e) {
        this.filterBox.setText("");
        PropertyEditorHelper.extractEditorFrom(this,
                                               propertyMenu,
                                               originalEvent,
                                               "");
    }

    @UiHandler("filterBox")
    public void onKeyUp(KeyUpEvent e) {
        if (originalEvent != null) {
            propertyMenu.clear();
            PropertyEditorHelper.extractEditorFrom(this,
                                                   propertyMenu,
                                                   originalEvent,
                                                   filterBox.getText());
        }
    }

    public void setFilterGroupVisible(boolean visible) {
        filterGroup.setVisible(visible);
    }

    public PanelGroup getPropertyMenu() {
        return propertyMenu;
    }

    /**
     * Bootstrap nows supports more than
     * one panel expanded, so this method is
     * @deprecated replaced by getExpandedCategories()
     */
    public String getLastOpenAccordionGroupTitle() {
        if (expandedCategories.isEmpty()) {
            return "";
        }
        return expandedCategories.get(expandedCategories.size() - 1);
    }

    /**
     * Bootstrap nows supports more than
     * one panel expanded, so this method is
     * @deprecated replaced by addExpandedCategory( String categoriesToExpand)
     */
    @Deprecated
    public void setLastOpenAccordionGroupTitle(String categoryToExpand) {
        this.expandedCategories.add(categoryToExpand);
    }

    public void addExpandedCategory(String... categoriesToExpand) {
        this.expandedCategories.addAll(Arrays.asList(categoriesToExpand));
    }

    public List<String> getExpandedCategories() {
        return expandedCategories;
    }

    protected void collapseCategory(String categoryToCollapse) {
        this.expandedCategories.remove(categoryToCollapse);
    }

    interface MyUiBinder extends UiBinder<Widget, PropertyEditorWidget> {

    }
}
