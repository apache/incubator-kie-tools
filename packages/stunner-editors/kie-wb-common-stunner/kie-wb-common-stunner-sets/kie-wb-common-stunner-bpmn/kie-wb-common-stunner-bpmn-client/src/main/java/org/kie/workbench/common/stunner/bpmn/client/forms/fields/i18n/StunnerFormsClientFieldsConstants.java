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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface StunnerFormsClientFieldsConstants extends Messages {

    StunnerFormsClientFieldsConstants CONSTANTS = GWT.create(StunnerFormsClientFieldsConstants.class);

    String Add();

    String businessProcess();

    String businessProcessResourceTypeDescription();

    String Cancel();

    String Expression();

    String Custom();

    String Data_Input();

    String Data_Inputs();

    String Data_Input_and_Assignment();

    String Data_Inputs_and_Assignments();

    String Data_Output_and_Assignment();

    String Data_Output();

    String Data_Outputs();

    String Data_Outputs_and_Assignments();

    String Data_IO();

    String Data_Type();

    String Edit();

    String Enter_expression();

    String Enter_type();

    String Error_retrieving_datatypes();

    String Invalid_character_in_name();

    String Invalid_character_in_value();

    String Name();

    String No_Data_Input();

    String No_Data_Output();

    String Only_single_entry_allowed();

    String Ok();

    String Source();

    String assignment_source_help();

    String Target();

    String assignment_target_help();

    String This_input_should_be_entered_as_a_property_for_the_task();

    String Removed_invalid_characters_from_name();

    String Unbalanced_GT_LT_from_name();

    String Removed_invalid_characters_from_value();

    String A_Data_Input_with_this_name_already_exists();

    String ProcessModel();

    String ConfirmCloseBusinessProcessEditor();

    String DeleteDiagramVariableError();

    String RenameDiagramVariableError();

    String DuplicatedVariableIDError(String processIDName);

    String DuplicatedVariableNameError(String variableName);

    String DuplicatedAttributeNameError(String attributeName);

    String AssignmentNameAlreadyInUseAsMultipleInstanceInputOutputVariable(String assignmentName);

    String Imports();

    String No_Imports();

    String No_Data_Type_Import();

    String Data_Type_Import();

    String Data_Type_Imports();

    String Data_Type_Imports_Title();

    String No_WSDL_Import();

    String WSDL_Import();

    String WSDL_Imports();

    String WSDL_Imports_Title();

    String Duplicate_Import(String location, String namespace);

    String Location();

    String Namespace();

    String ClassName();

    String Language();

    String No_Correlations();

    String Correlation();

    String Correlations();

    String Correlations_Title();

    String Duplicate_Correlation(String id);

    String Id();

    String PropertyId();

    String PropertyName();

    String PropertyType();

    // Notifications
    String replyToOptional();

    String replyToMessage();

    String message();

    String toUsers();

    String toGroups();

    String toEmails();

    String subjectPlaceholder();

    String emailsPlaceholder();

    String bodyPlaceholder();

    String taskStateType();

    String notCompleted();

    String notStarted();

    String taskExpirationDefinition();

    String notificationPopover();

    String notifyLabel();

    String notifyAfter();

    String notificationRepeat();

    String notifyEvery();

    String until();

    String expressionTextArea();

    String taskStateChangesLabel();

    String repeatCountReachesLabel();

    String syntaxRequirement();

    String incorrectEmail();
}
