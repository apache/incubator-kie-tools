/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.services.verifier.webworker.client;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.services.verifier.api.client.configuration.CheckConfiguration;
import org.drools.workbench.services.verifier.api.client.reporting.CheckType;

public class CheckConfigurationProvider {

    public static CheckConfiguration get( final GuidedDecisionTable52.HitPolicy hitPolicy ) {

        switch ( hitPolicy ) {
            case RULE_ORDER:
            case FIRST_HIT:
                return getWhiteListWithNoRowToRowChecks();
            case UNIQUE_HIT:
            case NONE:
            default:
                return CheckConfiguration.newDefault();
        }
    }

    private static CheckConfiguration getWhiteListWithNoRowToRowChecks() {
        final CheckConfiguration checkConfiguration = CheckConfiguration.newDefault();

        checkConfiguration.getCheckConfiguration()
                .removeAll( CheckType.getRowLevelCheckTypes() );

        return checkConfiguration;
    }

}
