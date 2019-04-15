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
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;

@Portable
public class DMNIncludedNode {

    private String fileName;

    private String modelName;

    private String drgElementId;

    private String drgElementName;

    private Class<? extends DRGElement> drgElementClass;

    public DMNIncludedNode(final @MapsTo("fileName") String fileName,
                           final @MapsTo("modelName") String modelName,
                           final @MapsTo("drgElementId") String drgElementId,
                           final @MapsTo("drgElementName") String drgElementName,
                           final @MapsTo("drgElementClass") Class<? extends DRGElement> drgElementClass) {
        this.fileName = fileName;
        this.modelName = modelName;
        this.drgElementId = drgElementId;
        this.drgElementName = drgElementName;
        this.drgElementClass = drgElementClass;
    }

    public String getFileName() {
        return fileName;
    }

    public String getModelName() {
        return modelName;
    }

    public String getDrgElementId() {
        return drgElementId;
    }

    public String getImportedElementId() {
        return modelName + ":" + drgElementId;
    }

    public String getDrgElementName() {
        return drgElementName;
    }

    public Class<? extends DRGElement> getDrgElementClass() {
        return drgElementClass;
    }
}
