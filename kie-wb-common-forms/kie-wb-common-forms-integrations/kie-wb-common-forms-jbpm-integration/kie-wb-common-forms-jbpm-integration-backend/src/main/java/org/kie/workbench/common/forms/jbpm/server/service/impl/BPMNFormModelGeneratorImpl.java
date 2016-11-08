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

package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.Dependent;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.UserTask;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinesProcessVariable;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskVariable;
import org.kie.workbench.common.forms.jbpm.server.service.BPMNFormModelGenerator;

@Dependent
public class BPMNFormModelGeneratorImpl implements BPMNFormModelGenerator {


    @Override
    public BusinessProcessFormModel generateProcessFormModel( Definitions source ) {

        Process process = getProcess( source );

        if ( process != null ) {

            List<BusinesProcessVariable> variables = new ArrayList<>();

            process.getProperties().forEach( prop -> {
                String varName = prop.getId();
                String varType = getDefinitionType( prop.getItemSubjectRef() );

                variables.add( new BusinesProcessVariable( varName, varType ) );

            } );

            return new BusinessProcessFormModel( process.getId(), process.getName(), variables );
        }

        return null;
    }

    @Override
    public List<TaskFormModel> generateTaskFormModels( Definitions source ) {

        Process process = getProcess( source );

        List<TaskFormModel> models = new ArrayList<>();

        if ( process != null ) {

            Map<String, String> variableDefinitions = new HashMap<>();

            process.getProperties().forEach( prop -> {
                String varName = prop.getId();
                String varType = getDefinitionType( prop.getItemSubjectRef() );
                variableDefinitions.put( varName, varType );
            } );


            generateTaskFormModels( process, models, variableDefinitions );
        }
        return models;
    }

    public void generateTaskFormModels( FlowElementsContainer container,
                                        List<TaskFormModel> models,
                                        Map<String, String> variableDefinitions ) {
        for ( FlowElement fe : container.getFlowElements() ) {
            if ( fe instanceof UserTask ) {
                models.add( getTaskFormModel( (UserTask) fe, container, variableDefinitions ) );
            } else if ( fe instanceof FlowElementsContainer ) {
                generateTaskFormModels( (FlowElementsContainer) fe, models, variableDefinitions );
            }
        }
    }

    @Override
    public TaskFormModel generateTaskFormModel( Definitions source, String taskId ) {
        Process process = getProcess( source );

        if ( process != null ) {
            Map<String, String> variableDefinitions = new HashMap<>();

            process.getProperties().forEach( prop -> {
                String varName = prop.getId();
                String varType = getDefinitionType( prop.getItemSubjectRef() );
                variableDefinitions.put( varName, varType );
            } );

            return generateTaskFormModel( taskId, process, variableDefinitions );
        }
        return null;
    }

    @Override
    public TaskFormModel generateTaskFormModel( String taskId,
                                                FlowElementsContainer container,
                                                Map<String, String> variableDefinitions ) {
        for ( FlowElement fe : container.getFlowElements() ) {
            if ( fe instanceof UserTask && fe.getId().equals( taskId ) ) {
                return getTaskFormModel( (UserTask) fe, container, variableDefinitions );
            } else if ( fe instanceof FlowElementsContainer ) {
                TaskFormModel model = generateTaskFormModel( taskId, (FlowElementsContainer) fe, variableDefinitions );
                if ( model != null ) {
                    return model;
                }
            }
        }
        return null;
    }

