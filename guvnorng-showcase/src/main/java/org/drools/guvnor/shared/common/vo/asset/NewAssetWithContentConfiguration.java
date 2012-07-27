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

package org.drools.guvnor.shared.common.vo.asset;

import org.drools.guvnor.shared.api.PortableObject;

/**
 * The configuration information required for a new Asset with default content
 */
public abstract class NewAssetWithContentConfiguration<T extends PortableObject> extends NewAssetConfiguration {

    private T content;

    // For GWT serialisation
    public NewAssetWithContentConfiguration() {
    }

    public NewAssetWithContentConfiguration(String assetName,
                                            String packageName,
                                            String packageUUID,
                                            String description,
                                            String initialCategory,
                                            String format,
                                            T content) {
        super( assetName,
               packageName,
               packageUUID,
               description,
               initialCategory,
               format );
        this.content = content;
    }

    // ************************************************************************
    // Getters
    // ************************************************************************

    public T getContent() {
        return content;
    }

}
