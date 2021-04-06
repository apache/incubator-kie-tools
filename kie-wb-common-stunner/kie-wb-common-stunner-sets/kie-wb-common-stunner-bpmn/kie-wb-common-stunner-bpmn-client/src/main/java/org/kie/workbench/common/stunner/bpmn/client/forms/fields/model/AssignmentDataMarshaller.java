/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import org.jboss.errai.common.client.protocols.SerializationParts;
import org.jboss.errai.marshalling.client.api.MarshallingSession;
import org.jboss.errai.marshalling.client.api.annotations.ClientMarshaller;
import org.jboss.errai.marshalling.client.api.annotations.ServerMarshaller;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.marshallers.AbstractNullableMarshaller;

@ClientMarshaller(AssignmentData.class)
@ServerMarshaller(AssignmentData.class)
public class AssignmentDataMarshaller
        extends AbstractNullableMarshaller<AssignmentData> {

    public static final String INPUT_VARIABLES = "inputVariables";
    public static final String OUTPUT_VARIABLES = "outputVariables";
    public static final String PROCESS_VARIABLES = "processVariables";
    public static final String ASSIGNMENTS = "assignments";
    public static final String DATA_TYPES = "dataTypes";
    public static final String DISALLOWED_PROPERTY_NAMES = "disallowedPropertyNames";
    public static final String VARIABLE_COUNTS_STRING = "variablecountsstring";

    public AssignmentData doNotNullDemarshall(final EJValue o,
                                              final MarshallingSession ctx) {
        EJObject obj = o.isObject();
        String inputVariables = obj.get(INPUT_VARIABLES).isString().stringValue();
        String outputVariables = obj.get(OUTPUT_VARIABLES).isString().stringValue();
        String processVariables = obj.get(PROCESS_VARIABLES).isString().stringValue();
        String assignments = obj.get(ASSIGNMENTS).isString().stringValue();
        String varCounts = obj.get(VARIABLE_COUNTS_STRING).isString().stringValue();
        String dataTypes = obj.get(DATA_TYPES).isString().stringValue();
        String disallowedPropertyNames = obj.get(DISALLOWED_PROPERTY_NAMES).isString().stringValue();
        AssignmentData data = new AssignmentData(inputVariables,
                                                 outputVariables,
                                                 processVariables,
                                                 assignments,
                                                 dataTypes,
                                                 disallowedPropertyNames);
        data.setVariableCountsString(varCounts);
        return data;
    }

    public String doNotNullMarshall(final AssignmentData o,
                                    final MarshallingSession ctx) {
        return "{\"" + SerializationParts.ENCODED_TYPE + "\":\"" + AssignmentData.class.getName() + "\"," +
                "\"" + SerializationParts.OBJECT_ID + "\":\"" + o.hashCode() + "\"," +
                "\"" + INPUT_VARIABLES + "\":\"" + o.getInputVariablesString() + "\"," +
                "\"" + OUTPUT_VARIABLES + "\":\"" + o.getOutputVariablesString() + "\"," +
                "\"" + PROCESS_VARIABLES + "\":\"" + o.getProcessVariablesString() + "\"," +
                "\"" + ASSIGNMENTS + "\":\"" + o.getAssignmentsString() + "\"," +
                "\"" + VARIABLE_COUNTS_STRING + "\":\"" + o.getVariableCountsString() + "\"," +
                "\"" + DATA_TYPES + "\":\"" + o.getDataTypesString() + "\"," +
                "\"" + DISALLOWED_PROPERTY_NAMES + "\":\"" + o.getDisallowedPropertyNamesString() + "\"}";
    }

    @Override
    public AssignmentData[] getEmptyArray() {
        return new AssignmentData[0];
    }
}
