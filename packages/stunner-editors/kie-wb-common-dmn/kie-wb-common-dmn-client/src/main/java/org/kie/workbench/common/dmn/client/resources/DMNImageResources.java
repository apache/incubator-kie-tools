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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

public interface DMNImageResources extends ClientBundleWithLookup {

    //Connectors
    String ASSOCIATION_TOOLBOX = "images/icons/association-toolbox.png";

    String AUTHORITY_REQUIREMENT_TOOLBOX = "images/icons/authority-requirement-toolbox.png";

    String INFORMATION_REQUIREMENT_TOOLBOX = "images/icons/information-requirement-toolbox.png";

    String KNOWLEDGE_REQUIREMENT_TOOLBOX = "images/icons/knowledge-requirement-toolbox.png";

    //Nodes
    String BUSINESS_KNOWLEDGE_MODEL_TOOLBOX = "images/icons/business-knowledge-model-toolbox.png";

    String DECISION_TOOLBOX = "images/icons/decision-toolbox.png";

    String INPUT_DATA_TOOLBOX = "images/icons/input-data-toolbox.png";

    String KNOWLEDGE_SOURCE_TOOLBOX = "images/icons/knowledge-source-toolbox.png";

    String TEXT_ANNOTATION_TOOLBOX = "images/icons/text-annotation-toolbox.png";

    // Logos
    String DROOLS_LOGO = "images/logos/drools-logo.svg";

    DMNImageResources INSTANCE = GWT.create(DMNImageResources.class);

    String SVG_MIME_TYPE = "image/svg+xml";

    @ClientBundle.Source(DMNSVGViewFactory.DIAGRAM)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource diagramToolbox();

    @ClientBundle.Source(BUSINESS_KNOWLEDGE_MODEL_TOOLBOX)
    ImageResource businessKnowledgeModelToolbox();

    @ClientBundle.Source(DECISION_TOOLBOX)
    ImageResource decisionToolbox();

    @ClientBundle.Source(INPUT_DATA_TOOLBOX)
    ImageResource inputDataToolbox();

    @ClientBundle.Source(KNOWLEDGE_SOURCE_TOOLBOX)
    ImageResource knowledgeSourceToolbox();

    @ClientBundle.Source(TEXT_ANNOTATION_TOOLBOX)
    ImageResource textAnnotationToolbox();

    @ClientBundle.Source(ASSOCIATION_TOOLBOX)
    ImageResource associationToolbox();

    @ClientBundle.Source(AUTHORITY_REQUIREMENT_TOOLBOX)
    ImageResource authorityRequirementToolbox();

    @ClientBundle.Source(INFORMATION_REQUIREMENT_TOOLBOX)
    ImageResource informationRequirementToolbox();

    @ClientBundle.Source(KNOWLEDGE_REQUIREMENT_TOOLBOX)
    ImageResource knowledgeRequirementToolbox();

    @ClientBundle.Source(DMNSVGViewFactory.BUSINESS_KNOWLEDGE_MODEL_PALETTE)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource businessKnowledgeModelPalette();

    @ClientBundle.Source(DMNSVGViewFactory.DECISION_PALETTE)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource decisionPalette();

    @ClientBundle.Source(DMNSVGViewFactory.INPUT_DATA_PALETTE)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource inputDataPalette();

    @ClientBundle.Source(DMNSVGViewFactory.KNOWLEDGE_SOURCE_PALETTE)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource knowledgeSourcePalette();

    @ClientBundle.Source(DMNSVGViewFactory.TEXT_ANNOTATION_PALETTE)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource textAnnotationPalette();

    @ClientBundle.Source(DMNDecisionServiceSVGViewFactory.DECISION_SERVICE_PALETTE)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource decisionServicePalette();

    @ClientBundle.Source(DROOLS_LOGO)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource droolsLogo();
}
