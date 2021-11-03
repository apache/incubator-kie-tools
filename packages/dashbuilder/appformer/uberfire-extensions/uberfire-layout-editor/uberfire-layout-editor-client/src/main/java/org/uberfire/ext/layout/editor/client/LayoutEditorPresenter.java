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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.api.editor.LayoutInstance;
import org.uberfire.ext.layout.editor.client.api.LayoutEditorElement;
import org.uberfire.ext.layout.editor.client.api.LayoutElementVisitor;
import org.uberfire.ext.layout.editor.client.event.LayoutElementClearAllPropertiesEvent;
import org.uberfire.ext.layout.editor.client.event.LayoutElementPropertyChangedEvent;
import org.uberfire.ext.layout.editor.client.components.container.Container;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Dependent
public class LayoutEditorPresenter {

    private final View view;
    private LayoutTemplate.Style pageStyle = LayoutTemplate.Style.FLUID;
    private Container container;
    private LayoutGenerator layoutGenerator;
    private boolean preview = false;

    @Inject
    public LayoutEditorPresenter(final View view,
                                 Container container,
                                 LayoutGenerator layoutGenerator) {
        this.view = view;
        this.container = container;
        this.layoutGenerator = layoutGenerator;
        view.init(this);
    }

    public void setup(Supplier<Boolean> lockSupplier) {
        container.setLockSupplier(lockSupplier);
    }

    @PostConstruct
    public void initNew() {
        view.setupDesign(container.getView());
        view.setPreviewEnabled(false);
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        view.setPreviewEnabled(previewEnabled);
    }

    public void setElementSelectionEnabled(boolean enabled) {
        container.setSelectable(enabled);
        container.visit(element -> element.setSelectable(enabled));
    }

    public void clear() {
        container.reset();
    }

    public UberElement<LayoutEditorPresenter> getView() {
        return view;
    }

    public LayoutTemplate getLayout() {
        return container.toLayoutTemplate();
    }

    public void loadLayout(LayoutTemplate layoutTemplate,
                           String emptyTitleText,
                           String emptySubTitleText) {

        view.setDesignStyle(layoutTemplate.getStyle());

        container.load(layoutTemplate,
                       emptyTitleText,
                       emptySubTitleText);
    }

    public void loadEmptyLayout(String layoutName,
                                String emptyTitleText,
                                String emptySubTitleText) {
        view.setDesignStyle(pageStyle);
        container.loadEmptyLayout(layoutName,
                                  pageStyle,
                                  emptyTitleText,
                                  emptySubTitleText);
    }

    public void addLayoutProperty(String key,
                                  String value) {
        container.addProperty(key,
                              value);
    }

    public String getLayoutProperty(String key) {
        return container.getProperty(key);
    }


    public void setPageStyle(LayoutTemplate.Style pageStyle) {
        this.pageStyle = pageStyle;
    }

    public void switchToDesignMode() {
        preview = false;
        view.setupDesign(container.getView());
    }

    public void switchToPreviewMode() {
        preview = true;
        LayoutTemplate layoutTemplate = container.toLayoutTemplate();
        LayoutInstance layoutInstance = layoutGenerator.build(layoutTemplate);
        view.setupPreview(layoutInstance.getElement());
    }

    public List<LayoutEditorElement> getLayoutElements() {
        List<LayoutEditorElement> result = new ArrayList<>();
        container.visit(result::add);
        return result;
    }

    public void visit(LayoutElementVisitor visitor) {
        container.visit(visitor);
    }

    // Refresh the layout preview when the properties of a layout element change

    protected void onLayoutPropertyChangedEvent(@Observes LayoutElementPropertyChangedEvent event) {
        if (preview) {
            switchToPreviewMode();
        }
    }

    protected void onClearAllPropertiesEvent(@Observes LayoutElementClearAllPropertiesEvent event) {
        if (preview) {
            switchToPreviewMode();
        }
    }

    public interface View extends UberElement<LayoutEditorPresenter> {

        void setupDesign(UberElement<Container> container);

        void setDesignStyle(LayoutTemplate.Style pageStyle);

        void setPreviewEnabled(boolean previewEnabled);

        void setupPreview(HTMLElement previewPanel);
    }
}
