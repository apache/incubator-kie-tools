/*
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

package org.uberfire.client.toolbar;

/**
 * This interface is used by ActionToolbar to know which buttons should be 
 * displayed
 */
public interface ActionToolbarButtonsConfigurationProvider {

    public boolean showSaveButton();
    public boolean showSaveAndCloseButton();
    public boolean showCopyButton();
    public boolean showRenameButton();
    public boolean showPromoteToGlobalButton();
    public boolean showArchiveButton();
    public boolean showDeleteButton();
    public boolean showChangeStatusButton();
    public boolean showSelectWorkingSetsButton();
    public boolean showValidateButton();
    public boolean showVerifyButton();
    public boolean showViewSourceButton();
    public boolean showStateLabel();
    
}
