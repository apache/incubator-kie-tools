/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface Constants extends Messages {

    public static final Constants INSTANCE = GWT.create(Constants.class);

    public String modelEditor_newModel();

    public String modelEditor_menu_main();

    public String modelEditor_menu_file();

    public String modelEditor_screen_name();

    public String modelEditor_menu_save();

    public String modelEditor_menu_new_dataObject();

    public String modelEditor_menu_generate();

    public String modelEditor_saving();

    public String modelEditor_saving_error();

    public String modelEditor_loading();

    public String modelEditor_loading_error();

    public String modelEditor_deleting_error();

    public String modelEditor_propertyType_loading_error();

    public String modelEditor_annotationDef_loading_error();

    public String modelEditor_projectPath_calc_error();

    public String modelEditor_notification_dataObject_created(String name);

    public String modelEditor_notification_dataObject_deleted(String name);

    public String modelEditor_notification_dataModel_generated();

    public String modelEditor_notification_dataModel_loaded(String name);

    public String modelEditor_notification_dataModel_saved(String seconds);

    public String modelEditor_notification_dataObject_referenced_has_been_deleted(String classA, String classB);

    public String modelEditor_notify_readonly_objects_read();

    public String modelEditor_notify_externally_modified_objects_read();

    public String modelEditor_discard_changes_message();

    public String modelEditor_confirm_delete();

    public String modelEditor_action_yes_force_save();

    public String modelEditor_action_no_discard_changes();

    public String modelEditor_action_yes_refactor_file_name();

    public String modelEditor_action_yes_refactor_directory();

    public String modelEditor_action_no_dont_refactor_file_name();

    public String modelEditor_action_no_dont_refactor_directory();

    public String modelEditor_confirm_save_model_before_project_change(String from, String to);

    public String modelEditor_confirm_save_model_before_project_change_force(String user, String to);

    public String modelEditor_confirm_save_before_rename();

    public String modelEditor_notify_project_change(String from, String to);

    public String modelEditor_message_file_parsing_errors();

    public String modelEditor_confirm_deletion_of_used_class(String className);

    public String modelEditor_confirm_renaming_of_used_class(String className);

    public String modelEditor_confirm_package_change_of_used_class(String className);

    public String modelEditor_confirm_deletion_of_used_field(String fieldName);

    public String modelEditor_confirm_renaming_of_used_field(String fieldName);

    public String modelEditor_confirm_file_name_refactoring(String newFileName);

    public String modelEditor_confirm_file_package_refactoring(String newPackageName);

    public String modelEditor_renaming_error();

    public String modelEditor_copying_error();

    public String modelBrowser_modelUnknown();

    public String modelBrowser_emptyTable();

    public String modelBrowser_create();

    public String modelBrowser_createDataObject();

    public String modelBrowser_action_deleteDataObject();

    public String objectBrowser_columnPosition();

    public String modelBrowser_columnName();

    public String objectBrowser_createProperty();

    public String objectBrowser_create();

    public String objectBrowser_emptyTable();

    public String objectBrowser_columnName();

    public String objectBrowser_columnLabel();

    public String objectBrowser_columnType();

    public String objectBrowser_action_deleteProperty();

    public String objectBrowser_idLabel();

    public String objectBrowser_labelLabel();

    public String objectBrowser_typeLabel();

    public String objectBrowser_labelPlaceholder();

    public String objectBrowser_readonlyLabel();

    public String objectBrowser_externallyModifiedLabel();

    public String objectBrowser_action_goToDataObjectDefinition();

    public String objectBrowser_message_file_not_exists_or_renamed(String file);

    public String objectEditor_nameLabel();

    public String objectEditor_labelLabel();

    public String objectEditor_descriptionLabel();

    public String objectEditor_packageLabel();

    public String objectEditor_superclassLabel();

    public String objectEditor_roleLabel();

    public String objectEditor_roleHelp();

    public String objectEditor_typeSafeLabel();

    public String objectEditor_typeSafeHelp();

    public String objectEditor_timestampLabel();

    public String objectEditor_timestampHelp();

    public String objectEditor_durationLabel();

    public String objectEditor_durationHelp();

    public String objectEditor_expiresLabel();

    public String objectEditor_expiresHelp();

    public String objectEditor_remotableLabel();

    public String objectEditor_remotableHelp();

    public String objectEditor_classReactiveLabel();

    public String objectEditor_classReactiveLabelHelp();

    public String objectEditor_propertyReactiveLabel();

    public String objectEditor_propertyReactiveLabelHelp();

    public String objectFieldEditor_nameLabel();

    public String objectFieldEditor_labelLabel();

    public String objectFieldEditor_descriptionLabel();

    public String objectFieldEditor_typeLabel();

    public String objectFieldEditor_equalsLabel();

    public String objectFieldEditor_equalsHelp();

    public String objectFieldEditor_positionLabel();

    public String objectFieldEditor_positionHelp();

    public String objectEditor_droolsParametersLabel();

    public String new_dataobject_popup_title();

    public String new_dataobject_popup_identifier();

    public String new_dataobject_popup_label();

    public String java_id_placeholder();

    public String package_id_placeholder();

    public String validPackageHelp(String line_break);

    public String new_dataobject_popup_new_package();

    public String new_dataobject_popup_existing_package();

    public String new_dataobject_popup_superclass();

    public String packageSelector_popup_title();

    public String packageSelector_popup_add();

    public String modeler_callback_error(String localMessage, String excMessage);

    public String validation_error_missing_object_attribute_type();

    public String validation_error_invalid_object_identifier(String name);

    public String validation_error_invalid_object_attribute_identifier(String attributeName);

    public String validation_error_invalid_package_identifier(String packageName);

    public String validation_error_invalid_package_identifier_null();

    public String validation_error_no_package_selected();

    public String validation_error_invalid_position();

    public String validation_error_object_already_exists(String name, String packageName);

    public String validation_error_object_attribute_already_exists(String attributeName);

    public String validation_error_cannot_delete_object(String objectName, String references);

    public String validation_error_cannot_change_object_name();

    public String validation_error_cannot_change_object_package();

    public String validation_error_cyclic_extension(String sibling, String parent);

    public String validation_error_invalid_timer_expression(String expression);

    public String objectEditor_dataObjectTitle();

    public String objectFieldEditor_fieldTitle();

    public String usages_popup_title();

    public String usages_popup_action_yes_delete_anyway();

    public String usages_popup_action_yes_rename_anyway();

    public String usages_popup_file_name_column();

    public String usages_popup_file_path_column();

}
