/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNPropertySet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@FormDefinition
public class BaseSubprocessTaskExecutionSet implements BPMNPropertySet {

    @Property
    @FormField
    @Valid
    private IsAsync isAsync;

    @Property
    @FormField(afterElement = "isAsync")
    @Valid
    protected SLADueDate slaDueDate;

    public BaseSubprocessTaskExecutionSet() {
        this(new IsAsync(),
             new SLADueDate());
    }

    public BaseSubprocessTaskExecutionSet(final @MapsTo("isAsync") IsAsync isAsync,
                                          final @MapsTo("slaDueDate") SLADueDate slaDueDate) {
        this.isAsync = isAsync;
        this.slaDueDate = slaDueDate;
    }

    public IsAsync getIsAsync() {
        return isAsync;
    }

    public void setIsAsync(IsAsync isAsync) {
        this.isAsync = isAsync;
    }

    public SLADueDate getSlaDueDate() {
        return slaDueDate;
    }

    public void setSlaDueDate(final SLADueDate slaDueDate) {
        this.slaDueDate = slaDueDate;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(isAsync),
                                         Objects.hashCode(slaDueDate));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BaseSubprocessTaskExecutionSet) {
            BaseSubprocessTaskExecutionSet other = (BaseSubprocessTaskExecutionSet) o;
            return Objects.equals(isAsync, other.isAsync) &&
                    Objects.equals(slaDueDate, other.slaDueDate);
        }
        return false;
    }
}
