/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.gen.codegen.PrimitiveDefinitionGenerator;
import org.kie.workbench.common.stunner.svg.gen.codegen.ViewDefinitionGenerator;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;
import org.kie.workbench.common.stunner.svg.gen.model.LayoutDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.PrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewFactory;
import org.kie.workbench.common.stunner.svg.gen.model.ViewRefDefinition;
import org.uberfire.annotations.processors.exceptions.GenerationException;

public class SVGViewDefinitionGenerator
        extends AbstractGenerator
        implements ViewDefinitionGenerator<ViewDefinition<SVGShapeView>> {

    private static final String CHILD_TEMPLATE = "view.addSVGChild(\"%1s\", %1s.this.%1sBasicView());";

    @Override
    public StringBuffer generate(final ViewFactory viewFactory,
                                 final ViewDefinition<SVGShapeView> viewDefinition) throws GeneratorException {
        StringBuffer result = null;
        final String factoryName = viewFactory.getSimpleName();
        final String name = viewDefinition.getName();
        final double x = viewDefinition.getX();
        final double y = viewDefinition.getY();
        final double width = viewDefinition.getWidth();
        final double height = viewDefinition.getHeight();
        final PrimitiveDefinition main = viewDefinition.getMain();
        final ViewDefinition.ViewBoxDefinition viewBox = viewDefinition.getViewBox();
        if (null != main) {
            final Map<String, Object> root = new HashMap<String, Object>();
            final List<String> shapes = new LinkedList<>();
            final List<String> scalableShapes = new LinkedList<>();
            // Main shape and children primitives.
            final PrimitiveDefinitionGenerator<PrimitiveDefinition<?>> mainGenerator = getGenerator(main);
            final StringBuffer mainBuffer = mainGenerator.generate(main);
            final List<PrimitiveDefinition> children = viewDefinition.getChildren();
            if (null != children) {
                children.stream()
                        .forEach(child -> {
                            final PrimitiveDefinitionGenerator<PrimitiveDefinition<?>> childGenerator = getGenerator(child);
                            try {
                                final StringBuffer childBuffer = childGenerator.generate(child);
                                if (child.isScalable()) {
                                    scalableShapes.add(childBuffer.toString());
                                } else {
                                    if (null != child.getLayoutDefinition() &&
                                            !child.getLayoutDefinition().equals(LayoutDefinition.NONE)) {
                                        childBuffer.append(", LayoutContainer.Layout.").append(child.getLayoutDefinition().name().toUpperCase());
                                    }
                                    shapes.add(childBuffer.toString());
                                }
                            } catch (GeneratorException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }

            // SVG View children.
            final List<String> rawChildren = new LinkedList<>();
            final List<ViewRefDefinition> svgViewRefs = viewDefinition.getSVGViewRefs();
            svgViewRefs.forEach(viewRef -> {
                final String parent = viewRef.getParent();
                final String svgName = viewRef.getViewName();
                final String viewName = getFactoryName(viewFactory,
                                                       svgName);
                if (null != viewName) {
                    final String childRaw = formatString(CHILD_TEMPLATE,
                                                         parent,
                                                         factoryName,
                                                         viewName);
                    rawChildren.add(childRaw);
                }
            });

            // Populate the context and generate using the template.
            root.put("name",
                     name);
            root.put("main",
                     mainBuffer.toString());
            root.put("width",
                     formatDouble(viewDefinition.getWidth()));
            root.put("height",
                     formatDouble(viewDefinition.getHeight()));
            root.put("children",
                     shapes);
            root.put("scalableChildren",
                     scalableShapes);
            root.put("rawChildren",
                     rawChildren);
            try {
                result = writeTemplate(root);
            } catch (final GenerationException e) {
                throw new GeneratorException(e);
            }
        }

        return result;
    }

    private String getFactoryName(final ViewFactory viewFactory,
                                  final String path) {
        final List<ViewDefinition<?>> viewDefinitions = viewFactory.getViewDefinitions();
        final ViewDefinition<?> d = viewDefinitions.stream()
                .filter(def -> def.getPath().endsWith(path))
                .findFirst()
                .orElse(null);
        if (null != d) {
            return d.getName();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private PrimitiveDefinitionGenerator<PrimitiveDefinition<?>> getGenerator(final PrimitiveDefinition main) {
        final PrimitiveDefinitionGenerator<?>[] array = ViewGenerators.newPrimitiveDefinitionGenerators();
        final List<PrimitiveDefinitionGenerator<?>> list = new LinkedList<PrimitiveDefinitionGenerator<?>>();
        Collections.addAll(list,
                           array);
        return (PrimitiveDefinitionGenerator<PrimitiveDefinition<?>>) list.stream()
                .filter(generator -> generator.getDefinitionType().equals(main.getClass()))
                .findFirst()
                .orElse(null);
    }

    @Override
    protected String getTemplatePath() {
        return "SVGShapeView";
    }
}
