/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.ala.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Class for getting information about a pipeline definition.
 */
@Portable
public class Pipeline
        extends AbstractHasKeyObject<PipelineKey> {

    private List<Stage> stages = new ArrayList<>();

    public Pipeline(@MapsTo("key") final PipelineKey key,
                    @MapsTo("stages") final List<Stage> stages) {
        super(key);
        this.stages = stages;
    }

    public Pipeline(final PipelineKey key) {
        super(key);
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void addStage(final Stage stage) {
        stages.add(stage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Pipeline pipeline = (Pipeline) o;

        return stages != null ? stages.equals(pipeline.stages) : pipeline.stages == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + (stages != null ? stages.hashCode() : 0);
        result = ~~result;
        return result;
    }
}