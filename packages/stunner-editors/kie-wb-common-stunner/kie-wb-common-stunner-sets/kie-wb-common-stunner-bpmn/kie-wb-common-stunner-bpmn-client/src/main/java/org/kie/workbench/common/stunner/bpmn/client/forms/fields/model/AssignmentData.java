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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;

import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.isEmpty;
import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.nonEmpty;

/**
 * Class which contains everything associated with Assignments which
 * is passed between the Designer properties and the DataIOEditor, i.e.
 * Assignments, InputVariables, OutputVariables, DataTypes and ProcessVariables
 */
@Portable
public class AssignmentData {

    private List<Variable> inputVariables = new ArrayList<Variable>();

    private List<Variable> outputVariables = new ArrayList<Variable>();

    private List<Variable> processVariables = new ArrayList<Variable>();

    private List<Assignment> assignments = new ArrayList<Assignment>();

    private List<String> dataTypes = new ArrayList<String>();
    private List<String> dataTypeDisplayNames = new ArrayList<String>();
    private Map<String, String> mapDisplayNameToDataType = new HashMap<String, String>();
    private Map<String, String> mapDataTypeToDisplayName = new HashMap<String, String>();
    private Map<String, String> mapSimpleDataTypeToDisplayName = new HashMap<String, String>();

    private List<String> disallowedPropertyNames = new ArrayList<String>();

    private String variableCountsString = "";

    public AssignmentData() {
    }

    public AssignmentData(final String sInputVariables,
                          final String sOutputVariables,
                          final String sProcessVariables,
                          final String sAssignments,
                          final String sDataTypes,
                          final String sDisallowedPropertyNames) {
        // setDataTypes before variables because these determine whether variable datatypes are custom or not
        setDataTypes(sDataTypes);
        setProcessVariables(sProcessVariables);
        setInputVariables(sInputVariables);
        setOutputVariables(sOutputVariables);
        setAssignments(sAssignments);
        setDisallowedPropertyNames(sDisallowedPropertyNames);
    }

    public AssignmentData(final String sInputVariables,
                          final String sOutputVariables,
                          final String sProcessVariables,
                          final String sAssignments,
                          final String sDisallowedPropertyNames) {
        this(sInputVariables,
             sOutputVariables,
             sProcessVariables,
             sAssignments,
             null,
             sDisallowedPropertyNames);
    }

    /**
     * Creates AssignmentData based on a list of inputAssignmentRows and outputAssignmentRows.
     * @param inputAssignmentRows
     * @param outputAssignmentRows
     */
    public AssignmentData(final List<AssignmentRow> inputAssignmentRows,
                          final List<AssignmentRow> outputAssignmentRows,
                          final List<String> dataTypes,
                          final List<String> dataTypeDisplayNames) {
        setDataTypes(dataTypes,
                     dataTypeDisplayNames);
        if (inputAssignmentRows != null) {
            for (AssignmentRow row : inputAssignmentRows) {
                convertAssignmentRow(row);
            }
        }
        if (outputAssignmentRows != null) {
            for (AssignmentRow row : outputAssignmentRows) {
                convertAssignmentRow(row);
            }
        }
    }

    protected void convertAssignmentRow(final AssignmentRow assignmentRow) {
        if (assignmentRow.getName() == null || assignmentRow.getName().isEmpty()) {
            return;
        }
        if (findVariable(assignmentRow.getName(),
                         assignmentRow.getVariableType()) == null) {
            Variable var = new Variable(assignmentRow.getName(),
                                        assignmentRow.getVariableType(),
                                        getDataTypeFromDisplayName(assignmentRow.getDataType()),
                                        assignmentRow.getCustomDataType());
            addVariable(var);
        }
        String processVarName;
        // If there's a expression, use it rather than processVar
        String expression = assignmentRow.getExpression();
        if (nonEmpty(expression)) {
            processVarName = null;
        } else {
            processVarName = assignmentRow.getProcessVar();
            if (nonEmpty(processVarName)) {
                HashSet<String> processVarsNames = new HashSet<String>();
                for (Variable var : processVariables) {
                    processVarsNames.add(var.getName());
                }
                if (!processVarsNames.contains(processVarName)) {
                    Variable processVar = new Variable(processVarName,
                                                       Variable.VariableType.PROCESS,
                                                       assignmentRow.getDataType(),
                                                       assignmentRow.getCustomDataType());
                    processVariables.add(processVar);
                }
            }
        }
        if (isEmpty(expression) && isEmpty(processVarName)) {
            return;
        }
        Assignment assignment = new Assignment(this,
                                               assignmentRow.getName(),
                                               assignmentRow.getVariableType(),
                                               processVarName,
                                               expression);
        assignments.add(assignment);
    }

