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


public class DefaultActionToolbarButtonsConfigurationProvider
        implements
        ActionToolbarButtonsConfigurationProvider {


    public DefaultActionToolbarButtonsConfigurationProvider() {
    }

    public boolean showSaveButton() {
        return true;
    }

    public boolean showSaveAndCloseButton() {
        return true;
    }

    public boolean showCopyButton() {
        return true;
    }

    public boolean showRenameButton() {
        return true;
    }

    public boolean showPromoteToGlobalButton() {
        return true;
    }

    public boolean showArchiveButton() {
        return true;
    }

    public boolean showDeleteButton() {
        return true;
    }

    public boolean showChangeStatusButton() {
        return true;
    }

    public boolean showSelectWorkingSetsButton() {
        return true;
    }

    public boolean showValidateButton() {
        return this.isValidatorTypeAsset();
    }

    public boolean showVerifyButton() {
        return this.isVerificationTypeAsset();
    }

    public boolean showViewSourceButton() {
        return true;
    }

    public boolean showStateLabel() {
        return true;
    }

    private boolean isValidatorTypeAsset() {
        return true;
    }

    private boolean isVerificationTypeAsset() {
        return true;
    }

}
