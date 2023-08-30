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


package org.kie.workbench.common.forms.adf.service.building;

import org.kie.workbench.common.forms.model.FieldDefinition;

/**
 * Component that modifies the status of a {@link FieldDefinition} depending on the state of a java given Object.
 */
public interface FieldStatusModifier<MODEL> {

    /**
     * Modifies the state of the given field depending on the passed MODEL.
     */
    void modifyFieldStatus(FieldDefinition field,
                           MODEL model);
}
