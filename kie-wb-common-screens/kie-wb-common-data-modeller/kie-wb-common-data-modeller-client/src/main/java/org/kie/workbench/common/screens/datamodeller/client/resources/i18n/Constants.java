/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

    Constants INSTANCE = GWT.create(Constants.class);

    String modelEditor_newModel();

    String modelEditor_menu_main();

    String modelEditor_menu_file();

    String modelEditor_screen_name();

    String modelEditor_menu_save();

    String modelEditor_menu_new_dataObject();

    String modelEditor_menu_generate();

    String modelEditor_saving();

    String modelEditor_saving_error();

    String modelEditor_loading();

    String modelEditor_loading_error();

    String modelEditor_deleting_error();

    String modelEditor_propertyType_loading_error();

    String modelEditor_annotationDef_loading_error();

    String modelEditor_projectPath_calc_error();

    String modelEditor_notification_dataObject_created( String name );

    String modelEditor_notification_dataObject_deleted( String name );

    String modelEditor_notification_dataModel_generated();

    String modelEditor_notification_dataModel_loaded( String name );

    String modelEditor_notification_dataModel_saved( String seconds );

    String modelEditor_notification_dataObject_referenced_has_been_deleted( String classA, String classB );

    String modelEditor_notify_readonly_objects_read();

    String modelEditor_notify_externally_modified_objects_read();

    String modelEditor_discard_changes_message();

    String modelEditor_confirm_delete();

    String modelEditor_action_yes_force_save();

    String modelEditor_action_no_discard_changes();

    String modelEditor_action_yes_refactor_file_name();

    String modelEditor_action_yes_refactor_directory();

    String modelEditor_action_no_dont_refactor_file_name();

    String modelEditor_action_no_dont_refactor_directory();

    String modelEditor_confirm_save_model_before_project_change( String from, String to );

    String modelEditor_confirm_save_model_before_project_change_force( String user, String to );

    String modelEditor_confirm_save_before_rename();

    String modelEditor_notify_project_change( String from, String to );

    String modelEditor_general_properties_label();

    String modelEditor_message_file_parsing_errors();

    String modelEditor_confirm_deletion_of_used_class( String className );

    String modelEditor_confirm_renaming_of_used_class( String className );

    String modelEditor_confirm_package_change_of_used_class( String className );

    String modelEditor_confirm_deletion_of_used_field( String fieldName );

    String modelEditor_confirm_renaming_of_used_field( String fieldName );

    String modelEditor_confirm_file_name_refactoring( String newFileName );

    String modelEditor_confirm_file_package_refactoring( String newPackageName );

    String modelEditor_renaming_error();

    String modelEditor_copying_error();

    String modelBrowser_modelUnknown();

    String modelBrowser_emptyTable();

    String modelBrowser_create();

    String modelBrowser_createDataObject();

    String modelBrowser_action_deleteDataObject();

    String objectBrowser_columnPosition();

    String modelBrowser_columnName();

    String objectBrowser_createProperty();

    String objectBrowser_create();

    String objectBrowser_emptyTable();

    String objectBrowser_columnName();

    String objectBrowser_columnLabel();

    String objectBrowser_columnType();

    String objectBrowser_action_deleteProperty();

    String objectBrowser_action_delete();

    String objectBrowser_newFieldTitle();

    String objectBrowser_idLabel();

    String objectBrowser_labelLabel();

    String objectBrowser_typeLabel();

    String objectBrowser_typeLabelMultiple();

    String objectBrowser_typeLabelMultipleTooltip();

    String objectBrowser_labelPlaceholder();

    String objectBrowser_action_createFieldAndContinue();

    String objectBrowser_action_createField();

    String objectBrowser_action_cancel();

    String objectBrowser_readonlyLabel();

    String objectBrowser_externallyModifiedLabel();

    String objectBrowser_action_goToDataObjectDefinition();

    String objectBrowser_action_add_field();

    String objectBrowser_message_file_not_exists_or_renamed( String file );

    String objectEditor_nameLabel();

    String objectEditor_labelLabel();

    String objectEditor_descriptionLabel();

    String objectEditor_packageLabel();

    String objectEditor_superclassLabel();

    String objectEditor_roleLabel();

    String objectEditor_roleHelp();

    String objectEditor_typeSafeLabel();

    String objectEditor_typeSafeHelp();

    String objectEditor_timestampLabel();

    String objectEditor_timestampHelp();

    String objectEditor_durationLabel();

    String objectEditor_durationHelp();

    String objectEditor_expiresLabel();

    String objectEditor_expiresHelp();

    String objectEditor_remotableLabel();

    String objectEditor_remotableHelp();

    String objectEditor_classReactiveLabel();

    String objectEditor_classReactiveLabelHelp();

    String objectEditor_propertyReactiveLabel();

    String objectEditor_propertyReactiveLabelHelp();

    String objectFieldEditor_nameLabel();

    String objectFieldEditor_labelLabel();

    String objectFieldEditor_descriptionLabel();

    String objectFieldEditor_typeLabel();

    String objectFieldEditor_equalsLabel();

    String objectFieldEditor_equalsHelp();

    String objectFieldEditor_positionLabel();

    String objectFieldEditor_positionHelp();

    String objectEditor_droolsParametersLabel();

    String new_dataobject_popup_title();

    String new_dataobject_popup_identifier();

    String new_dataobject_popup_label();

    String java_id_placeholder();

    String package_id_placeholder();

    String validPackageHelp( String line_break );

    String new_dataobject_popup_new_package();

    String new_dataobject_popup_existing_package();

    String new_dataobject_popup_superclass();

    String packageSelector_popup_title();

    String packageSelector_popup_add();

    String modeler_callback_error( String localMessage, String excMessage );

    String validation_error_missing_object_attribute_type();

    String validation_error_invalid_object_identifier( String name );

    String validation_error_invalid_object_attribute_identifier( String attributeName );

    String validation_error_invalid_package_identifier( String packageName );

    String validation_error_invalid_package_identifier_null();

    String validation_error_no_package_selected();

    String validation_error_invalid_position();

    String validation_error_object_already_exists( String name, String packageName );

    String validation_error_object_attribute_already_exists( String attributeName );

    String validation_error_object_un_managed_attribute_already_exists( String attributeName, String attributeType );

    String validation_error_cannot_delete_object( String objectName, String references );

    String validation_error_cannot_change_object_name();

    String validation_error_cannot_change_object_package();

    String validation_error_cyclic_extension( String sibling, String parent );

    String validation_error_invalid_timer_expression( String expression );

    String validation_error_position_greater_or_equal_than( String position, String minValue );

    String validation_error_position_lower_than( String position, String maxValue );

    String validation_error_position_already_used_by_fields( String position, String fieldNames );

    String validation_error_position_greater_or_equal_than_and_lower_than( String position, String minValue, String maxValue );

    String objectEditor_dataObjectTitle();

    String objectFieldEditor_fieldTitle();

    String usages_popup_title();

    String usages_popup_action_yes_delete_anyway();

    String usages_popup_action_yes_rename_anyway();

    String usages_popup_action_yes_change_anyway();

    String usages_popup_file_name_column();

    String usages_popup_file_path_column();

    //advanced domain editor i18n

    String advanced_domain_screen_name();

    String advanced_domain_wizard_title();

    String advanced_domain_wizard_search_page_title();

    String advanced_domain_wizard_search_page_search_field();

    String advanced_domain_wizard_search_page_search_field_place_holder();

    String advanced_domain_wizard_search_page_message_annotation_not_loaded();

    String advanced_domain_wizard_search_page_message_annotation_is_loaded();

    String advanced_domain_wizard_search_page_message_class_not_found( String className );

    String advanced_domain_wizard_search_page_search_button_tooltip();

    String advanced_domain_wizard_value_pair_editor_page_message_enter_required_value_and_validate();

    String advanced_domain_wizard_value_pair_editor_page_message_enter_optional_value_and_validate();

    String advanced_domain_wizard_value_pair_editor_page_message_value_validated();

    String advanced_domain_wizard_value_pair_editor_page_message_value_not_validated();

    String advanced_domain_annotation_list_editor_action_add();

    String advanced_domain_annotation_list_editor_action_delete();

    String advanced_domain_annotation_list_editor_action_edit();

    String advanced_domain_annotation_list_editor_action_clear();

    String advanced_domain_annotation_list_editor_message_confirm_annotation_deletion( String annotationClassName, String elementType );

    String advanced_domain_annotation_list_editor_message_value_pair_has_no_default_value( String valuePair, String annotationClassName );

    String advanced_domain_annotation_list_editor_message_invalid_value_for_value_pair( String valuePair );

    String advanced_domain_annotation_list_editor_message_value_pair_cant_be_null( String valuePair );

    String advanced_domain_annotation_list_editor_message_value_not_set();

    String advanced_domain_annotation_list_editor_message_source_code_not_available();

    String advanced_domain_generic_value_pair_editor_action();

    String advanced_domain_multiple_value_pair_editor_message_null_or_invalid();

    String advanced_domain_multiple_value_pair_editor_action_delete();

    String advanced_domain_multiple_value_pair_editor_action_add();

    String advanced_domain_value_pair_editor_popup_title();

    //persistence domain editor

    String persistence_domain_objectEditor_entity_category();

    String persistence_domain_objectEditor_entity_field_label();

    String persistence_domain_objectEditor_entity_field_help_heading();

    String persistence_domain_objectEditor_entity_field_help();

    String persistence_domain_objectEditor_table_field_label();

    String persistence_domain_objectEditor_table_field_help_heading();

    String persistence_domain_objectEditor_table_field_help();

    String persistence_domain_objectEditor_add_identifier_action();

    String persistence_domain_objectEditor_dont_add_identifier_action();

    String persistence_domain_objectEditor_on_make_persistable_message();

    String persistence_domain_objectEditor_required_identifier_is_missing_message();

    String persistence_domain_objectEditor_review_relationship_fields_message();

    String persistence_domain_objectEditor_review_relationship_field_for_review_message( String fieldName );

    String persistence_domain_fieldEditor_identifier_category();

    String persistence_domain_fieldEditor_identifier_field_label();

    String persistence_domain_fieldEditor_identifier_field_help_heading();

    String persistence_domain_fieldEditor_identifier_field_help();

    String persistence_domain_fieldEditor_generation_strategy_field_label();

    String persistence_domain_fieldEditor_generation_strategy_field_help_heading();

    String persistence_domain_fieldEditor_generation_strategy_field_help();

    String persistence_domain_fieldEditor_sequence_generator_field_label();

    String persistence_domain_fieldEditor_sequence_generator_field_help_heading();

    String persistence_domain_fieldEditor_sequence_generator_field_help();

    String persistence_domain_fieldEditor_column_category();

    String persistence_domain_fieldEditor_column_field_label();

    String persistence_domain_fieldEditor_column_field_help_heading();

    String persistence_domain_fieldEditor_column_field_help();

    String persistence_domain_fieldEditor_unique_field_label();

    String persistence_domain_fieldEditor_unique_field_help_heading();

    String persistence_domain_fieldEditor_unique_field_help();

    String persistence_domain_fieldEditor_nullable_field_label();

    String persistence_domain_fieldEditor_nullable_field_help_heading();

    String persistence_domain_fieldEditor_nullable_field_help();

    String persistence_domain_fieldEditor_insertable_field_label();

    String persistence_domain_fieldEditor_insertable_field_help_heading();

    String persistence_domain_fieldEditor_insertable_field_help();

    String persistence_domain_fieldEditor_updatable_field_label();

    String persistence_domain_fieldEditor_updatable_field_help_heading();

    String persistence_domain_fieldEditor_updatable_field_help();

    String persistence_domain_fieldEditor_relationship_category();

    String persistence_domain_fieldEditor_relationship_field_label();

    String persistence_domain_fieldEditor_relationship_field_help_heading();

    String persistence_domain_fieldEditor_relationship_field_help();

    String persistence_domain_fieldEditor_relationship_field_relation_not_set_message();

    //persistence descriptor editor i18n

    String persistence_descriptor_resource_type_description();

    String persistence_descriptor_editor_loading_classes_message();

    String persistence_descriptor_editor_loading_source_message();

    String persistence_descriptor_editor_resource_local_transactions_not_supported_message();

    String persistence_descriptor_editor_peristable_class_not_found( String className );

    String persistence_descriptor_editor_persistence_unit_field();

    String persistence_descriptor_editor_persistence_provider_field();

    String persistence_descriptor_editor_data_source_field();

    String persistence_descriptor_editor_transactions_type_field();

    String persistence_descriptor_editor_advanced_properties_table();

    String persistence_descriptor_editor_persistable_objects_table();

    String persistence_unit_property_grid_new_property_panel();

    String persistence_unit_property_grid_new_property_name_placeholder();

    String persistence_unit_property_grid_new_property_value_placeholder();

    String persistence_unit_property_grid_action_add();

    String persistence_unit_property_grid_action_delete();

    String persistence_unit_property_grid_no_properties_message();

    String persistence_unit_property_grid_property_name_column();

    String persistence_unit_property_grid_property_value_column();

    String persistence_unit_property_grid_property_action_column();

    String project_class_list_action_add_class();

    String project_class_list_action_add_project_persistable_classes();

    String project_class_list_action_delete();

    String project_class_list_no_classes_message();

    String project_class_list_enter_persistable_class_name_message();

    String project_class_list_class_name_empty_message();

    String project_class_list_class_name_column();

    String project_class_list_action_column();

    String character_value_pair_editor_invalid_character_message();

}
