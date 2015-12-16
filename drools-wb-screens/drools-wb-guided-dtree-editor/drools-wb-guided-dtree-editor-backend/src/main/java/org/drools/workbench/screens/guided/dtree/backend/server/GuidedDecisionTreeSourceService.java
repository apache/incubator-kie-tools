/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtree.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.models.guided.dtree.backend.GuidedDecisionTreeDRLPersistence;
import org.drools.workbench.models.guided.dtree.shared.model.GuidedDecisionTree;
import org.drools.workbench.screens.guided.dtree.service.GuidedDecisionTreeEditorService;
import org.drools.workbench.screens.guided.dtree.type.GuidedDTreeResourceTypeDefinition;
import org.kie.workbench.common.services.backend.source.BaseSourceService;
import org.kie.workbench.common.services.shared.source.SourceGenerationFailedException;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class GuidedDecisionTreeSourceService
        extends BaseSourceService<GuidedDecisionTree> {

    @Inject
    private GuidedDTreeResourceTypeDefinition resourceType;

    @Inject
    private GuidedDecisionTreeEditorService guidedDecisionTreeEditorService;

    @Override
    public String getPattern() {
        return resourceType.getSuffix();
    }

    @Override
    public String getSource( final Path path,
                             final GuidedDecisionTree model ) throws SourceGenerationFailedException {
        try {
            return new StringBuilder().append( GuidedDecisionTreeDRLPersistence.getInstance().marshal( model ) ).toString();
        } catch (Exception e) {
            throw new SourceGenerationFailedException(e.getMessage());
        }
    }

    @Override
    public String getSource( final Path path ) throws SourceGenerationFailedException {
        return getSource( path,
                          guidedDecisionTreeEditorService.load( Paths.convert( path ) ) );
    }

}
