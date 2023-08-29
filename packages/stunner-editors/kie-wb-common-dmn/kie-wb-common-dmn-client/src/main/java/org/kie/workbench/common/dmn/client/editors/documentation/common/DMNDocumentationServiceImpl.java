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

package org.kie.workbench.common.dmn.client.editors.documentation.common;

import java.util.Optional;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import org.kie.workbench.common.dmn.client.editors.documentation.template.DMNDocumentationTemplateSource;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput;
import org.kie.workbench.common.stunner.core.documentation.model.HTMLDocumentationTemplate;
import org.uberfire.ext.editor.commons.client.template.mustache.ClientMustacheTemplateRenderer;

import static org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput.EMPTY;

@Dependent
public class DMNDocumentationServiceImpl implements DMNDocumentationService {

    private final ClientMustacheTemplateRenderer mustacheTemplateRenderer;

    private final DMNDocumentationFactory dmnDocumentationFactory;

    @Inject
    public DMNDocumentationServiceImpl(final ClientMustacheTemplateRenderer mustacheTemplateRenderer,
                                       final DMNDocumentationFactory dmnDocumentationFactory) {
        this.mustacheTemplateRenderer = mustacheTemplateRenderer;
        this.dmnDocumentationFactory = dmnDocumentationFactory;
    }

    @Override
    public DMNDocumentation processDocumentation(final Diagram diagram) {
        return dmnDocumentationFactory.create(diagram);
    }

    @Override
    public HTMLDocumentationTemplate getDocumentationTemplate() {
        final DMNDocumentationTemplateSource source = GWT.create(DMNDocumentationTemplateSource.class);
        return new HTMLDocumentationTemplate(source.documentationTemplate().getText());
    }

    @Override
    public DocumentationOutput buildDocumentation(final HTMLDocumentationTemplate template,
                                                  final DMNDocumentation diagramDocumentation) {
        final String rendered = mustacheTemplateRenderer.render(template.getTemplate(), diagramDocumentation);
        return new DocumentationOutput(rendered);
    }

    @Override
    public DocumentationOutput generate(final Diagram diagram) {
        return Optional.ofNullable(diagram)
                .map(this::processDocumentation)
                .map(dmnDocumentation -> buildDocumentation(getDocumentationTemplate(), dmnDocumentation))
                .orElse(EMPTY);
    }
}
