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


package org.kie.workbench.common.forms.service.shared.meta.processing.impl.processors;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.impl.meta.entries.FieldTypeEntry;
import org.kie.workbench.common.forms.service.shared.meta.processing.MetaDataEntryProcessor;

@Dependent
public class FieldTypeEntryProcessor implements MetaDataEntryProcessor<FieldTypeEntry, FieldDefinition> {

    @Override
    public String getEntryName() {
        return FieldTypeEntry.NAME;
    }

    @Override
    public Class<FieldTypeEntry> getEntryClass() {
        return FieldTypeEntry.class;
    }

    @Override
    public boolean supports(FieldDefinition fieldDefinition) {
        return false; // It shouldn't process anything
    }

    @Override
    public void process(FieldTypeEntry entry, FieldDefinition fieldDefinition) {
        // Does nothing
    }
}
