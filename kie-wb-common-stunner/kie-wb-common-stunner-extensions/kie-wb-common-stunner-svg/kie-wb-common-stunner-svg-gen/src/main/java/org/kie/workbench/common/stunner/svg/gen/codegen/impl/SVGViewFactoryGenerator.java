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

package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.stunner.svg.gen.codegen.ViewFactoryGenerator;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewFactory;
import org.uberfire.annotations.processors.exceptions.GenerationException;

public class SVGViewFactoryGenerator
        extends AbstractGenerator
        implements ViewFactoryGenerator {

    @Override
    public StringBuffer generate(final ViewFactory viewFactory) throws GeneratorException {
        final List<StringBuffer> viewBuffers = new LinkedList<>();
        final String name = viewFactory.getSimpleName();
        final String pkg = viewFactory.getPackage();
        final List<ViewDefinition<?>> viewDefinitions = viewFactory.getViewDefinitions();
        viewDefinitions.stream().forEach((viewDefinition) -> {
            try {
                final StringBuffer viewBuffer = generateView(viewFactory,
                                                             viewDefinition);
                viewBuffers.add(viewBuffer);
            } catch (GeneratorException e) {
                throw new RuntimeException(e);
            }
        });

        // Generate template context.
        final List<String> viewsContent = new LinkedList<>();
        viewBuffers.forEach(b -> viewsContent.add(b.toString()));
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("genClassName",
                 this.getClass().getName());
        root.put("name",
                 name);
        root.put("pkg",
                 pkg);
        root.put("implementedTypeName",
                 viewFactory.getImplementedType());
        root.put("fmethods",
                 viewsContent);

        // Generate the code using the given template.
        StringBuffer result = null;
        try {
            result = writeTemplate(root);
        } catch (final GenerationException e) {
            throw new GeneratorException(e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private StringBuffer generateView(final ViewFactory viewFactory,
                                      final ViewDefinition viewDefinition) throws GeneratorException {
        return ViewGenerators.newShapeViewGenerator().generate(viewFactory,
                                                               viewDefinition);
    }

    @Override
    protected String getTemplatePath() {
        return "SVGShapeViewFactory";
    }
}
