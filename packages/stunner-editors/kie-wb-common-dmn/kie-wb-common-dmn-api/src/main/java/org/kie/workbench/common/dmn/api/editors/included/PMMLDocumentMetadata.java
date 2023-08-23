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

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class PMMLDocumentMetadata {

    static final String UNDEFINED_MODEL_NAME = "undefined";

    private final String path;

    private final String name;

    private final String importType;

    private final List<PMMLModelMetadata> models;

    public PMMLDocumentMetadata(final String path,
                                final String importType,
                                final List<PMMLModelMetadata> models) {
        this(path,
             UNDEFINED_MODEL_NAME,
             importType,
             models);
    }

    public PMMLDocumentMetadata(final @MapsTo("path") String path,
                                final @MapsTo("name") String name,
                                final @MapsTo("importType") String importType,
                                final @MapsTo("models") List<PMMLModelMetadata> models) {
        this.path = path;
        this.name = name;
        this.importType = importType;
        this.models = models;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getImportType() {
        return importType;
    }

    public List<PMMLModelMetadata> getModels() {
        return models;
    }
}
