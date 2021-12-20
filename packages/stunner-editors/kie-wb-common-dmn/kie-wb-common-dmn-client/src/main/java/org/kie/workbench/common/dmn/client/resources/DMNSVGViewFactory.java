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
package org.kie.workbench.common.dmn.client.resources;

import org.kie.workbench.common.stunner.svg.annotation.SVGSource;
import org.kie.workbench.common.stunner.svg.annotation.SVGViewFactory;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeViewResource;

import static org.kie.workbench.common.dmn.client.resources.DMNSVGViewFactory.PATH_CSS;

@SVGViewFactory(cssPath = PATH_CSS)
public interface DMNSVGViewFactory {

    String PATH_CSS = "images/shapes/dmn-shapes.css";

    String DIAGRAM = "images/shapes/diagram.svg";

    String BUSINESS_KNOWLEDGE_MODEL = "images/shapes/business-knowledge-model.svg";

    String BUSINESS_KNOWLEDGE_MODEL_PALETTE = "images/shapes/business-knowledge-model-palette.svg";

    String DECISION = "images/shapes/decision.svg";

    String DECISION_PALETTE = "images/shapes/decision-palette.svg";

    String INPUT_DATA = "images/shapes/input-data.svg";

    String INPUT_DATA_PALETTE = "images/shapes/input-data-palette.svg";

    String KNOWLEDGE_SOURCE = "images/shapes/knowledge-source.svg";

    String KNOWLEDGE_SOURCE_PALETTE = "images/shapes/knowledge-source-palette.svg";

    String TEXT_ANNOTATION = "images/shapes/text-annotation.svg";

    String TEXT_ANNOTATION_PALETTE = "images/shapes/text-annotation-palette.svg";

    @SVGSource(DIAGRAM)
    SVGShapeViewResource diagram();

    @SVGSource(BUSINESS_KNOWLEDGE_MODEL)
    SVGShapeViewResource businessKnowledgeModel();

    @SVGSource(DECISION)
    SVGShapeViewResource decision();

    @SVGSource(INPUT_DATA)
    SVGShapeViewResource inputData();

    @SVGSource(KNOWLEDGE_SOURCE)
    SVGShapeViewResource knowledgeSource();

    @SVGSource(TEXT_ANNOTATION)
    SVGShapeViewResource textAnnotation();
}

