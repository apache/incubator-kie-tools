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

package org.kie.workbench.common.dmn.client.editors.documentation.common;

import java.util.List;

import com.google.gwt.core.client.GWT;
import elemental2.core.Array;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.core.documentation.model.DiagramDocumentation;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class DMNDocumentation implements DiagramDocumentation {

    private String namespace;

    private String diagramName;

    private String diagramDescription;

    private String diagramImage;

    private String fileName;

    private String currentDate;

    private String currentUser;

    private String moduleName;

    private Array<DMNDocumentationDataType> dataTypes;

    private Array<DMNDocumentationDRD> drds;

    private List<DMNDocumentationDataType> dataTypesList;

    private boolean hasGraphNodes;

    private DMNDocumentationI18n i18n;

    private DMNDocumentation() {

    }

    @JsOverlay
    public static DMNDocumentation create(final String namespace,
                                          final String fileName,
                                          final String diagramName,
                                          final String diagramDescription,
                                          final boolean hasGraphNodes,
                                          final List<DMNDocumentationDataType> dataTypes,
                                          final List<DMNDocumentationDRD> drds,
                                          final String diagramImage,
                                          final String currentUser,
                                          final String currentDate,
                                          final DMNDocumentationI18n i18n) {

        final DMNDocumentation dmn = new DMNDocumentation();

        dmn.namespace = namespace;
        dmn.diagramName = diagramName;
        dmn.diagramDescription = diagramDescription;
        dmn.fileName = fileName;
        dmn.currentUser = currentUser;
        dmn.currentDate = currentDate;
        dmn.diagramImage = diagramImage;
        dmn.hasGraphNodes = hasGraphNodes;
        dmn.moduleName = GWT.getModuleName();
        dmn.dataTypes = asJsArray(dataTypes);
        dmn.drds = asJsArray(drds);
        dmn.dataTypesList = dataTypes;
        dmn.i18n = i18n;

        return dmn;
    }

    @JsOverlay
    public final String getNamespace() {
        return namespace;
    }

    @JsOverlay
    public final String getDiagramName() {
        return diagramName;
    }

    @JsOverlay
    public final String getDiagramDescription() {
        return diagramDescription;
    }

    @JsOverlay
    public final String getDiagramImage() {
        return diagramImage;
    }

    @JsOverlay
    public final String getFileName() {
        return fileName;
    }

    @JsOverlay
    public final String getCurrentDate() {
        return currentDate;
    }

    @JsOverlay
    public final String getCurrentUser() {
        return currentUser;
    }

    @JsOverlay
    public final String getModuleName() {
        return moduleName;
    }

    @JsOverlay
    public final Array<DMNDocumentationDataType> getDataTypes() {
        return dataTypes;
    }

    @JsOverlay
    public final Array<DMNDocumentationDRD> getDrds() {
        return drds;
    }

    @JsOverlay
    public final List<DMNDocumentationDataType> getDataTypesList() {
        return dataTypesList;
    }

    @JsOverlay
    public final boolean hasGraphNodes() {
        return hasGraphNodes;
    }

    @JsOverlay
    public final DMNDocumentationI18n getI18n() {
        return i18n;
    }

    @JsOverlay
    @SuppressWarnings("unchecked")
    static <T> Array<T> asJsArray(final List<T> javaList) {
        final Array<T> jsArray = new Array<>();
        javaList.forEach(jsArray::push);
        return jsArray;
    }
}
