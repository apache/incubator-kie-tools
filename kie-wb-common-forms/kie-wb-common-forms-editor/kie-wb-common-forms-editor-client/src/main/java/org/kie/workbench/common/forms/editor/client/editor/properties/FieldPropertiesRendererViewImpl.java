/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.editor.client.editor.properties;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.util.FormsElementWrapperWidgetUtil;
import org.kie.workbench.common.forms.editor.client.editor.properties.binding.DataBindingEditor;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.editor.service.shared.FormEditorRenderingContext;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

@Dependent
@Templated
public class FieldPropertiesRendererViewImpl extends Composite implements FieldPropertiesRenderer.FieldPropertiesRendererView {

    @Inject
    @DataField
    private FlowPanel formContent;

    @Inject
    @DataField
    private ListBox fieldType;

    @Inject
    @DataField
    private FlowPanel fieldBinding;

    private DynamicFormRenderer formRenderer;

    private TranslationService translationService;

    private FormsElementWrapperWidgetUtil wrapperWidgetUtil;

    private FieldPropertiesRenderer presenter;

    private FieldPropertiesRendererHelper helper;

    private DataBindingEditor dataBindingEditor;

    private BaseModal modal;

    @Inject
    public FieldPropertiesRendererViewImpl(DynamicFormRenderer formRenderer,
                                           TranslationService translationService,
                                           FormsElementWrapperWidgetUtil wrapperWidgetUtil) {
        this.formRenderer = formRenderer;
        this.translationService = translationService;
        this.wrapperWidgetUtil = wrapperWidgetUtil;
    }

    @PostConstruct
    protected void init() {

        modal = new BaseModal();
        modal.setClosable(false);
        modal.setBody(this);
        modal.add(new ModalFooterOKCancelButtons(
                this::maybeOk,
                this::close));
        modal.addHideHandler(evt -> presenter.onClose());
        formContent.add(formRenderer);
    }

    @Override
    public void setPresenter(FieldPropertiesRenderer presenter) {
        this.presenter = presenter;
    }

    @Override
    public void render(FieldPropertiesRendererHelper helper,
                       FormEditorRenderingContext renderingContext,
                       DataBindingEditor dataBindingEditor) {
        this.helper = helper;
        this.dataBindingEditor = dataBindingEditor;
        formRenderer.render(renderingContext);
        initFieldTypeList();
        fieldBinding.clear();
        fieldBinding.add(wrapperWidgetUtil.getWidget(this, dataBindingEditor.getElement()));
        modal.setTitle(translationService.getTranslation(FormEditorConstants.FieldPropertiesRendererViewImplTitle));
    }

    @Override
    public Modal getPropertiesModal() {
        return modal;
    }

    private void maybeOk() {
        if (formRenderer.isValid()) {
            presenter.onPressOk();
            close();
        }
    }

    private void close() {
        formRenderer.unBind();
        modal.hide();
    }

    protected void initFieldTypeList() {
        fieldType.clear();

        List<String> types = presenter.getCompatibleFieldTypes();

        if (types.size() == 1) {
            fieldType.setEnabled(false);
        }

        types.forEach(fieldType::addItem);

        String currentType = presenter.getCurrentField().getFieldType().getTypeName();

        fieldType.setSelectedIndex(types.indexOf(currentType));
    }

    @EventHandler("fieldType")
    public void onTypeChange(ChangeEvent event) {
        presenter.onFieldTypeChange(fieldType.getSelectedValue());
    }

    @PreDestroy
    public void clear() {
        fieldBinding.clear();
        if (dataBindingEditor != null) {
            wrapperWidgetUtil.clear(this);
        }
        formRenderer.unBind();
        modal.clear();
    }
}
