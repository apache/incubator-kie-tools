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

package org.uberfire.ext.layout.editor.client.infra;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.mvp.Command;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;

@Dependent
public class DragHelperComponentColumn {

    @Inject
    LayoutDragComponentHelper helper;

    @Inject
    Document document;

    private LayoutDragComponent layoutDragComponent;

    private LayoutComponent layoutComponent;

    public LayoutDragComponent getLayoutDragComponent() {
        if (layoutDragComponent == null) {
            layoutDragComponent =
                    helper.lookupDragTypeBean(layoutComponent.getDragTypeName());
        }
        return layoutDragComponent;
    }

    public boolean hasModalConfiguration() {
        return getLayoutDragComponent() instanceof HasModalConfiguration;
    }

    public void setLayoutComponent(LayoutComponent layoutComponent) {
        this.layoutComponent = layoutComponent;
    }

    public HTMLElement getPreviewElement(Widget context) {
        HTMLElement div = document.createElement("div");
        addCSSClass(div,
                    "uf-perspective-col");
        FlowPanel gwtDivWrapper = GWT.create(FlowPanel.class);
        gwtDivWrapper.getElement().addClassName("uf-perspective-col");
        gwtDivWrapper.add(getLayoutDragComponent()
                                  .getPreviewWidget(new RenderingContext(layoutComponent,
                                                                         context)).asWidget());
        DOMUtil.appendWidgetToElement(div,
                                      gwtDivWrapper);
        return div;
    }

    public void showConfigModal(Command configurationFinish,
                                Command configurationCanceled) {
        ModalConfigurationContext ctx = new ModalConfigurationContext(layoutComponent,
                                                                      configurationFinish,
                                                                      configurationCanceled
        );
        Modal configModal = ((HasModalConfiguration)
                getLayoutDragComponent()).getConfigurationModal(ctx);
        configModal.show();
    }
}