    protected TaskFormModel getTaskFormModel( UserTask userTask,
                                              FlowElementsContainer container,
                                              Map<String, String> variableDefinitions ) {
        Map<String, TaskVariableSetting> taskVariableSettings = new HashMap<>();

        List<DataInputAssociation> dataInputAssociations = userTask.getDataInputAssociations();

        if ( dataInputAssociations != null ) {
            for ( DataInputAssociation inputAssociation : dataInputAssociations ) {

                if ( inputAssociation.getSourceRef() != null && inputAssociation.getSourceRef().size() > 0 && inputAssociation.getTargetRef() != null ) {

                    String taskVariable = inputAssociation.getSourceRef().get( 0 ).getId();

                    TaskVariableSetting taskVariableSetting = taskVariableSettings.get( taskVariable );

                    if ( taskVariableSetting != null ) {
                        continue;
                    }

                    String type = getDefinitionType( inputAssociation.getSourceRef().get( 0 ).getItemSubjectRef() );

                    String variableType = variableDefinitions.get( taskVariable );

                    if ( !StringUtils.isEmpty( variableType ) && ( variableType.equals( type ) || type == null ) ) {

                        taskVariableSetting = new TaskVariableSetting( taskVariable, variableType );

                        taskVariableSettings.put( taskVariable, taskVariableSetting );

                        DataInput input = (DataInput) inputAssociation.getTargetRef();

                        if ( input != null ) taskVariableSetting.setInput( input.getName() );
                    }
                }
            }
        }

        List<DataOutputAssociation> dataOutputAssociations = userTask.getDataOutputAssociations();

        if ( dataOutputAssociations != null ) {
            for ( DataOutputAssociation outputAssociation : dataOutputAssociations ) {

                if ( outputAssociation.getSourceRef() != null && outputAssociation.getSourceRef().size() > 0 && outputAssociation.getTargetRef() != null ) {

                    String taskVariable = outputAssociation.getTargetRef().getId();

                    TaskVariableSetting taskVariableSetting = taskVariableSettings.get( taskVariable );

                    String type = getDefinitionType( outputAssociation.getSourceRef().get( 0 ).getItemSubjectRef() );

                    DataOutput output = (DataOutput) outputAssociation.getSourceRef().get( 0 );

                    if ( taskVariableSetting != null && !taskVariableSetting.getType().equals( type ) ) {
                        continue;
                    }

                    if ( taskVariableSetting == null ) {

                        String variableType = variableDefinitions.get( taskVariable );

                        if ( !StringUtils.isEmpty( variableType ) && ( variableType.equals( type ) || type == null ) ) {

                            taskVariableSetting = new TaskVariableSetting( taskVariable, variableType );

                            taskVariableSettings.put( taskVariable, taskVariableSetting );

                        }
                    }

                    if ( taskVariableSetting != null && output != null ) {
                        taskVariableSetting.setOutput( output.getName() );
                    }
                }
            }
        }

        List<TaskVariable> taskVariables = new ArrayList<>();
        taskVariableSettings.values().forEach( setting -> {
            taskVariables.add( new TaskVariable( setting.getVariable(),
                                                 setting.getType(),
                                                 setting.getInput(),
                                                 setting.getOutput() ) );
        } );

        return new TaskFormModel( container.getId(),
                                  userTask.getId(),
                                  userTask.getName(),
                                  taskVariables );
    }

    protected Process getProcess( Definitions source ) {
        for ( RootElement re : source.getRootElements() ) {
            if ( re instanceof Process ) {
                return (Process) re;
            }
        }
        return null;
    }

    private String getDefinitionType( ItemDefinition definition ) {

        if ( definition == null ) {
            return null;
        }

        String type = StringUtils.defaultIfEmpty( definition.getStructureRef(), "java.lang.Object" );

        if ( !type.contains( "." ) ) {
            if ( "String".equals( type ) ) return String.class.getName();
            if ( "Integer".equals( type ) ) return Integer.class.getName();
            if ( "Short".equals( type ) ) return Short.class.getName();
            if ( "Long".equals( type ) ) return Long.class.getName();
            if ( "Float".equals( type ) ) return Float.class.getName();
            if ( "Double".equals( type ) ) return Double.class.getName();
            if ( "Boolean".equals( type ) ) return Boolean.class.getName();
            if ( "Date".equals( type ) ) return Date.class.getName();
            if ( "BigDecimal".equals( type ) ) return java.math.BigDecimal.class.getName();
            if ( "BigInteger".equals( type ) ) return java.math.BigInteger.class.getName();
        }

        return type;
    }

    private class TaskVariableSetting {
        private String variable;
        private String input;
        private String output;
        private String type;

        public TaskVariableSetting( String variable, String type ) {
            this.variable = variable;
            this.type = type;
        }

        public String getVariable() {
            return variable;
        }

        public String getInput() {
            return input;
        }

        public void setInput( String input ) {
            this.input = input;
        }

        public String getOutput() {
            return output;
        }

        public void setOutput( String output ) {
            this.output = output;
        }

        public String getType() {
            return type;
        }
    }

}
