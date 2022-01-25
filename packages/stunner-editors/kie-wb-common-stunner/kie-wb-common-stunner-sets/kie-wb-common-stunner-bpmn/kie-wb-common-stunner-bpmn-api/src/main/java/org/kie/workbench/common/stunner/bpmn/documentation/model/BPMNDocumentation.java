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

package org.kie.workbench.common.stunner.bpmn.documentation.model;

import com.google.gwt.core.client.GWT;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.bpmn.documentation.model.element.ElementDetails;
import org.kie.workbench.common.stunner.bpmn.documentation.model.general.ProcessOverview;
import org.kie.workbench.common.stunner.core.documentation.model.DiagramDocumentation;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class BPMNDocumentation implements DiagramDocumentation {

    private ProcessOverview process;
    private ElementDetails elementsDetails;
    private String diagramImage;
    private String moduleName;

    private BPMNDocumentation() {
    }

    @JsOverlay
    public static final BPMNDocumentation create(ProcessOverview process, ElementDetails elementsDetails,
                                                 String diagramImage) {
        final BPMNDocumentation instance = new BPMNDocumentation();
        instance.process = process;
        instance.elementsDetails = elementsDetails;
        instance.diagramImage = diagramImage;
        instance.moduleName = GWT.getModuleName();
        return instance;
    }

    @JsOverlay
    public final ProcessOverview getProcess() {
        return process;
    }

    @JsOverlay
    public final ElementDetails getElementsDetails() {
        return elementsDetails;
    }

    @JsOverlay
    public final String getDiagramImage() {
        return diagramImage;
    }

    @JsOverlay
    public final String getModuleName() {
        return moduleName;
    }
}
