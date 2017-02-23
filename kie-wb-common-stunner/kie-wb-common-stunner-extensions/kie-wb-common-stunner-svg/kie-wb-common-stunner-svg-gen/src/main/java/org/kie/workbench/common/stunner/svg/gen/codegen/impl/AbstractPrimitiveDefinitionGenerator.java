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

import org.kie.workbench.common.stunner.svg.gen.codegen.PrimitiveDefinitionGenerator;
import org.kie.workbench.common.stunner.svg.gen.exception.GeneratorException;
import org.kie.workbench.common.stunner.svg.gen.model.PrimitiveDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.TransformDefinition;

public abstract class AbstractPrimitiveDefinitionGenerator<I extends PrimitiveDefinition<?>>
        extends AbstractGenerator
        implements PrimitiveDefinitionGenerator<I> {

    private final static String NON_DRAGGABLE = ".setDraggable(false)";
    private final static String ID = ".setID(\"%1s\")";
    private final static String X = ".setX(%1$.3f)";
    private final static String Y = ".setY(%1$.3f)";
    private final static String ALPHA = ".setAlpha(%1$.3f)";
    private final static String SCALE = ".setScale(%1$.3f,%2$.3f)";
    private final static String TRANSLATE = ".setOffset(%1$.3f,%2$.3f)";

    protected abstract StringBuffer doGenerate(final I input) throws GeneratorException;

    @Override
    public StringBuffer generate(final I input) throws GeneratorException {
        final StringBuffer shapeRaw = doGenerate(input);
        // Set primitives as non-draggable.
        appendDraggable(shapeRaw,
                        input);
        // Id.
        appendId(shapeRaw,
                 input);
        // X, Y and Alpha.
        appendCoordinates(shapeRaw,
                          input);
        appendOpacity(shapeRaw,
                      input);
        // Transforms.
        appendTransform(shapeRaw,
                        input);
        return shapeRaw;
    }

    protected void appendDraggable(final StringBuffer buffer,
                                   final I input) {
        buffer.append(NON_DRAGGABLE);
    }

    protected void appendId(final StringBuffer buffer,
                            final I input) {
        final String id = input.getId();
        if (null != id && id.trim().length() > 0) {
            buffer.append(String.format(ID,
                                        id));
        }
    }

    protected void appendCoordinates(final StringBuffer buffer,
                                     final I input) {
        buffer.append(String.format(X,
                                    input.getX()));
        buffer.append(String.format(Y,
                                    input.getY()));
    }

    protected void appendOpacity(final StringBuffer buffer,
                                 final I input) {
        buffer.append(String.format(ALPHA,
                                    input.getAlpha()));
    }

    protected void appendTransform(final StringBuffer buffer,
                                   final I input) {
        final TransformDefinition transformDefinition = input.getTransformDefinition();
        if (null != transformDefinition) {
            buffer.append(String.format(SCALE,
                                        transformDefinition.getScaleX(),
                                        transformDefinition.getScaleY()));
            buffer.append(String.format(TRANSLATE,
                                        transformDefinition.getTranslateX(),
                                        transformDefinition.getTranslateY()));
        }
    }
}
