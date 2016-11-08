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

package org.kie.workbench.common.forms.jbpm.model.authoring.process;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.jbpm.model.authoring.AbstractJBPMFormModel;

@Portable
public class BusinessProcessFormModel extends AbstractJBPMFormModel<BusinesProcessVariable> {

    protected String processId;

    protected String processName;

    public BusinessProcessFormModel( @MapsTo( "processId" ) String processId,
                                     @MapsTo( "processName" ) String processName,
                                     @MapsTo( "variables" ) List<BusinesProcessVariable> variables ) {
        super( variables );
        this.processId = processId;
        this.processName = processName;
    }

    @Override
    public String getName() {
        return "process";
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId( String processId ) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName( String processName ) {
        this.processName = processName;
    }
}
