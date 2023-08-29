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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import java.util.HashMap;
import java.util.Map;

import org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor.AssignmentsEditorWidget;

/**
 * Utils class extracted from the {@link AssignmentsEditorWidget}.
 */
public class AssignmentParser {

    public static final String DATAINPUT = "datainput";
    public static final String DATAINPUTSET = "datainputset";
    public static final String DATAOUTPUT = "dataoutput";
    public static final String DATAOUTPUTSET = "dataoutputset";
    public static final String ASSIGNMENTS = "assignments";

    public static Map<String, String> parseAssignmentsInfo(String assignmentsInfo) {
        Map<String, String> properties = new HashMap<String, String>();
        if (assignmentsInfo != null) {
            String[] parts = assignmentsInfo.split("\\|");
            if (parts.length > 0 && parts[0] != null && parts[0].length() > 0) {
                properties.put(DATAINPUT,
                               parts[0]);
            }
            if (parts.length > 1 && parts[1] != null && parts[1].length() > 0) {
                properties.put(DATAINPUTSET,
                               parts[1]);
            }
            if (parts.length > 2 && parts[2] != null && parts[2].length() > 0) {
                properties.put(DATAOUTPUT,
                               parts[2]);
            }
            if (parts.length > 3 && parts[3] != null && parts[3].length() > 0) {
                properties.put(DATAOUTPUTSET,
                               parts[3]);
            }
            if (parts.length > 4 && parts[4] != null && parts[4].length() > 0) {
                properties.put(ASSIGNMENTS,
                               parts[4]);
            }
        }
        return properties;
    }
}
