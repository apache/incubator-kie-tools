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

package org.uberfire.ext.plugin.client.perspective.editor.layout.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import org.gwtbootstrap3.client.ui.Modal;
import org.uberfire.ext.layout.editor.api.css.CssProperty;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.HasModalConfiguration;
import org.uberfire.ext.layout.editor.client.api.ModalConfigurationContext;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;
import org.uberfire.ext.layout.editor.client.infra.LayoutEditorCssHelper;
import org.uberfire.ext.plugin.client.perspective.editor.api.PerspectiveEditorCoreComponent;
import org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups.EditHTMLPresenter;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.ext.properties.editor.model.PropertyEditorCategory;

@Dependent
public class HTMLLayoutDragComponent implements PerspectiveEditorCoreComponent,
                                                HasModalConfiguration {

    public static final String HTML_CODE_PARAMETER = "HTML_CODE";

    @Inject
    private LayoutEditorCssHelper layoutCssHelper;

    @Inject
    private EditHTMLPresenter htmlEditor;

    @Override
    public String getDragComponentTitle() {
        return CommonConstants.INSTANCE.HTMLComponent();
    }

    @Override
    public String getDragComponentIconClass() {
        return "fa fa-html5";
    }

    @Override
    public List<PropertyEditorCategory> getPropertyCategories(LayoutComponent layoutComponent) {
        Map<String, String> propertyMap = layoutComponent.getProperties();
        List<PropertyEditorCategory> result = new ArrayList<>();

        PropertyEditorCategory category = layoutCssHelper.createCategory(LayoutEditorCssHelper.CSS_CATEGORY_TEXT);
        category.withField(layoutCssHelper.createField(propertyMap, CssProperty.TEXT_ALIGN));
        category.withField(layoutCssHelper.createField(propertyMap, CssProperty.TEXT_DECORATION));
        category.withField(layoutCssHelper.createField(propertyMap, CssProperty.COLOR));
        category.withField(layoutCssHelper.createField(propertyMap, CssProperty.FONT_SIZE));
        category.withField(layoutCssHelper.createField(propertyMap, CssProperty.FONT_WEIGHT));
        result.add(category);
        return result;
    }

    @Override
    public IsWidget getPreviewWidget(RenderingContext container) {
        return getShowWidget(container);
    }

    @Override
    public IsWidget getShowWidget(RenderingContext context) {
        Map<String, String> properties = context.getComponent().getProperties();
        String html = properties.get(HTMLLayoutDragComponent.HTML_CODE_PARAMETER);
        if (html == null) {
            return null;
        }
        return new HTMLPanel(html);
    }

    @Override
    public Modal getConfigurationModal(ModalConfigurationContext ctx) {
        htmlEditor.init(ctx);
        return htmlEditor.getView().getModal();
    }
}