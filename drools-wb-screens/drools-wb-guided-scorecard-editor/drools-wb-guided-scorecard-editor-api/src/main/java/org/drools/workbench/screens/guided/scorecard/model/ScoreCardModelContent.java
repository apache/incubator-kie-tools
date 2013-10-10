/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.screens.guided.scorecard.model;

import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;

@Portable
public class ScoreCardModelContent {

    private ScoreCardModel model;
    private PackageDataModelOracleBaselinePayload dataModel;

    public ScoreCardModelContent() {
    }

    public ScoreCardModelContent( final ScoreCardModel model,
                                  final PackageDataModelOracleBaselinePayload dataModel ) {
        this.model = model;
        this.dataModel = dataModel;
    }

    public ScoreCardModel getModel() {
        return this.model;
    }

    public PackageDataModelOracleBaselinePayload getDataModel() {
        return this.dataModel;
    }

}
