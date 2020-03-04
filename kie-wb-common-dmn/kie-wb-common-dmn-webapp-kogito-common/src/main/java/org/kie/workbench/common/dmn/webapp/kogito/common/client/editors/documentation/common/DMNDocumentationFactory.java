/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.editors.documentation.common;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationDRDsFactory;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.uberfire.rpc.SessionInfo;

@Dependent
@Alternative
@ApplicationScoped
public class DMNDocumentationFactory extends org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationFactory {

    @Inject
    public DMNDocumentationFactory(final CanvasFileExport canvasFileExport,
                                   final TranslationService translationService,
                                   final DMNDocumentationDRDsFactory drdsFactory,
                                   final SessionInfo sessionInfo,
                                   final DMNGraphUtils graphUtils) {
        super(canvasFileExport,
              translationService,
              drdsFactory,
              sessionInfo,
              graphUtils);
    }

    @Override
    protected String getCurrentUserName() {
        return null;
    }
}
