/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.model;

import org.guvnor.common.services.shared.metadata.model.Overview;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.commons.validation.PortablePreconditions;

@Portable
public class GuidedDecisionTableEditorGraphContent {

    private GuidedDecisionTableEditorGraphModel model;
    private Overview overview;

    public GuidedDecisionTableEditorGraphContent() {
    }

    public GuidedDecisionTableEditorGraphContent( final GuidedDecisionTableEditorGraphModel model,
                                                  final Overview overview ) {
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
        this.overview = PortablePreconditions.checkNotNull( "overview",
                                                            overview );
    }

    public GuidedDecisionTableEditorGraphModel getModel() {
        return model;
    }

    public Overview getOverview() {
        return overview;
    }

}
