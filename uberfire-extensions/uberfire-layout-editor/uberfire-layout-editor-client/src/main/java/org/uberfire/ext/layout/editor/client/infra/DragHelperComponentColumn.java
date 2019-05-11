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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.uberfire.ext.layout.editor.api.css.CssValue;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.api.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;
import org.uberfire.mvp.Command;

import static org.jboss.errai.common.client.dom.DOMUtil.addCSSClass;

@Dependent
public class DragHelperComponentColumn {

    @Inject
    LayoutDragComponentHelper dragHelper;

    @Inject
    LayoutEditorCssHelper layoutCssHelper;

    @Inject
    Document document;

    private LayoutDragComponent layoutDragComponent;

    private LayoutComponent layoutComponent;

    private LayoutTemplate.Style pageStyle;

    public LayoutDragComponent getLayoutDragComponent() {
        if (layoutDragComponent == null) {
            layoutDragComponent =
                    dragHelper.lookupDragTypeBean(layoutComponent.getDragTypeName());
        }
        return layoutDragComponent;
    }

    public List<PropertyEditorCategory> getLayoutDragComponentProperties() {
        List<PropertyEditorCategory> result = layoutCssHelper.getComponentPropertyCategories(layoutComponent);
        result.addAll(getLayoutDragComponent().getPropertyCategories(layoutComponent));
        return result;
    }

    public boolean hasModalConfiguration() {
        return getLayoutDragComponent() instanceof HasModalConfiguration;
    }

    public void setup(LayoutComponent layoutComponent,
                      LayoutTemplate.Style pageStyle) {
        this.layoutComponent = layoutComponent;
        this.pageStyle = pageStyle;
    }

    public HTMLElement getPreviewElement(Widget context) {
        HTMLElement div = document.createElement("div");
        applyCssPropertiesToLayoutComponent(div);
        addCSSClass(div,
                    "uf-perspective-col");

        if (LayoutTemplate.Style.PAGE == pageStyle) {
            addCSSClass(div,
                    "uf-le-overflow");
        }
        FlowPanel gwtDivWrapper = GWT.create(FlowPanel.class);
        LayoutDragComponent layoutDragComponent = getLayoutDragComponent();
        RenderingContext ctx = new RenderingContext(layoutComponent,context);
        gwtDivWrapper.add(getLayoutDragComponent().getPreviewWidget(ctx).asWidget());
        
        layoutComponent.getParts().forEach(part -> {
            layoutDragComponent.getContentPart(part.getPartId(), ctx).ifPresent(contentPart -> {
                applyCssProperties(contentPart.asWidget(), part.getCssProperties());
            });
        });
        
        DOMUtil.appendWidgetToElement(div,
                                      gwtDivWrapper);
        return div;
    }

    public void showConfigModal(Command configurationFinish,
                                Command configurationCanceled,
                                Supplier<LayoutTemplate> currentLayoutTemplateSupplier) {
        ModalConfigurationContext ctx = new ModalConfigurationContext(layoutComponent,
                                                                      configurationFinish,
                                                                      configurationCanceled,
                                                                      currentLayoutTemplateSupplier);
        Modal configModal = ((HasModalConfiguration)
                getLayoutDragComponent()).getConfigurationModal(ctx);
        configModal.show();
    }
    
    protected void applyCssPropertiesToLayoutComponent(HTMLElement widget) {
        applyCssProperties(ElementWrapperWidget.getWidget(widget), layoutComponent.getProperties());
    }
    
    protected void applyCssProperties(Widget widget, Map<String, String> properties) {
        List<CssValue> cssValues = layoutCssHelper.readCssValues(properties);
        cssValues.forEach(cssValue -> {
            String prop = cssValue.getPropertyInCamelCase();
            String val = cssValue.getValue();
            widget.getElement().getStyle().setProperty(prop, val);
        });
    }

}
