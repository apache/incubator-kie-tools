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

import java.util.Objects;

import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;

import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.isEmpty;

public class Assignment {

    private Variable variable;

    AssignmentData assignmentData;

    /*
        Assignments have either a processVar or a constant
     */
    private Variable processVar;
    private String expression;

    private static final String INPUT_ASSIGNMENT_PREFIX = "[din]";
    private static final String OUTPUT_ASSIGNMENT_PREFIX = "[dout]";
    private static final String ASSIGNMENT_OPERATOR_TOVARIABLE = "->";
    private static final String ASSIGNMENT_OPERATOR_TOCONSTANT = "=";

    public Assignment() {
    }

    public Assignment(final AssignmentData assignmentData,
                      final String variableName,
                      final Variable.VariableType variableType,
                      final String dataType,
                      final String customDataType,
                      final String processVarName,
                      final String expression) {
        this.assignmentData = assignmentData;
        variable = assignmentData.findVariable(variableName,
                                               variableType);
        if (variable == null) {
            variable = new Variable(variableName,
                                    variableType,
                                    dataType,
                                    customDataType);
            assignmentData.addVariable(variable);
        }
        this.processVar = assignmentData.findProcessVariable(processVarName);
        this.expression = expression;
    }

    public Assignment(final AssignmentData assignmentData,
                      final String variableName,
                      final Variable.VariableType variableType,
                      final String processVarName,
                      final String expression) {
        this.assignmentData = assignmentData;
        variable = assignmentData.findVariable(variableName,
                                               variableType);
        if (variable == null) {
            variable = new Variable(variableName,
                                    variableType);
            assignmentData.addVariable(variable);
        }
        processVar = assignmentData.findProcessVariable(processVarName);
        this.expression = expression;
    }

    public String getName() {
        return variable.getName();
    }

    public void setName(final String name) {
        variable.setName(name);
    }

    public Variable.VariableType getVariableType() {
        return variable.getVariableType();
    }

    public void setVariableType(final Variable.VariableType variableType) {
        variable.setVariableType(variableType);
    }

    public Variable getVariable() {
        return variable;
    }

    public String getDataType() {
        return variable.getDataType();
    }

    public void setDataType(final String dataType) {
        variable.setDataType(dataType);
    }

    public String getCustomDataType() {
        return variable.getCustomDataType();
    }

    public void setCustomDataType(final String customDataType) {
        variable.setCustomDataType(customDataType);
    }

    public String getProcessVarName() {
        return ((processVar != null) ? processVar.getName() : null);
    }

    public void setProcessVarName(final String processVarName) {
        this.processVar = assignmentData.findProcessVariable(processVarName);
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(final String expression) {
        this.expression = expression;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Assignment)) {
            return false;
        }
        Assignment that = (Assignment) o;

        return Objects.equals(expression, that.expression)
                && Objects.equals(variable, that.variable)
                && Objects.equals(processVar, that.processVar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, processVar, expression);
    }

    /**
     * Serializes assignment
     * e.g. e.g. [din]str1->inStr, [din]inStrConst=TheString, [dout]outStr1->str1
     * @return serialized Assignment
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getVariableType() == Variable.VariableType.INPUT) {
            if (getExpression() != null && !getExpression().isEmpty()) {
                sb.append(INPUT_ASSIGNMENT_PREFIX).append(getName()).append(ASSIGNMENT_OPERATOR_TOCONSTANT).append(
                        StringUtils.urlEncode(getExpression()));
            } else if (getProcessVarName() != null && !getProcessVarName().isEmpty()) {
                sb.append(INPUT_ASSIGNMENT_PREFIX).append(getProcessVarName()).append(ASSIGNMENT_OPERATOR_TOVARIABLE).append(getName());
            } else {
                sb.append(INPUT_ASSIGNMENT_PREFIX).append(ASSIGNMENT_OPERATOR_TOVARIABLE).append(getName());
            }
        } else {
            if (!isEmpty(getExpression())) {
                sb.append(OUTPUT_ASSIGNMENT_PREFIX).append(StringUtils.urlEncode(getExpression())).append(ASSIGNMENT_OPERATOR_TOCONSTANT).append(getName());
            } else if (getProcessVarName() != null && !getProcessVarName().isEmpty()) {
                sb.append(OUTPUT_ASSIGNMENT_PREFIX).append(getName()).append(ASSIGNMENT_OPERATOR_TOVARIABLE).append(getProcessVarName());
            } else {
                sb.append(OUTPUT_ASSIGNMENT_PREFIX).append(getName()).append(ASSIGNMENT_OPERATOR_TOVARIABLE);
            }
        }
        return sb.toString();
    }

    /**
     * Deserializes an assignment string
     * e.g. [din]str1->inStr, [din]inStrConst=TheString, [dout]outStr1->str1
     * @param assignmentData context of assignment
     * @param sAssignment serialized Assignment
     * @return Assignment
     */
    public static Assignment deserialize(final AssignmentData assignmentData,
                                         String sAssignment) {
        if (sAssignment == null || sAssignment.isEmpty()) {
            return null;
        }
        // Parse the assignment string
        Variable.VariableType assignmentType = null;
        if (sAssignment.startsWith(INPUT_ASSIGNMENT_PREFIX)) {
            assignmentType = Variable.VariableType.INPUT;
            sAssignment = sAssignment.substring(INPUT_ASSIGNMENT_PREFIX.length());
        } else if (sAssignment.startsWith(OUTPUT_ASSIGNMENT_PREFIX)) {
            assignmentType = Variable.VariableType.OUTPUT;
            sAssignment = sAssignment.substring(OUTPUT_ASSIGNMENT_PREFIX.length());
        }
        String variableName = null;
        String processVariableName = null;
        String constant = null;
        if (sAssignment.contains(ASSIGNMENT_OPERATOR_TOVARIABLE)) {
            int i = sAssignment.indexOf(ASSIGNMENT_OPERATOR_TOVARIABLE);
            if (assignmentType == Variable.VariableType.INPUT) {
                processVariableName = sAssignment.substring(0,
                                                            i);
                variableName = sAssignment.substring(i + ASSIGNMENT_OPERATOR_TOVARIABLE.length());
            } else {
                variableName = sAssignment.substring(0,
                                                     i);
                processVariableName = sAssignment.substring(i + ASSIGNMENT_OPERATOR_TOVARIABLE.length());
            }
        } else if (sAssignment.contains(ASSIGNMENT_OPERATOR_TOCONSTANT)) {
            int i = sAssignment.indexOf(ASSIGNMENT_OPERATOR_TOCONSTANT);
            if (assignmentType == Variable.VariableType.INPUT) {
                variableName = sAssignment.substring(0, i);
                constant = StringUtils.urlDecode(sAssignment.substring(i + ASSIGNMENT_OPERATOR_TOCONSTANT.length()));
            } else {
                variableName = sAssignment.substring(i + ASSIGNMENT_OPERATOR_TOCONSTANT.length());
                String value = StringUtils.urlDecode(sAssignment.substring(0, i));
                constant = value.equals("null") ? null : value;
            }
        }
        // Create the new assignment
        return new Assignment(assignmentData,
                              variableName,
                              assignmentType,
                              processVariableName,
                              constant);
    }
}
