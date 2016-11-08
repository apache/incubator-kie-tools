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

package org.kie.workbench.common.forms.editor.client.editor.properties;

import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.editor.service.FormEditorRenderingContext;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;

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
    private ListBox fieldBinding;

    private DynamicFormRenderer formRenderer;

    private TranslationService translationService;

    private FieldPropertiesRenderer presenter;

    private FieldPropertiesRendererHelper helper;

    private BaseModal modal;

    @Inject
    public FieldPropertiesRendererViewImpl( DynamicFormRenderer formRenderer, TranslationService translationService ) {
        this.formRenderer = formRenderer;
        this.translationService = translationService;
    }

    protected void closeModal() {
        if ( formRenderer.isValid() ) {
            helper.onClose();
            modal.hide();
        }
    }

    @PostConstruct
    protected void init() {

        modal = new BaseModal();
        modal.setClosable( false );
        modal.setBody( this );
        modal.add( new ModalFooterOKButton( new Command() {
            @Override
            public void execute() {
                closeModal();
            }
        } ) );

        formContent.add( formRenderer );
    }

    @Override
    public void setPresenter( FieldPropertiesRenderer presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void render( FieldPropertiesRendererHelper helper, FormEditorRenderingContext renderingContext ) {
        this.helper = helper;
        formRenderer.render( renderingContext );
        initFieldTypeList();
        initFieldBindings();

        modal.setTitle( translationService.getTranslation( FormEditorConstants.FieldPropertiesRendererViewImplTitle ) );
    }

    @Override
    public Modal getPropertiesModal() {
        return modal;
    }

    protected void initFieldTypeList() {
        fieldType.clear();
        Collection<String> types = helper.getCompatibleFieldTypes();
        int i = 0;
        for ( String type : types ) {
            fieldType.addItem( type );
            if ( type.equals( helper.getCurrentField().getCode() )) {
                fieldType.setSelectedIndex( i );
            }
            i++;
        }
    }

    @EventHandler( "fieldType" )
    public void onTypeChange( ChangeEvent event ) {
        helper.onFieldTypeChange( fieldType.getSelectedValue() );
    }

    @EventHandler( "fieldBinding" )
    public void onBindingChange( ChangeEvent event ) {
        helper.onFieldBindingChange( fieldBinding.getSelectedValue() );
        initFieldBindings();
    }

    protected void initFieldBindings() {
        fieldBinding.clear();
        List<String> fields = helper.getAvailableFields();
        for ( int i = 0; i < fields.size(); i++ ) {
            String field = fields.get( i );
            fieldBinding.addItem( field );
            if ( field.equals( helper.getCurrentField().getBinding() )) {
                fieldBinding.setSelectedIndex( i );
            }
        }
    }
}
