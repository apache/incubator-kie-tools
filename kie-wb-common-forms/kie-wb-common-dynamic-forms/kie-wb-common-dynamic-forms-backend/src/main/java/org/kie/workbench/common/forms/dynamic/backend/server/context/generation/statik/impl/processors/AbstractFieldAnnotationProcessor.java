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

package org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.processors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.drools.workbench.models.datamodel.oracle.Annotation;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.FieldSetting;
import org.kie.workbench.common.forms.dynamic.service.context.generation.TransformerContext;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.service.FieldProvider;

public abstract class AbstractFieldAnnotationProcessor<T extends FieldDefinition, P extends FieldProvider<T>> implements FieldAnnotationProcessor<T> {

    protected P fieldProvider;

    @Inject
    public AbstractFieldAnnotationProcessor( P fieldProvider ) {
        this.fieldProvider = fieldProvider;
    }

    @Override
    public T getFieldDefinition( FieldSetting setting, Annotation annotation, TransformerContext context ) {
        T field = fieldProvider.getFieldByType( setting.getTypeInfo() );

        if ( field == null ) {
            return null;
        }

        field.setId( setting.getFieldName() );
        field.setName( setting.getFieldName() );
        field.setLabel( setting.getLabel() );

        String binding = setting.getFieldName();
        if ( !StringUtils.isEmpty( setting.getProperty() ) ) {
            binding += "." + setting.getProperty();
        }

        field.setBinding( binding );

        initField( field, annotation, setting, context );

        return field;
    }

    @Override
    public boolean supportsAnnotation( Annotation annotation ) {
        return annotation.getQualifiedTypeName().equals( getSupportedAnnotation().getName() );
    }

    protected abstract void initField( T field,
                                       Annotation annotation,
                                       FieldSetting fieldSetting,
                                       TransformerContext context );

    protected abstract Class< ? extends java.lang.annotation.Annotation> getSupportedAnnotation();
}