    public List<Variable> getInputVariables() {
        return inputVariables;
    }

    public String getInputVariablesString() {
        return getStringForList(inputVariables);
    }

    public void setInputVariables(String sInputVariables) {
        sInputVariables = StringUtils.preFilterVariablesTwoSemicolonForGenerics(sInputVariables);
        inputVariables.clear();
        if (sInputVariables != null && !sInputVariables.isEmpty()) {
            String[] inputs = sInputVariables.split(",");
            for (String input : inputs) {
                if (!input.isEmpty()) {
                    Variable var = Variable.deserialize(StringUtils.postFilterForGenerics(input),
                                                        Variable.VariableType.INPUT,
                                                        dataTypes);
                    if (var != null && var.getName() != null && !var.getName().isEmpty()) {
                        inputVariables.add(var);
                    }
                }
            }
        }
    }

    public List<Variable> getOutputVariables() {
        return outputVariables;
    }

    public String getOutputVariablesString() {
        return getStringForList(outputVariables);
    }

    public void setOutputVariables(String sOutputVariables) {
        sOutputVariables = StringUtils.preFilterVariablesTwoSemicolonForGenerics(sOutputVariables);
        outputVariables.clear();
        if (sOutputVariables != null && !sOutputVariables.isEmpty()) {
            String[] outputs = sOutputVariables.split(",");
            for (String output : outputs) {
                if (!output.isEmpty()) {
                    Variable var = Variable.deserialize(StringUtils.postFilterForGenerics(output),
                                                        Variable.VariableType.OUTPUT,
                                                        dataTypes);
                    if (var != null && var.getName() != null && !var.getName().isEmpty()) {
                        outputVariables.add(var);
                    }
                }
            }
        }
    }

    public List<Variable> getProcessVariables() {
        return processVariables;
    }

    public String getProcessVariablesString() {
        return getStringForList(processVariables);
    }

