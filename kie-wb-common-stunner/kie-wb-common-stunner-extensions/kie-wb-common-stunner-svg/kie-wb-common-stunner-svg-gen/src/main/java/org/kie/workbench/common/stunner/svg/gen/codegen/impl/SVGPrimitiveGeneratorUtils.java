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

import java.util.List;
import java.util.function.Function;

import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import org.kie.workbench.common.stunner.svg.client.shape.view.impl.SVGPrimitivePolicies;
import org.kie.workbench.common.stunner.svg.gen.codegen.PrimitiveDefinitionGenerator;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;
import org.kie.workbench.common.stunner.svg.gen.model.LayoutDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.PrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ShapeDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ShapePolicyDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.impl.GroupDefinition;

public class SVGPrimitiveGeneratorUtils {

    public static final ShapePolicyDefinition DEFAULT_POLICY = ShapePolicyDefinition.NONE;
    public static final String NEW_SVG_CONTAINER_TEMPLATE = "SVGContainer %1s = SVGPrimitiveFactory.newSVGContainer(\"%1s\",%1s, %1s, %1s);";
    public static final String NEW_SVG_SHAPE_TEMPLATE = "SVGPrimitiveShape %1s = SVGPrimitiveFactory.newSVGPrimitiveShape(%1s, %1s, %1s, " +
            "SVGPrimitivePolicies.Builder.build(%1s));";
    public static final String GROUP_ADD_CHILD_TEMPLATE = "%1s.add(%1s);";

    public static String generateSvgPrimitive(final String instanceId,
                                              final Function<PrimitiveDefinition, PrimitiveDefinitionGenerator<PrimitiveDefinition<?>>> generatorProvider,
                                              final PrimitiveDefinition child) {
        String childRaw = null;
        try {
            final StringBuffer childBuffer = generatorProvider.apply(child).generate(child);
            final String scalableRaw = String.valueOf(child.isScalable());
            final LayoutDefinition layoutDefinition = child.getLayoutDefinition();
            final String childLayoutRaw = formatLayout(layoutDefinition);
            if (child instanceof ShapeDefinition) {
                final String policyRaw =
                        formatShapePolicy(((ShapeDefinition) child).getShapePolicyDefinition());
                childRaw = AbstractGenerator.formatString(NEW_SVG_SHAPE_TEMPLATE,
                                                          instanceId,
                                                          childBuffer.toString(),
                                                          scalableRaw,
                                                          childLayoutRaw,
                                                          policyRaw);
            } else if (child instanceof GroupDefinition) {
                final GroupDefinition groupDefinition = (GroupDefinition) child;
                // Generate the group primitive.
                childRaw = AbstractGenerator.formatString(NEW_SVG_CONTAINER_TEMPLATE,
                                                          instanceId,
                                                          groupDefinition.getId(),
                                                          childBuffer.toString(),
                                                          scalableRaw,
                                                          childLayoutRaw);
                // Generate the group children ones.
                final List<PrimitiveDefinition> children = groupDefinition.getChildren();
                for (int j = 0; j < children.size(); j++) {
                    final PrimitiveDefinition childDef = children.get(j);
                    final String childDefInstanceId = instanceId + "_" + j;
                    final String childDefRaw = generateSvgPrimitive(childDefInstanceId,
                                                                    generatorProvider,
                                                                    childDef);
                    if (null != childDefRaw) {
                        childRaw += childDefRaw;
                        childRaw += AbstractGenerator.formatString(GROUP_ADD_CHILD_TEMPLATE,
                                                                   instanceId,
                                                                   childDefInstanceId);
                    }
                }
            }
        } catch (GeneratorException e) {
            throw new RuntimeException(e);
        }

        return childRaw;
    }

    public static String formatShapePolicy(final ShapePolicyDefinition policyDefinition) {
        if (null == policyDefinition) {
            return formatShapePolicy(DEFAULT_POLICY);
        }
        return SVGPrimitivePolicies.class.getName() + "." + policyDefinition.name().toUpperCase();
    }

    public static String formatLayout(final LayoutDefinition layoutDefinition) {
        return null != layoutDefinition && !LayoutDefinition.NONE.equals(layoutDefinition) ?
                LayoutContainer.class.getName() + ".Layout." + layoutDefinition.name().toUpperCase() :
                "null";
    }
}
