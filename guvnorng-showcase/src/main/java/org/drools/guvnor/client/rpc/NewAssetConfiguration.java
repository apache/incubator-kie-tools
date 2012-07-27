/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The configuration information required for a new Asset with no default
 * content
 */
public class NewAssetConfiguration
    implements
    IsSerializable {

    private String assetName;
    private String packageName;
    private String packageUUID;
    private String description;
    private String initialCategory;
    private String format;

    // For GWT serialisation
    public NewAssetConfiguration() {
    }

    public NewAssetConfiguration(String assetName,
                                 String packageName,
                                 String packageUUID,
                                 String description,
                                 String initialCategory,
                                 String format) {
        this.assetName = assetName;
        this.packageName = packageName;
        this.packageUUID = packageUUID;
        this.description = description;
        this.initialCategory = initialCategory;
        this.format = format;
    }

    // ************************************************************************
    // Getters
    // ************************************************************************

    public String getAssetName() {
        return assetName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getPackageUUID() {
        return packageUUID;
    }

    public String getDescription() {
        return description;
    }

    public String getInitialCategory() {
        return initialCategory;
    }

    public String getFormat() {
        return format;
    }

}
