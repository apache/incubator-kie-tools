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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.client.lienzo.shape.impl.ShapeStateDefaultHandler;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.gen.codegen.PrimitiveDefinitionGenerator;
import org.kie.workbench.common.stunner.svg.gen.codegen.ViewDefinitionGenerator;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;
import org.kie.workbench.common.stunner.svg.gen.model.LayoutDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.PrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ShapeDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.StyleSheetDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewFactory;
import org.kie.workbench.common.stunner.svg.gen.model.ViewRefDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.SVGModelUtils;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewDefinitionImpl;
import org.uberfire.annotations.processors.exceptions.GenerationException;

public class SVGViewDefinitionGenerator
        extends AbstractGenerator
        implements ViewDefinitionGenerator<ViewDefinition<SVGShapeView>> {

    public static final String PRIM_CHILD_TEMPLATE = "view.addChild(%1s);";
    private static final String SVG_CHILD_TEMPLATE = "view.addSVGChild(%1s, %1s.this.%1sBasicView());";

    @Override
    @SuppressWarnings("unchecked")
    public StringBuffer generate(final ViewFactory viewFactory,
                                 final ViewDefinition<SVGShapeView> viewDefinition) throws GeneratorException {
        StringBuffer result = null;
        final String factoryName = viewFactory.getSimpleName();
        final String viewId = viewDefinition.getId();
        final String methodName = viewDefinition.getFactoryMethodName();
        final ShapeDefinition main = viewDefinition.getMain();
        if (null != main) {
            final Map<String, Object> root = new HashMap<>();

            // Generate the children primitives.
            final List<String> childrenRaw = new LinkedList<>();
            final List<PrimitiveDefinition> children = viewDefinition.getChildren();
            for (final PrimitiveDefinition child : children) {
                final String childId = SVGGeneratorFormatUtils.getValidInstanceId(child);
                String childRaw =
                        SVGPrimitiveGeneratorUtils
                                .generateSvgPrimitive(childId,
                                                      SVGViewDefinitionGenerator::getGenerator,
                                                      child);
                if (null != childRaw) {
                    childrenRaw.add(childRaw);
                    childrenRaw.add(AbstractGenerator.formatString(PRIM_CHILD_TEMPLATE,
                                                                   childId));
                }
            }

            // SVG View children.
            final List<String> svgChildrenRaw = new LinkedList<>();
            final List<ViewRefDefinition> svgViewRefs = viewDefinition.getSVGViewRefs();
            svgViewRefs.forEach(viewRef -> {
                final String parent = viewRef.getParent();
                final String svgName = viewRef.getFilePath();
                final String viewRefId = viewRef.getViewRefId();
                final boolean existReferencedView = viewFactory
                        .getViewDefinitions().stream()
                        .anyMatch(def -> viewRefId.equals(def.getId()));
                if (existReferencedView) {
                    final String childRaw = formatString(SVG_CHILD_TEMPLATE,
                                                         parent,
                                                         factoryName,
                                                         viewRefId);
                    svgChildrenRaw.add(childRaw);
                } else {
                    throw new RuntimeException("The view [" + viewRefId + "] references " +
                                                       "another the view [" + svgName + "], but no factory method " +
                                                       "for it exists in [" + viewFactory.getImplementedType() + "]");
                }
            });

            // Look for the state shape view.
            final List<ShapeDefinition> stateViews = new LinkedList<>();
            SVGModelUtils.visit(viewDefinition,
                                p -> {
                                    if (p instanceof ShapeDefinition && p.getAlpha() > 0) {
                                        final ShapeDefinition shapeDefinition = (ShapeDefinition) p;
                                        shapeDefinition.getStateDefinition()
                                                .ifPresent(s -> stateViews.add((ShapeDefinition) p));
                                    }
                                });

            final String stateViewIds = stateViews.isEmpty() ? "view" : stateViews.stream()
                    .map(d -> SVGGeneratorFormatUtils.getValidInstanceId(d.getId()))
                    .collect(Collectors.joining(","));
            final String stateViewPolicyType = stateViews.isEmpty() ?
                    ShapeStateDefaultHandler.RenderType.STROKE.name() :
                    ((ShapeDefinition.ShapeStateDefinition) stateViews.get(0).getStateDefinition().get()).name();
            final String stateViewPolicy = ShapeStateDefaultHandler.RenderType.class.getName().replace("$", ".") +
                    "." + stateViewPolicyType.toUpperCase();
            // Generate the main shape.
            final PrimitiveDefinitionGenerator<PrimitiveDefinition<?>> mainGenerator = getGenerator(main);
            final StringBuffer mainBuffer = mainGenerator.generate(main);
            final LayoutDefinition mainLayoutDefinition = main.getLayoutDefinition();
            final String mainLayoutRaw = SVGPrimitiveGeneratorUtils.formatLayout(mainLayoutDefinition);

            // Generate the view's text styling stuff.
            final StyleSheetDefinition globalStyleSheetDefinition =
                    ((ViewDefinitionImpl) viewDefinition).getGlobalStyleSheetDefinition();
            final String viewTextRaw = null != globalStyleSheetDefinition ?
                    SVGShapeTextCodeBuilder.generate("view",
                                                     viewId,
                                                     globalStyleSheetDefinition) : "";

            // Populate the context and generate using the template.
            root.put("viewId",
                     viewId);
            root.put("name",
                     methodName);
            root.put("mainShape",
                     mainBuffer.toString());
            root.put("layout",
                     mainLayoutRaw);
            root.put("width",
                     formatDouble(viewDefinition.getWidth()));
            root.put("height",
                     formatDouble(viewDefinition.getHeight()));
            root.put("text",
                     viewTextRaw);
            root.put("stateViewIds",
                     stateViewIds);
            root.put("stateViewPolicy",
                     stateViewPolicy);
            root.put("children",
                     childrenRaw);
            root.put("svgChildren",
                     svgChildrenRaw);
            try {
                result = writeTemplate(root);
            } catch (final GenerationException e) {
                throw new GeneratorException(e);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private static PrimitiveDefinitionGenerator<PrimitiveDefinition<?>> getGenerator(final PrimitiveDefinition main) {
        final PrimitiveDefinitionGenerator<?>[] array = ViewGenerators.newPrimitiveDefinitionGenerators();
        final List<PrimitiveDefinitionGenerator<?>> list = new LinkedList<>();
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
