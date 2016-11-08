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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.drools.workbench.models.datamodel.oracle.Annotation;
import org.kie.workbench.common.forms.dynamic.backend.server.context.generation.statik.impl.FieldSetting;
import org.kie.workbench.common.forms.dynamic.service.context.generation.TransformerContext;
import org.kie.workbench.common.forms.metaModel.Slider;
import org.kie.workbench.common.forms.model.impl.basic.slider.SliderBase;
import org.kie.workbench.common.forms.service.impl.fieldProviders.SliderFieldProvider;

@Dependent
public class SliderAnnotationProcessor extends AbstractFieldAnnotationProcessor<SliderBase, SliderFieldProvider> {

    @Inject
    public SliderAnnotationProcessor( SliderFieldProvider fieldProvider ) {
        super( fieldProvider );
    }

    @Override
    protected void initField( SliderBase field,
                              Annotation annotation,
                              FieldSetting fieldSetting,
                              TransformerContext context ) {

        field.setMin( ((Number)annotation.getParameters().get( "min" )).doubleValue() );
        field.setMax( ((Number)annotation.getParameters().get( "max" )).doubleValue() );
        field.setPrecision( ((Number)annotation.getParameters().get( "precision" )).doubleValue() );
        field.setStep( ((Number)annotation.getParameters().get( "step" )).doubleValue() );
    }

    @Override
    protected Class<Slider> getSupportedAnnotation() {
        return Slider.class;
    }
}
