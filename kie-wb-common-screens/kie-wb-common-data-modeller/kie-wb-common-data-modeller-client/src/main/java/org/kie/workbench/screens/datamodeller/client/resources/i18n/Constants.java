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

package org.kie.workbench.screens.datamodeller.client.resources.i18n;

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

    public String modelEditor_propertyType_loading_error();

    public String modelEditor_annotationDef_loading_error();

    public String modelEditor_projectPath_calc_error();

    public String modelEditor_notification_dataObject_created(String name);

    public String modelEditor_notification_dataObject_deleted(String name);

    public String modelEditor_notification_dataModel_generated();

    public String modelEditor_notification_dataModel_loaded(String name);

    public String modelEditor_notification_dataModel_saved();

    public String modelEditor_discard_changes_message();

    public String modelBrowser_modelUnknown();

    public String modelBrowser_emptyTable();

    public String modelBrowser_create();
    
    public String modelBrowser_createDataObject();

    public String modelBrowser_action_deleteDataObject();

    public String modelBrowser_columnName();

    public String objectEditor_objectUnknown();
    
    public String objectEditor_createProperty();

    public String objectEditor_create();

    public String objectEditor_emptyTable();

    public String objectEditor_columnName();

    public String objectEditor_columnType();

    public String objectEditor_basicType();
    
    public String objectEditor_dataObjectType();
    
    public String objectEditor_multiple();

    public String objectEditor_action_deleteProperty();

    public String new_dataobject_popup_title();
    
    public String new_dataobject_popup_name();

    public String new_dataobject_placeholder();
    
    public String new_dataobject_popup_new_package();
    
    public String new_dataobject_popup_existing_package();

    public String new_dataobject_popup_superclass();

    public String packageSelector_popup_title();

    public String packageSelector_popup_add();

    public String modeler_callback_error(String localMessage, String excMessage);

    public String validation_error_invalid_object_identifier(String name);

    public String validation_error_invalid_object_attribute_identifier(String attributeName);

    public String validation_error_invalid_package_identifier(String packageName);

    public String validation_error_invalid_position();

    public String validation_error_object_already_exists(String name, String packageName);

    public String validation_error_object_attribute_already_exists(String attributeName);

    public String validation_error_cannot_delete_object(String objectName);

    public String validation_error_cannot_change_object_name();

    public String validation_error_cannot_change_object_package();

}
