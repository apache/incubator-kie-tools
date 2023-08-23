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
package org.kie.workbench.common.dmn.api.definition.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;

/**
 * Specialisation of {@link Import} that has definitions and elements counts.
 */
@Portable
public class ImportDMN extends Import {

    private int itemDefinitionsCount;

    private int drgElementsCount;

    public ImportDMN() {
        super();
    }

    public ImportDMN(final String namespace,
                     final LocationURI locationURI,
                     final String importType) {
        super(namespace,
              locationURI,
              importType);
    }

    public int getItemDefinitionsCount() {
        return itemDefinitionsCount;
    }

    public void setItemDefinitionsCount(final int itemDefinitionsCount) {
        this.itemDefinitionsCount = itemDefinitionsCount;
    }

    public int getDrgElementsCount() {
        return drgElementsCount;
    }

    public void setDrgElementsCount(final int drgElementsCount) {
        this.drgElementsCount = drgElementsCount;
    }
}
