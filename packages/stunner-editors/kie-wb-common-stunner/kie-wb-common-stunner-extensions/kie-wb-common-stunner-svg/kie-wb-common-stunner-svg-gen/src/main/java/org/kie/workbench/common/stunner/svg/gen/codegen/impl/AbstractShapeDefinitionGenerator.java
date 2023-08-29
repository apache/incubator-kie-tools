/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.svg.gen.codegen.impl;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.svg.gen.codegen.ShapeDefinitionGenerator;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;
import org.kie.workbench.common.stunner.svg.gen.model.ShapeDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.StyleDefinition;

public abstract class AbstractShapeDefinitionGenerator<I extends ShapeDefinition<?>>
        extends AbstractPrimitiveDefinitionGenerator<I>
        implements ShapeDefinitionGenerator<I> {

    private final static String FILL_COLOR = ".setFillColor(\"%1s\")";
    private final static String FILL_ALPHA = ".setFillAlpha(%1s)";
    private final static String STROKE_COLOR = ".setStrokeColor(\"%1s\")";
    private final static String STROKE_ALPHA = ".setStrokeAlpha(%1s)";
    private final static String STROKE_WIDTH = ".setStrokeWidth(%1s)";
    private final static String STROKE_DASHARRAY = ".setDashArray(%1s)";

    @Override
    public StringBuffer generate(final I input) throws GeneratorException {
        final StringBuffer shapeRaw = super.generate(input);
        // Styles.
        final StyleDefinition styleDefinition = input.getStyleDefinition();
        if (null != styleDefinition) {
            if (null != styleDefinition.getFillColor()) {
                shapeRaw.append(formatString(FILL_COLOR,
                                             styleDefinition.getFillColor()));
            }
            if (null != styleDefinition.getFillAlpha()) {
                shapeRaw.append(formatDouble(FILL_ALPHA,
                                             styleDefinition.getFillAlpha()));
            }
            if (null != styleDefinition.getStrokeColor()) {
                shapeRaw.append(formatString(STROKE_COLOR,
                                             styleDefinition.getStrokeColor()));
            }
            if (null != styleDefinition.getStrokeAlpha()) {
                shapeRaw.append(formatDouble(STROKE_ALPHA,
                                             styleDefinition.getStrokeAlpha()));
            }
            if (null != styleDefinition.getStrokeWidth()) {
                shapeRaw.append(formatDouble(STROKE_WIDTH,
                                             styleDefinition.getStrokeWidth()));
            }
            if (null != styleDefinition.getStrokeDashArray()) {
                String commaSeparatedValues = Stream.of(styleDefinition.getStrokeDashArray()).map(AbstractGenerator::formatDouble).collect(Collectors.joining(","));
                shapeRaw.append(formatString(STROKE_DASHARRAY,
                                             commaSeparatedValues));
            }
            return shapeRaw;
        }
        return new StringBuffer();
    }
}
