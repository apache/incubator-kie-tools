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

package org.kie.workbench.common.forms.jbpm.model.authoring.task;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMVariable;

@Portable
public class TaskVariable implements JBPMVariable {

    protected String name;
    protected String type;
    protected String inputMapping;
    protected String ouputMapping;

    public TaskVariable() {
    }

    public TaskVariable( @MapsTo( "name" ) String name,
                         @MapsTo( "type" ) String type,
                         @MapsTo( "inputMapping" ) String inputMapping,
                         @MapsTo( "outputMapping" ) String ouputMapping ) {
        this.name = name;
        this.type = type;
        this.inputMapping = inputMapping;
        this.ouputMapping = ouputMapping;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType( String type ) {
        this.type = type;
    }

    public String getInputMapping() {
        return inputMapping;
    }

    public void setInputMapping( String inputMapping ) {
        this.inputMapping = inputMapping;
    }

    public String getOuputMapping() {
        return ouputMapping;
    }

    public void setOuputMapping( String ouputMapping ) {
        this.ouputMapping = ouputMapping;
    }
}
