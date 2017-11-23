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

package org.kie.workbench.common.dmn.client.shape.def;

import org.kie.workbench.common.dmn.api.definition.DMNViewDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.Decision;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.dmn.client.resources.DMNSVGViewFactory;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeViewResources;
import org.kie.workbench.common.stunner.svg.client.shape.view.SVGShapeView;

public class DMNSVGShapeDefImpl implements DMNSVGShapeDef<DMNViewDefinition> {

    public static final SVGShapeViewResources<DMNViewDefinition, DMNSVGViewFactory> VIEW_RESOURCES =
            new SVGShapeViewResources<DMNViewDefinition, DMNSVGViewFactory>()
                    .put(BusinessKnowledgeModel.class, DMNSVGViewFactory::businessKnowledgeModel)
                    .put(Decision.class, DMNSVGViewFactory::decision)
                    .put(DMNDiagram.class, DMNSVGViewFactory::diagram)
                    .put(InputData.class, DMNSVGViewFactory::inputData)
                    .put(KnowledgeSource.class, DMNSVGViewFactory::knowledgeSource)
                    .put(TextAnnotation.class, DMNSVGViewFactory::textAnnotation);

    @Override
    public SVGShapeView<?> newViewInstance(final DMNSVGViewFactory factory,
                                           final DMNViewDefinition bean) {
        return VIEW_RESOURCES
                .getResource(factory, bean)
                .build(true);
    }
}
