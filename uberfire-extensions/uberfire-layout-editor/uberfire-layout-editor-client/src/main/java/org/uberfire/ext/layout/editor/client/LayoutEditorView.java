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

package org.uberfire.ext.layout.editor.client;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.ListItem;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.resources.i18n.CommonConstants;

@Templated
@Dependent
public class LayoutEditorView
        implements UberElement<LayoutEditorPresenter>,
                   LayoutEditorPresenter.View,
                   IsElement {

    @Inject
    @DataField
    Div mainDiv;

    @Inject
    @DataField
    Div container;

    @Inject
    @DataField
    Div tabsDiv;

    @Inject
    @DataField
    ListItem designTab;

    @Inject
    @DataField
    ListItem previewTab;

    @Inject
    @DataField
    Div designDiv;

    @Inject
    @DataField
    Div previewDiv;

    @Inject
    @DataField
    Anchor designAnchor;

    @Inject
    @DataField
    Anchor previewAnchor;

    private LayoutEditorPresenter presenter;

    @Override
    public void init(LayoutEditorPresenter presenter) {
        this.presenter = presenter;
        designAnchor.setTextContent(CommonConstants.INSTANCE.Editor());
        previewAnchor.setTextContent(CommonConstants.INSTANCE.Preview());
    }

    @Override
    public void setPreviewEnabled(boolean previewEnabled) {
        tabsDiv.setHidden(!previewEnabled);
        mainDiv.getStyle().setProperty("height", previewEnabled ? "95%" : "100%");
    }

    @Override
    public void setupDesign(UberElement<Container> container) {
        designDiv.setHidden(false);
        previewDiv.setHidden(true);
        designTab.setClassName("active");
        previewTab.setClassName("");
        this.container.appendChild(container.getElement());
    }

    @Override
    public void setDesignStyle(LayoutTemplate.Style pageStyle) {
        designDiv.setClassName("le-design-container le-design-" + pageStyle.toString().toLowerCase());
    }

    @Override
    public void setupPreview(HTMLElement previewPanel) {
        designDiv.setHidden(true);
        previewDiv.setHidden(false);
        designTab.setClassName("");
        previewTab.setClassName("active");
        DOMUtil.removeAllChildren(this.previewDiv);
        this.previewDiv.appendChild(previewPanel);
    }

    @EventHandler("designTab")
    private void designTabClicked(ClickEvent event) {
        if (!designTab.getClassName().equals("active")) {
            presenter.switchToDesignMode();
        }
    }

    @EventHandler("previewTab")
    private void previewTabClicked(ClickEvent event) {
        if (!previewTab.getClassName().equals("active")) {
            presenter.switchToPreviewMode();
        }
    }
}