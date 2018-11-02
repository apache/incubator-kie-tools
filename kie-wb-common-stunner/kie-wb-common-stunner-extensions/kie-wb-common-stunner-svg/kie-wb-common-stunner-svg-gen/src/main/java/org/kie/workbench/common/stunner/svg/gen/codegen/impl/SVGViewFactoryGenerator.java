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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.svg.client.shape.view.factory.AbstractSVGViewFactory;
import org.kie.workbench.common.stunner.svg.gen.codegen.ViewFactoryGenerator;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewFactory;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewDefinitionImpl;
import org.uberfire.annotations.processors.exceptions.GenerationException;

public class SVGViewFactoryGenerator
        extends AbstractGenerator
        implements ViewFactoryGenerator {

    public static final String FIELD_STATICS = "VALUE_";

    public static String getStaticFieldValidId(final String id) {
        return FIELD_STATICS + SVGGeneratorFormatUtils.getValidInstanceId(id).toUpperCase();
    }

    @Override
    public StringBuffer generate(final ViewFactory viewFactory) throws GeneratorException {
        final List<StringBuffer> viewBuffers = new LinkedList<>();
        final String name = viewFactory.getSimpleName();
        final String pkg = viewFactory.getPackage();
        final List<ViewDefinition<?>> viewDefinitions = viewFactory.getViewDefinitions();
        final Map<String, String> staticFields = new LinkedHashMap<>();
        viewDefinitions.stream().forEach((viewDefinition) -> {
            try {
                if (viewDefinition instanceof ViewDefinitionImpl) {
                    staticFields.putAll(((ViewDefinitionImpl) viewDefinition).getStaticFields());
                }
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
        Map<String, Object> root = new HashMap<>();
        root.put("genClassName",
                 this.getClass().getName());
        root.put("name",
                 name);
        root.put("pkg",
                 pkg);
        root.put("extendsTypeName",
                 AbstractSVGViewFactory.class.getName());
        root.put("implementsTypeName",
                 viewFactory.getImplementedType());
        root.put("viewBuilder",
                 generateViewBuilderInstance(viewFactory));
        root.put("fmethods",
                 viewsContent);
        root.put("fields",
                 generateStaticFields(staticFields));

        // Generate the code using the given template.
        StringBuffer result;
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

    private List<String> generateStaticFields(final Map<String, String> values) {
        return values.entrySet().stream()
                .map(entry -> "public static final String " +
                        getStaticFieldValidId(entry.getKey()) +
                        " = " +
                        "\"" + entry.getValue() + "\";")
                .collect(Collectors.toList());
    }

    @Override
    protected String getTemplatePath() {
        return "SVGShapeViewFactory";
    }

    private static String generateViewBuilderInstance(ViewFactory viewFactory) {
        return "new " + viewFactory.getViewBuilderType() + "()";
    }
}