    public void setProcessVariables(String sProcessVariables) {
        processVariables.clear();
        sProcessVariables = StringUtils.preFilterVariablesForGenerics(sProcessVariables);
        if (sProcessVariables != null && !sProcessVariables.isEmpty()) {
            HashSet<String> procVarNames = new HashSet<String>();
            String[] processVars = sProcessVariables.split(",");
            for (String processVar : processVars) {
                if (!processVar.isEmpty()) {
                    Variable var = Variable.deserialize(processVar,
                                                        Variable.VariableType.PROCESS,
                                                        dataTypes);
                    if (var != null && var.getName() != null && !var.getName().isEmpty()) {
                        if (!procVarNames.contains(var.getName())) {
                            procVarNames.add(var.getName());
                            processVariables.add(var);
                        }
                    }
                }
            }
        }
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public String getAssignmentsString() {
        return getStringForList(assignments);
    }

    public void setAssignments(final String sAssignments) {
        assignments.clear();
        if (sAssignments != null && !sAssignments.isEmpty()) {
            String[] as = sAssignments.split(",(?=\\[din])|,(?=\\[dout])");
            for (String a : as) {
                if (!a.isEmpty()) {
                    Assignment ass = Assignment.deserialize(this,
                                                            a);
                    if (ass != null && ass.getName() != null && !ass.getName().isEmpty()) {
                        assignments.add(ass);
                    }
                }
            }
        }
    }

    public List<String> getDataTypes() {
        return dataTypes;
    }

    protected void setDataTypes(String dataTypes) {
        dataTypes = StringUtils.preFilterVariablesTwoSemicolonForGenerics(dataTypes);
        this.dataTypes.clear();
        this.dataTypeDisplayNames.clear();
        mapDisplayNameToDataType.clear();
        mapDataTypeToDisplayName.clear();
        mapSimpleDataTypeToDisplayName.clear();
        if (dataTypes != null && !dataTypes.isEmpty()) {
            String[] dts = dataTypes.split(",");
            for (String dt : dts) {
                dt = dt.trim();
                if (!dt.isEmpty() && !dt.startsWith("*")) {
                    String dtName = "";
                    String dtDisplayName = "";
                    String dtSimpleType = "";
                    if (dt.contains(":")) {
                        dtDisplayName = dt.substring(0,
                                                     dt.indexOf(':')).trim();
                        dtName = dt.substring(dt.indexOf(':') + 1).trim();
                    } else {
                        dtDisplayName = dt.trim();
                        dtName = dt.trim();
                    }
                    if (dtDisplayName.indexOf(' ') > 0) {
                        dtSimpleType = dtDisplayName.substring(0,
                                                               dtDisplayName.indexOf(' '));
                    } else {
                        dtSimpleType = dtDisplayName;
                    }

                    dtName = StringUtils.postFilterForGenerics(dtName);
                    dtDisplayName = StringUtils.postFilterForGenerics(dtDisplayName);
                    dtSimpleType = StringUtils.postFilterForGenerics(dtSimpleType);

                    if (!dtName.isEmpty()) {
                        this.dataTypeDisplayNames.add(dtDisplayName);
                        this.dataTypes.add(dtName);
                        mapDisplayNameToDataType.put(dtDisplayName,
                                                     dtName);
                        mapDataTypeToDisplayName.put(dtName,
                                                     dtDisplayName);
                    }
                    if (!dtSimpleType.isEmpty()) {
                        mapSimpleDataTypeToDisplayName.put(dtSimpleType,
                                                           dtDisplayName);
                    }
                }
            }
        }
    }

    protected void setDataTypes(final List<String> dataTypes,
                                final List<String> dataTypeDisplayNames) {
        this.dataTypes.clear();
        this.dataTypeDisplayNames.clear();
        mapDisplayNameToDataType.clear();
        mapDataTypeToDisplayName.clear();
        mapSimpleDataTypeToDisplayName.clear();
        this.dataTypes = dataTypes;
        this.dataTypeDisplayNames = dataTypeDisplayNames;
        for (int i = 0; i < dataTypeDisplayNames.size(); i++) {
            if (i < dataTypes.size()) {
                mapDisplayNameToDataType.put(dataTypeDisplayNames.get(i),
                                             dataTypes.get(i));
                mapDataTypeToDisplayName.put(dataTypes.get(i),
                                             dataTypeDisplayNames.get(i));
            } else {
                mapDisplayNameToDataType.put(dataTypeDisplayNames.get(i),
                                             dataTypeDisplayNames.get(i));
                mapDataTypeToDisplayName.put(dataTypeDisplayNames.get(i),
                                             dataTypeDisplayNames.get(i));
            }
        }
    }

    public List<String> getDisallowedPropertyNames() {
        return disallowedPropertyNames;
    }

    protected void setDisallowedPropertyNames(final String disallowedPropertyNames) {
        this.disallowedPropertyNames.clear();
        if (disallowedPropertyNames != null && !disallowedPropertyNames.isEmpty()) {
            String[] hps = disallowedPropertyNames.split(",");
            for (String hp : hps) {
                hp = hp.trim();
                if (!hp.isEmpty()) {
                    this.disallowedPropertyNames.add(hp);
                }
            }
        }
    }

    public Variable findProcessVariable(final String processVarName) {
        if (processVarName == null || processVarName.isEmpty()) {
            return null;
        }
        for (Variable var : processVariables) {
            if (processVarName.equals(var.getName())) {
                return var;
            }
        }
        return null;
    }

    public Variable findVariable(final String variableName,
                                 final Variable.VariableType variableType) {
        if (variableName == null || variableName.isEmpty()) {
            return null;
        }
        if (variableType == Variable.VariableType.INPUT) {
            for (Variable var : inputVariables) {
                if (variableName.equals(var.getName())) {
                    return var;
                }
            }
        } else if (variableType == Variable.VariableType.OUTPUT) {
            for (Variable var : outputVariables) {
                if (variableName.equals(var.getName())) {
                    return var;
                }
            }
        }
        return null;
    }

    public void addVariable(final Variable variable) {
        if (variable.getName() == null || variable.getName().isEmpty()) {
            return;
        }
        if (findVariable(variable.getName(),
                         variable.getVariableType()) != null) {
            return;
        }
        if (variable.getVariableType() == Variable.VariableType.INPUT) {
            inputVariables.add(variable);
        } else if (variable.getVariableType() == Variable.VariableType.OUTPUT) {
            outputVariables.add(variable);
        } else if (variable.getVariableType() == Variable.VariableType.PROCESS) {
            processVariables.add(variable);
        }
    }

    public List<String> getDataTypeDisplayNames() {
        return dataTypeDisplayNames;
    }

    public String getDataTypeFromDisplayName(final String dataTypeDisplayName) {
        if (mapDisplayNameToDataType.get(dataTypeDisplayName) != null) {
            return mapDisplayNameToDataType.get(dataTypeDisplayName);
        } else {
            return dataTypeDisplayName;
        }
    }

    public String getDisplayNameFromDataType(final String dataType) {
        if (mapDataTypeToDisplayName.get(dataType) != null) {
            return mapDataTypeToDisplayName.get(dataType);
        } else {
            return dataType;
        }
    }

    public String getDataTypeDisplayNameForUserString(final String userValue) {
        if (mapDataTypeToDisplayName.containsKey(userValue)) {
            return mapDataTypeToDisplayName.get(userValue);
        } else if (mapSimpleDataTypeToDisplayName.containsKey(userValue)) {
            return mapSimpleDataTypeToDisplayName.get(userValue);
        }
        return null;
    }

    public String getDataTypesString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < dataTypes.size(); i++) {
            String dataTypeDisplayName = dataTypes.get(i);
            String dataType = dataTypes.get(i);
            sb.append(dataTypeDisplayName).append(':').append(dataType).append(',');
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public String getDisallowedPropertyNamesString() {
        return getStringForList(disallowedPropertyNames);
    }

    public List<String> getProcessVariableNames() {
        List<String> processVarNames = new ArrayList<String>();
        for (Variable processVar : processVariables) {
            processVarNames.add(processVar.getName());
        }
        return processVarNames;
    }

    /**
     * Gets a list of AssignmentRows based on the current Assignments
     * @return
     */
    public List<AssignmentRow> getAssignmentRows(final Variable.VariableType varType) {
        List<AssignmentRow> rows = new ArrayList<>();
        List<Variable> handledVariables = new ArrayList<>();
        // Create an AssignmentRow for each Assignment
        for (Assignment assignment : assignments) {
            if (assignment.getVariableType() == varType) {
                String dataType = getDisplayNameFromDataType(assignment.getDataType());
                AssignmentRow row = new AssignmentRow(assignment.getName(),
                                                      assignment.getVariableType(),
                                                      dataType,
                                                      assignment.getCustomDataType(),
                                                      assignment.getProcessVarName(),
                                                      assignment.getExpression());
                rows.add(row);
                handledVariables.add(assignment.getVariable());
            }
        }
        List<Variable> vars = null;
        if (varType == Variable.VariableType.INPUT) {
            vars = inputVariables;
        } else {
            vars = outputVariables;
        }
        // Create an AssignmentRow for each Variable that doesn't have an Assignment
        for (Variable var : vars) {
            if (!handledVariables.contains(var)) {
                AssignmentRow row = new AssignmentRow(var.getName(),
                                                      var.getVariableType(),
                                                      var.getDataType(),
                                                      var.getCustomDataType(),
                                                      null,
                                                      null);
                rows.add(row);
            }
        }
        return rows;
    }

    public void setVariableCountsString(final String variableCountsString) {
        this.variableCountsString = variableCountsString;
    }

    public void setVariableCountsString(final boolean hasInputVars,
                                        final boolean isSingleInputVar,
                                        final boolean hasOutputVars,
                                        final boolean isSingleOutputVar) {
        StringBuilder sb = new StringBuilder();
        if (hasInputVars) {
            List<AssignmentRow> inputAssignments = getAssignmentRows(Variable.VariableType.INPUT);
            inputAssignments = removeDisallowedInputAssignmentRows(inputAssignments);
            if (inputAssignments == null || inputAssignments.isEmpty()) {
                if (isSingleInputVar) {
                    sb.append(StunnerFormsClientFieldsConstants.CONSTANTS.No_Data_Input());
                } else {
                    sb.append("0 " + StunnerFormsClientFieldsConstants.CONSTANTS.Data_Inputs());
                }
            } else if (inputAssignments.size() == 1) {
                sb.append("1 " + StunnerFormsClientFieldsConstants.CONSTANTS.Data_Input());
            } else if (inputAssignments.size() > 1) {
                sb.append(inputAssignments.size() + " " + StunnerFormsClientFieldsConstants.CONSTANTS.Data_Inputs());
            }
        }
        if (hasOutputVars) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            List<AssignmentRow> outputAssignments = getAssignmentRows(Variable.VariableType.OUTPUT);
            if (outputAssignments == null || outputAssignments.isEmpty()) {
                if (isSingleOutputVar) {
                    sb.append(StunnerFormsClientFieldsConstants.CONSTANTS.No_Data_Output());
                } else {
                    sb.append("0 " + StunnerFormsClientFieldsConstants.CONSTANTS.Data_Outputs());
                }
            } else if (outputAssignments.size() == 1) {
                sb.append("1 " + StunnerFormsClientFieldsConstants.CONSTANTS.Data_Output());
            } else if (outputAssignments.size() > 1) {
                sb.append(outputAssignments.size() + " " + StunnerFormsClientFieldsConstants.CONSTANTS.Data_Outputs());
            }
        }
        variableCountsString = sb.toString();
    }

