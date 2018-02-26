/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.properties;

import java.util.stream.Collectors;

import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;

public class ProcessPropertyReader extends BasePropertyReader {

    private final Process process;

    public ProcessPropertyReader(Process element, BPMNPlane plane) {
        super(element, plane);
        this.process = element;
    }

    public String getPackageName() {
        return optionalAttribute("packageName").orElse(null);
    }

    public String getVersion() {
        return optionalAttribute("version").orElse("1.0");
    }

    public boolean isAdHoc() {
        return Boolean.parseBoolean(attribute("adHoc"));
    }

    public boolean isAsync() {
        return Boolean.parseBoolean(metaData("customAsync"));
    }

    @Override
    public Bounds getBounds() {
        return new BoundsImpl(
                new BoundImpl(0d, 0d),
                new BoundImpl(950d, 950d));
    }

    public String getProcessVariables() {
        return process.getProperties()
                .stream()
                .map(p -> p.getId() + ":" + p.getItemSubjectRef().getStructureRef())
                .collect(Collectors.joining(","));
    }
}
