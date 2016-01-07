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

package org.uberfire.ext.security.management.client.screens.home;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;

import java.util.List;

public class EntitiesManagementHomeView extends Composite implements EntitiesManagementHome {

    interface EditorHomeViewBinder
            extends
            UiBinder<FlowPanel, EntitiesManagementHomeView> {

    }

    private static EditorHomeViewBinder uiBinder = GWT.create( EditorHomeViewBinder.class );

    @UiField
    FlowPanel mainPanel;

    @UiField
    HTML homeText;

    @UiField
    HTMLPanel itemsPanel;
    
    @UiConstructor
    public EntitiesManagementHomeView() {
        init();
    }

    private void init() {
        initWidget( uiBinder.createAndBindUi( this ) );
    }
    
    public void show(final String welcomeText, final List<String> items) {
        // Main text.
        homeText.setText(SafeHtmlUtils.htmlEscape(welcomeText));
        
        // List items.
        removeChildrenElements(itemsPanel.getElement());
        if (items != null && !items.isEmpty()) {
            final Element listElement = DOM.createElement("UL");
            for (final String item : items) {
                final Element itemElement = DOM.createElement("LI");
                final Element spanElement = DOM.createElement("SPAN");
                spanElement.setInnerText(SafeHtmlUtils.htmlEscape(item));
                DOM.appendChild(listElement, itemElement);
                DOM.appendChild(itemElement, spanElement);
            }
            DOM.appendChild(itemsPanel.getElement(), listElement);
        }
    }
    
    private void removeChildrenElements(final Element element) {
        if (element == null) throw new NullPointerException();
        
        final int c = element.getChildCount();
        for (int x = 0; x < c; x++) {
            final Node e = element.getChild(x);
            e.removeFromParent();
        }
    }

    
}