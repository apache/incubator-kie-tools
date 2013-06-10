/**
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

package org.kie.workbench.common.screens.datamodeller.model;

import org.jboss.errai.common.client.api.annotations.Portable;

import java.util.HashMap;
import java.util.Map;

@Portable
public class GenerationResult {

    long generationTime;

    Map<String, String> objectFingerPrints = new HashMap<String, String>();

    public GenerationResult() {
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
}
