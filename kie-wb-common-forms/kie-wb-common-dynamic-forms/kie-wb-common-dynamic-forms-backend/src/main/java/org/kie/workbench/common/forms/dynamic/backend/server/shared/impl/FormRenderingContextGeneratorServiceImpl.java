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

package org.kie.workbench.common.forms.dynamic.backend.server.shared.impl;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.dynamic.service.context.generation.DynamicGenerator;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.context.generation.FormRenderingContextGenerator;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContextGeneratorService;
import org.kie.workbench.common.forms.dynamic.service.context.generation.StaticGenerator;
import org.kie.workbench.common.forms.dynamic.service.context.generation.TransformerContext;

@Dependent
@Service
public class FormRenderingContextGeneratorServiceImpl implements FormRenderingContextGeneratorService {

    protected FormRenderingContextGenerator staticContextGenerator;
    protected FormRenderingContextGenerator dynamicContextGenerator;

    @Inject
    public FormRenderingContextGeneratorServiceImpl( @StaticGenerator FormRenderingContextGenerator<? extends TransformerContext<?>, ? extends FormRenderingContext> staticContextGenerator,
                                                     @DynamicGenerator FormRenderingContextGenerator<? extends TransformerContext<?>, ? extends FormRenderingContext> dynamicContextGenerator ) {
        // There could be two different context generator for static models & dynamic
        this.staticContextGenerator = staticContextGenerator;
        this.dynamicContextGenerator = dynamicContextGenerator;
    }

    @Override
    public FormRenderingContext createContext( Object model ) {
        // right now we don't support dynamic forms so static conversion will be incharged of doing everything.
        return staticContextGenerator.createContext( model );
    }
}