    public String getVariableCountsString() {
        return variableCountsString;
    }

    public String getVariableCountsString(final boolean hasInputVars,
                                          final boolean isSingleInputVar,
                                          final boolean hasOutputVars,
                                          final boolean isSingleOutputVar) {
        setVariableCountsString(hasInputVars,
                                isSingleInputVar,
                                hasOutputVars,
                                isSingleOutputVar);
        return getVariableCountsString();
    }

    List<AssignmentRow> removeDisallowedInputAssignmentRows(List<AssignmentRow> inputAssignments) {
        if (inputAssignments == null) {
            return null;
        }
        List<AssignmentRow> allowedRows = new ArrayList<AssignmentRow>();
        for (AssignmentRow inputAssignment : inputAssignments) {
            String name = inputAssignment.getName();
            if (name != null && !name.isEmpty()) {
                if (!isDisallowedPropertyName(name)) {
                    allowedRows.add(inputAssignment);
                }
            }
        }
        return allowedRows;
    }

    private boolean isDisallowedPropertyName(final String name) {
        if (disallowedPropertyNames != null) {
            for (String disallowedPropertyName : disallowedPropertyNames) {
                if (disallowedPropertyName.equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"inputVariables\":\"").append(getInputVariablesString()).append("\"").append(",\n");
        sb.append("\"outputVariables\":\"").append(getOutputVariablesString()).append("\"").append(",\n");
        sb.append("\"processVariables\":\"").append(getProcessVariablesString()).append("\"").append(",\n");
        sb.append("\"assignments\":\"").append(getAssignmentsString()).append("\"").append(",\n");
        sb.append("\"dataTypes\":\"").append(getDataTypesString()).append("\"").append(",\n");
        sb.append("\"disallowedPropertyNames\":\"").append(getDisallowedPropertyNamesString()).append("\"");
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AssignmentData)) {
            return false;
        }
        AssignmentData that = (AssignmentData) o;
        if (getInputVariables() != null ? !getInputVariables().equals(that.getInputVariables()) : that.getInputVariables() != null) {
            return false;
        }
        if (getOutputVariables() != null ? !getOutputVariables().equals(that.getOutputVariables()) : that.getOutputVariables() != null) {
            return false;
        }
        if (getProcessVariables() != null ? !getProcessVariables().equals(that.getProcessVariables()) : that.getProcessVariables() != null) {
            return false;
        }
        if (getAssignments() != null ? !getAssignments().equals(that.getAssignments()) : that.getAssignments() != null) {
            return false;
        }
        if (getVariableCountsString() != null ? !getVariableCountsString().equals(that.getVariableCountsString()) : that.getVariableCountsString() != null) {
            return false;
        }
        if (getDataTypes() != null ? !getDataTypes().equals(that.getDataTypes()) : that.getDataTypes() != null) {
            return false;
        }
        if (getDataTypeDisplayNames() != null ? !getDataTypeDisplayNames().equals(that.getDataTypeDisplayNames()) : that.getDataTypeDisplayNames() != null) {
            return false;
        }
        return getDisallowedPropertyNames() != null ? getDisallowedPropertyNames().equals(that.getDisallowedPropertyNames()) : that.getDisallowedPropertyNames() == null;
    }

    @Override
    public int hashCode() {
        int result = getInputVariables() != null ? getInputVariables().hashCode() : 0;
        result = 31 * result + (getOutputVariables() != null ? getOutputVariables().hashCode() : 0);
        result = 31 * result + (getProcessVariables() != null ? getProcessVariables().hashCode() : 0);
        result = 31 * result + (getAssignments() != null ? getAssignments().hashCode() : 0);
        result = 31 * result + (getVariableCountsString() != null ? getVariableCountsString().hashCode() : 0);
        result = 31 * result + (getDataTypes() != null ? getDataTypes().hashCode() : 0);
        result = 31 * result + (getDataTypeDisplayNames() != null ? getDataTypeDisplayNames().hashCode() : 0);
        result = 31 * result + (getDisallowedPropertyNames() != null ? getDisallowedPropertyNames().hashCode() : 0);
        return result;
    }

    private String getStringForList(final List<?> objects) {
        StringBuilder sb = new StringBuilder();
        for (Object o : objects) {
            sb.append(o.toString()).append(',');
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }
}
