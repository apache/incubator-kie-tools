/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.api.editors.included;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class DMNIncludedModel extends BaseIncludedModel {

    private final Integer drgElementsCount;

    private final Integer itemDefinitionsCount;

    public DMNIncludedModel(final @MapsTo("modelName") String modelName,
                            final @MapsTo("modelPackage") String modelPackage,
                            final @MapsTo("path") String path,
                            final @MapsTo("namespace") String namespace,
                            final @MapsTo("importType") String importType,
                            final @MapsTo("drgElementsCount") Integer drgElementsCount,
                            final @MapsTo("itemDefinitionsCount") Integer itemDefinitionsCount) {
        super(modelName,
              modelPackage,
              path,
              importType,
              namespace);
        this.drgElementsCount = drgElementsCount;
        this.itemDefinitionsCount = itemDefinitionsCount;
    }

    public Integer getDrgElementsCount() {
        return drgElementsCount;
    }

    public Integer getItemDefinitionsCount() {
        return itemDefinitionsCount;
    }
}
