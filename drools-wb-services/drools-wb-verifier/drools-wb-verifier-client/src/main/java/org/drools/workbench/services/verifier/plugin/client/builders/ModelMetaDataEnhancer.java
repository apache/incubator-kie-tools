/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.services.verifier.plugin.client.builders;

import java.util.HashMap;
import java.util.Map;

import org.uberfire.commons.validation.PortablePreconditions;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.services.verifier.plugin.client.api.HeaderMetaData;

public class ModelMetaDataEnhancer {

    private final GuidedDecisionTable52 model;

    public ModelMetaDataEnhancer( final GuidedDecisionTable52 model ) {
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
    }

    public HeaderMetaData getHeaderMetaData() {

        int columnIndex = 0;
        final Map<Integer, Pattern52> map = new HashMap();

        for ( final BaseColumn baseColumn : model.getExpandedColumns() ) {
            if ( baseColumn instanceof ConditionCol52 ) {
                map.put( columnIndex,
                         model.getPattern( (ConditionCol52) baseColumn ) );
            }

            columnIndex++;
        }

        return new HeaderMetaData( map );
    }
}
