/*
* Copyright 2015 JBoss Inc
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
package org.uberfire.ext.layout.editor.client.components;

/**
 * Interface to allow Drag & Drop Elements to have Default Setting that are going to be shared on the Drag & Drop
 * events
 */
public interface HasDragAndDropSettings {
    String[] getSettingsKeys();
    String getSettingValue( String key );
    void setSettingValue( String key, String value );
}
