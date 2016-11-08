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

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.datamodel.oracle.Annotation;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.FieldSetting;
import org.kie.workbench.common.forms.dynamic.service.shared.SelectorDataProviderManager;
import org.kie.workbench.common.forms.dynamic.service.context.generation.TransformerContext;
import org.kie.workbench.common.forms.metaModel.Option;
import org.kie.workbench.common.forms.metaModel.SelectorDataProvider;
import org.kie.workbench.common.forms.model.impl.basic.selectors.SelectorFieldBase;
import org.kie.workbench.common.forms.model.impl.basic.selectors.SelectorOption;
import org.kie.workbench.common.forms.model.impl.basic.selectors.StringSelectorOption;
import org.kie.workbench.common.forms.service.impl.fieldProviders.SelectorFieldProvider;

public abstract class AbstractSelectorAnnotationProcessor<T extends SelectorFieldBase, P extends SelectorFieldProvider<T>>
        extends AbstractFieldAnnotationProcessor<T, P> {

    public AbstractSelectorAnnotationProcessor( P fieldProvider ) {
        super( fieldProvider );
    }

    @Override
    protected void initField( T field, Annotation annotation, FieldSetting fieldSetting, TransformerContext context ) {
        List<SelectorOption> options = new ArrayList<>();
        Annotation selectorProvider = null;
        for ( Annotation settingAnnotation : fieldSetting.getAnnotations() ) {
            if ( settingAnnotation.getQualifiedTypeName().equals( Option.class.getName() ) ) {
                StringSelectorOption selectorOption = new StringSelectorOption();
                selectorOption.setValue( settingAnnotation.getParameters().get( "value" ).toString() );
                selectorOption.setText( settingAnnotation.getParameters().get( "text" ).toString() );
                selectorOption.setDefaultValue( (Boolean) settingAnnotation.getParameters().get( "isDefault" ) );
                options.add( selectorOption );
            } else if ( settingAnnotation.getQualifiedTypeName().equals( SelectorDataProvider.class.getName() ) ) {
                selectorProvider = settingAnnotation;
            }
        }

        field.setOptions( options );

        if ( options.isEmpty() && selectorProvider != null ) {
            String providerId = selectorProvider.getParameters().get( "type" ) + SelectorDataProviderManager.SEPARATOR + selectorProvider.getParameters().get( "className" );
            field.setDataProvider( providerId );
        }
    }
}
