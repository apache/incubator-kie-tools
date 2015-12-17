/**
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.uberfire.backend.vfs.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Portable
public class GenerationResult extends DataModelerResult {

    long generationTime;

    Map<String, String> objectFingerPrints = new HashMap<String, String>();

    private String source;

    private DataObject dataObject;

    private Path path;

    public GenerationResult() {
    }

    public GenerationResult( String source, DataObject dataObject, List<DataModelerError> errors ) {
        this.source = source;
        this.dataObject = dataObject;
        this.errors = errors;
    }

    public long getGenerationTime() {
        return generationTime;
    }

    public long getGenerationTimeSeconds() {
        return generationTime / 1000;
    }

    public void setGenerationTime(long generationTime) {
        this.generationTime = generationTime;
    }

    public Map<String, String> getObjectFingerPrints() {
        return objectFingerPrints;
    }

    public void setObjectFingerPrints(Map<String, String> objectFingerPrints) {
        this.objectFingerPrints = objectFingerPrints;
    }

    public String getSource() {
        return source;
    }

    public void setSource( String source ) {
        this.source = source;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    public void setDataObject( DataObject dataObject ) {
        this.dataObject = dataObject;
    }

    public Path getPath() {
        return path;
    }

    public void setPath( Path path ) {
        this.path = path;
    }
}
