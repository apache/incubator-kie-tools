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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundleWithLookup;
import com.google.gwt.resources.client.DataResource;

public interface DMNImageResources extends ClientBundleWithLookup {

    DMNImageResources INSTANCE = GWT.create(DMNImageResources.class);
    String SVG_MIME_TYPE = "image/svg+xml";

    @ClientBundle.Source(DMNSVGViewFactory.DIAGRAM)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource nodes();

    @ClientBundle.Source(DMNSVGViewFactory.DIAGRAM)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource connectors();

    @ClientBundle.Source(DMNSVGViewFactory.DIAGRAM)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource diagram();

    @ClientBundle.Source(DMNSVGViewFactory.INPUT_DATA)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource inputData();

    @ClientBundle.Source(DMNSVGViewFactory.KNOWLEDGE_SOURCE)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource knowledgeSource();

    @ClientBundle.Source(DMNSVGViewFactory.BUSINESS_KNOWLEDGE_MODEL)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource businessKnowledgeModel();

    @ClientBundle.Source(DMNSVGViewFactory.DECISION)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource decision();

    @ClientBundle.Source(DMNSVGViewFactory.TEXT_ANNOTATION)
    @DataResource.MimeType(SVG_MIME_TYPE)
    DataResource textAnnotation();
}
