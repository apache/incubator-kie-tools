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
import java.util.stream.IntStream;

import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;
import org.kie.workbench.common.stunner.svg.gen.codegen.PrimitiveDefinitionGenerator;
import org.kie.workbench.common.stunner.svg.gen.codegen.ViewDefinitionGenerator;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;
import org.kie.workbench.common.stunner.svg.gen.model.LayoutDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.PrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ShapeDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ShapePolicyDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.StyleSheetDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewFactory;
import org.kie.workbench.common.stunner.svg.gen.model.ViewRefDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.ViewDefinitionImpl;
import org.uberfire.annotations.processors.exceptions.GenerationException;

public class SVGViewDefinitionGenerator
        extends AbstractGenerator
        implements ViewDefinitionGenerator<ViewDefinition<SVGShapeView>> {

    private static final String CHILD_OBJECT_ID = "child";
    public static final String PRIM_CHILD_TEMPLATE = "view.addChild(%1s);";
    private static final String SVG_CHILD_TEMPLATE = "view.addSVGChild(%1s, %1s.this.%1sBasicView());";

    @Override
    public StringBuffer generate(final ViewFactory viewFactory,
                                 final ViewDefinition<SVGShapeView> viewDefinition) throws GeneratorException {
        StringBuffer result = null;
        final String factoryName = viewFactory.getSimpleName();
        final String viewId = viewDefinition.getId();
        final String methodName = viewDefinition.getFactoryMethodName();
        final double x = viewDefinition.getX();
        final double y = viewDefinition.getY();
        final double width = viewDefinition.getWidth();
        final double height = viewDefinition.getHeight();
        final ShapeDefinition main = viewDefinition.getMain();
        final ViewDefinition.ViewBoxDefinition viewBox = viewDefinition.getViewBox();
        if (null != main) {
            final Map<String, Object> root = new HashMap<String, Object>();

            // Generate the children primitives.
            final List<String> childrenRaw = new LinkedList<>();
            final List<PrimitiveDefinition> children = viewDefinition.getChildren();
            for (int i = 0; i < children.size(); i++) {
                final PrimitiveDefinition child = children.get(i);
                final String childId = CHILD_OBJECT_ID + i;
                String childRaw =
                        SVGPrimitiveGeneratorUtils
                                .generateSvgPrimitive(childId,
                                                      SVGViewDefinitionGenerator::getGenerator,
                                                      child);
                if (null != childRaw) {
                    childRaw += AbstractGenerator.formatString(PRIM_CHILD_TEMPLATE,
                                                               childId);
                } else {
                    childRaw = "";
                }
                childrenRaw.add(childRaw);
            }

            // SVG View children.
            final List<String> svgChildrenRaw = new LinkedList<>();
            final List<ViewRefDefinition> svgViewRefs = viewDefinition.getSVGViewRefs();
            svgViewRefs.forEach(viewRef -> {
                final String parent = viewRef.getParent();
                final int parentIndex = getChildrenIndex(viewDefinition, parent);
                if (parentIndex < 0) {
                    throw new IllegalArgumentException("No parent found with id [" + parent + "]");
                }
                final String svgName = viewRef.getFilePath();
                final String viewRefId = viewRef.getViewRefId();
                final boolean existReferencedView = viewFactory
                        .getViewDefinitions().stream()
                        .anyMatch(def -> viewRefId.equals(def.getId()));
                if (existReferencedView) {
                    final String childRaw = formatString(SVG_CHILD_TEMPLATE,
                                                         CHILD_OBJECT_ID + parentIndex,
                                                         factoryName,
                                                         viewRefId);
                    svgChildrenRaw.add(childRaw);
                } else {
                    throw new RuntimeException("The view [" + viewRefId + "] references " +
                                                       "another the view [" + svgName + "], but no factory method " +
                                                       "for it exists in [" + viewFactory.getImplementedType() + "]");
                }
            });

            // Generate the main shape.
            final PrimitiveDefinitionGenerator<PrimitiveDefinition<?>> mainGenerator = getGenerator(main);
            final StringBuffer mainBuffer = mainGenerator.generate(main);
            final LayoutDefinition mainLayoutDefinition = main.getLayoutDefinition();
            final String mainLayoutRaw = SVGPrimitiveGeneratorUtils.formatLayout(mainLayoutDefinition);
            final ShapePolicyDefinition policy = main.getShapePolicyDefinition();
            final String mainPolicyRaw = SVGPrimitiveGeneratorUtils.formatShapePolicy(policy);

            // Generate the view shape states styling stuff.
            final String shapeStateHoldersRaw =
                    SVGShapeStateHolderCodeBuilder.generateStateHolders("view",
                                                                        viewDefinition.getShapeStateDefinition());

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
            root.put("policy",
                     mainPolicyRaw);
            root.put("width",
                     formatDouble(viewDefinition.getWidth()));
            root.put("height",
                     formatDouble(viewDefinition.getHeight()));
            root.put("stateHolders",
                     shapeStateHoldersRaw);
            root.put("text",
                     viewTextRaw);
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

    private static int getChildrenIndex(final ViewDefinition<SVGShapeView> viewDefinition,
                                        final String id) {
        final List<PrimitiveDefinition> children = viewDefinition.getChildren();
        return IntStream.range(0, children.size())
                .filter(i -> id.equals(children.get(i).getId()))
                .findFirst()
                .orElse(-1);
    }

    @SuppressWarnings("unchecked")
    private static PrimitiveDefinitionGenerator<PrimitiveDefinition<?>> getGenerator(final PrimitiveDefinition main) {
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
