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
package org.kie.workbench.common.dmn.client.resources;

import org.kie.workbench.common.stunner.svg.annotation.SVGSource;
import org.kie.workbench.common.stunner.svg.annotation.SVGViewFactory;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

@SVGViewFactory
public interface DMNSVGViewFactory {

    String DIAGRAM = "images/shapes/diagram.svg";

    String INPUT_DATA = "images/shapes/input-data.svg";

    String KNOWLEDGE_SOURCE = "images/shapes/knowledge-source.svg";

    String BUSINESS_KNOWLEDGE_MODEL = "images/shapes/business-knowledge-model.svg";

    String DECISION = "images/shapes/decision.svg";

    String TEXT_ANNOTATION = "images/shapes/text-annotation.svg";

    @SVGSource(DIAGRAM)
    SVGShapeView diagram(final double width,
                         final double height,
                         final boolean resizable);

    @SVGSource(INPUT_DATA)
    SVGShapeView inputData(final double width,
                           final double height,
                           final boolean resizable);

    @SVGSource(KNOWLEDGE_SOURCE)
    SVGShapeView knowledgeSource(final double width,
                                 final double height,
                                 final boolean resizable);

    @SVGSource(BUSINESS_KNOWLEDGE_MODEL)
    SVGShapeView businessKnowledgeModel(final double width,
                                        final double height,
                                        final boolean resizable);

    @SVGSource(DECISION)
    SVGShapeView decision(final double width,
                          final double height,
                          final boolean resizable);

    @SVGSource(TEXT_ANNOTATION)
    SVGShapeView textAnnotation(final double width,
                                final double height,
                                final boolean resizable);
}

