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

package org.kie.workbench.common.dmn.client.resources;

import org.kie.workbench.common.stunner.core.client.shape.ImageDataUriGlyph;

public interface DMNSVGGlyphFactory {

    ImageDataUriGlyph DIAGRAM_TOOLBOX = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.diagramToolbox().getSafeUri());

    ImageDataUriGlyph BUSINESS_KNOWLEDGE_MODEL_TOOLBOX = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.businessKnowledgeModelToolbox().getSafeUri());

    ImageDataUriGlyph DECISION_TOOLBOX = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.decisionToolbox().getSafeUri());

    ImageDataUriGlyph INPUT_DATA_TOOLBOX = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.inputDataToolbox().getSafeUri());

    ImageDataUriGlyph KNOWLEDGE_SOURCE_TOOLBOX = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.knowledgeSourceToolbox().getSafeUri());

    ImageDataUriGlyph TEXT_ANNOTATION_TOOLBOX = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.textAnnotationToolbox().getSafeUri());

    ImageDataUriGlyph ASSOCIATION_TOOLBOX = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.associationToolbox().getSafeUri());

    ImageDataUriGlyph AUTHORITY_REQUIREMENT_TOOLBOX = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.authorityRequirementToolbox().getSafeUri());

    ImageDataUriGlyph INFORMATION_REQUIREMENT_TOOLBOX = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.informationRequirementToolbox().getSafeUri());

    ImageDataUriGlyph KNOWLEDGE_REQUIREMENT_TOOLBOX = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.knowledgeRequirementToolbox().getSafeUri());

    ImageDataUriGlyph BUSINESS_KNOWLEDGE_MODEL_PALETTE = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.businessKnowledgeModelPalette().getSafeUri());

    ImageDataUriGlyph DECISION_PALETTE = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.decisionPalette().getSafeUri());

    ImageDataUriGlyph INPUT_DATA_PALETTE = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.inputDataPalette().getSafeUri());

    ImageDataUriGlyph KNOWLEDGE_SOURCE_PALETTE = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.knowledgeSourcePalette().getSafeUri());

    ImageDataUriGlyph TEXT_ANNOTATION_PALETTE = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.textAnnotationPalette().getSafeUri());

    ImageDataUriGlyph DECISION_SERVICE_PALETTE = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.decisionServicePalette().getSafeUri());

    ImageDataUriGlyph DROOLS_LOGO = ImageDataUriGlyph.create(DMNImageResources.INSTANCE.droolsLogo().getSafeUri());
}
