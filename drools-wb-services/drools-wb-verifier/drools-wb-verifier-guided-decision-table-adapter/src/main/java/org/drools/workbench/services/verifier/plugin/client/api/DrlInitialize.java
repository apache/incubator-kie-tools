/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.plugin.client.api;

import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.verifier.api.client.api.Initialize;

@Portable
public class DrlInitialize
        implements Initialize {

    private final String uuid;
    private final GuidedDecisionTable52 model;
    private final String dateFormat;
    private final HeaderMetaData headerMetaData;
    private final FactTypes factTypes;

    public DrlInitialize(@MapsTo("uuid") final String uuid,
                         @MapsTo("model") final GuidedDecisionTable52 model,
                         @MapsTo( "headerMetaData" ) HeaderMetaData headerMetaData,
                         @MapsTo("factTypes") final FactTypes factTypes,
                         @MapsTo("dateFormat") final String dateFormat ) {
        this.uuid = uuid;
        this.model = model;
        this.headerMetaData = headerMetaData;
        this.factTypes = factTypes;
        this.dateFormat = dateFormat;
    }

    public String getUuid() {
        return uuid;
    }

    public GuidedDecisionTable52 getModel() {
        return model;
    }

    public HeaderMetaData getHeaderMetaData() {
        return headerMetaData;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public FactTypes getFactTypes() {
        return factTypes;
    }

}
