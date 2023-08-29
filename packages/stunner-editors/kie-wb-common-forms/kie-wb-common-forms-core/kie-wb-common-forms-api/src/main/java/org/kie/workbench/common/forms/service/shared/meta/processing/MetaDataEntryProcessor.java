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


package org.kie.workbench.common.forms.service.shared.meta.processing;

import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.MetaDataEntry;

/**
 * Processes the value of a specific type of {@link MetaDataEntry} and initalizes a {@link FieldDefinition} with it.
 * @param <ENTRY> any type of {@link MetaDataEntry}
 * @param <FIELD> any type of {@link FieldDefinition}
 */
public interface MetaDataEntryProcessor<ENTRY extends MetaDataEntry, FIELD> {

    /**
     * Returns the name of the {@link MetaDataEntry} supported by this processor
     */
    String getEntryName();

    /**
     * Returns the Class of the {@link MetaDataEntry} supported by this processor
     * @return
     */
    Class<ENTRY> getEntryClass();

    /**
     * Processes the given entry and initializes the field with its value.
     * @param entry any type of {@link MetaDataEntry}
     * @param field any type of {@link FieldDefinition}
     */
    void process(ENTRY entry, FIELD field);

    /**
     * Determines if the processor supports the given {@link FieldDefinition}.
     * @param fieldDefinition any type of {@link FieldDefinition}
     * @return True if the processor supports it or false if not.
     */
    default boolean supports(FieldDefinition fieldDefinition) {
        return true;
    }
}
