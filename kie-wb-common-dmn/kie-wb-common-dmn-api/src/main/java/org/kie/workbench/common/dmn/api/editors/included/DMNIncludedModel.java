/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.api.editors.included;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DMNIncludedModel {

    private String modelName;

    private String modelPackage;

    private String path;

    private String namespace;

    private final Integer drgElementsCount;

    private final Integer itemDefinitionsCount;

    public DMNIncludedModel(final @MapsTo("modelName") String modelName,
                            final @MapsTo("modelPackage") String modelPackage,
                            final @MapsTo("path") String path,
                            final @MapsTo("namespace") String namespace,
                            final @MapsTo("drgElementsCount") Integer drgElementsCount,
                            final @MapsTo("itemDefinitionsCount") Integer itemDefinitionsCount) {
        this.modelName = modelName;
        this.modelPackage = modelPackage;
        this.path = path;
        this.namespace = namespace;
        this.drgElementsCount = drgElementsCount;
        this.itemDefinitionsCount = itemDefinitionsCount;
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelPackage() {
        return modelPackage;
    }

    public String getPath() {
        return path;
    }

    public String getNamespace() {
        return namespace;
    }

    public Integer getDrgElementsCount() {
        return drgElementsCount;
    }

    public Integer getItemDefinitionsCount() {
        return itemDefinitionsCount;
    }
}
