/*
 * Copyright 2013 JBoss Inc
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

package org.drools.workbench.screens.guided.scorecard.backend.server;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.models.guided.scorecard.backend.GuidedScoreCardDRLPersistence;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.drools.workbench.screens.guided.scorecard.service.GuidedScoreCardEditorService;
import org.drools.workbench.screens.guided.scorecard.type.GuidedScoreCardResourceTypeDefinition;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.file.Path;
import org.kie.workbench.common.services.backend.source.BaseSourceService;

@ApplicationScoped
public class GuidedScoreCardSourceService
        extends BaseSourceService<ScoreCardModel> {

    @Inject
    private GuidedScoreCardResourceTypeDefinition resourceType;

    @Inject
    private GuidedScoreCardEditorService guidedScoreCardEditorService;

    @Override
    public String getPattern() {
        return resourceType.getSuffix();
    }

    @Override
    public String getSource( final Path path,
                             final ScoreCardModel model ) {
        return new StringBuilder().append( GuidedScoreCardDRLPersistence.marshal( model ) ).toString();
    }

    @Override
    public String getSource( final Path path ) {
        return getSource( path,
                          guidedScoreCardEditorService.load( Paths.convert( path ) ) );
    }

}
