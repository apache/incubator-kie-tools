/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.drools.workbench.screens.scenariosimulation.client.editor.menu;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class MenuItemViewImpl implements MenuItemView {

    @DataField("liElement")
    private LIElement liElement = Document.get().createLIElement();

    @DataField("spanElement")
    private SpanElement spanElement = Document.get().createSpanElement();

    private MenuItemPresenter menuItemPresenter;

    @Override
    public LIElement getLabelMenuElement() {
        liElement.addClassName("disabled");
        return liElement;
    }

    @Override
    public void setPresenter(MenuItemPresenter menuItemPresenter) {
        this.menuItemPresenter = menuItemPresenter;
    }

    @Override
    public void setId(String id) {
        liElement.setId(id);
    }

    @Override
    public void setDataI18nKey(String dataI18nKey) {
        spanElement.setAttribute("data-i18n-key", dataI18nKey);
    }

    @Override
    public void setLabel(String label) {
        String innerHtml = "<b>" + label.toUpperCase() + "</b>";
        spanElement.setInnerHTML(innerHtml);
    }

    @EventHandler("liElement")
    public void onClickEvent(ClickEvent event) {
        menuItemPresenter.onClickEvent(event);
    }
}
