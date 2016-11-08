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

package org.kie.workbench.common.forms.dynamic.client.rendering;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Panel;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.generator.AbstractLayoutGenerator;
import org.uberfire.ext.layout.editor.client.infra.LayoutDragComponentHelper;

@Any
@Dependent
public class FormLayoutGenerator extends AbstractLayoutGenerator {

    private List<FieldLayoutComponent> layoutComponents = new ArrayList<FieldLayoutComponent>();

    @Inject
    private LayoutDragComponentHelper dragTypeBeanResolver;

    private FormRenderingContext renderingContext;

    public Panel buildLayout( FormRenderingContext renderingContext ) {
        this.renderingContext = renderingContext;
        layoutComponents.clear();
        if ( renderingContext == null || renderingContext.getRootForm() == null ) {
            return getLayoutContainer();
        }
        return build( renderingContext.getRootForm().getLayoutTemplate() );
    }

    @Override
    public ComplexPanel getLayoutContainer() {
        return new Column( ColumnSize.MD_12 );
    }

    @Override
    public LayoutDragComponent getLayoutDragComponent( LayoutComponent layoutComponent ) {
        LayoutDragComponent dragComponent = dragTypeBeanResolver.lookupDragTypeBean( layoutComponent.getDragTypeName() );
        if ( dragComponent instanceof FieldLayoutComponent ) {
            FieldLayoutComponent fieldComponent = (FieldLayoutComponent) dragComponent;

            FieldDefinition field = renderingContext.getRootForm().getFieldById( layoutComponent.getProperties().get(
                    FieldLayoutComponent.FIELD_ID ) );
            fieldComponent.init( renderingContext, field );

            layoutComponents.add( fieldComponent );
        }
        return dragComponent;
    }

    public List<FieldLayoutComponent> getLayoutFields() {
        return layoutComponents;
    }

    public FieldLayoutComponent getFieldLayoutComponentForField( FieldDefinition field ) {
        for ( FieldLayoutComponent component : layoutComponents ) {
            if ( component.getField().equals( field ) ) return component;
        }
        return null;
    }
}
