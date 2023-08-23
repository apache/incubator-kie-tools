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

package org.kie.workbench.common.dmn.client.editors.documentation.common;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.DataResource;
import elemental2.core.JsArray;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.stunner.core.documentation.model.DiagramDocumentation;

import static org.kie.workbench.common.dmn.client.resources.DMNImageResources.INSTANCE;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class DMNDocumentation implements DiagramDocumentation {

    private String droolsLogoURI;

    private String namespace;

    private String diagramName;

    private String diagramDescription;

    private String diagramImage;

    private String currentDate;

    private String currentYear;

    private String moduleName;

    private JsArray<DMNDocumentationDataType> dataTypes;

    private JsArray<DMNDocumentationDRD> drds;

    private List<DMNDocumentationDataType> dataTypesList;

    private boolean hasGraphNodes;

    private DMNDocumentationI18n i18n;

    private DMNDocumentation() {

    }

    @JsOverlay
    public static DMNDocumentation create(final String namespace,
                                          final String diagramName,
                                          final String diagramDescription,
                                          final boolean hasGraphNodes,
                                          final List<DMNDocumentationDataType> dataTypes,
                                          final List<DMNDocumentationDRD> drds,
                                          final String diagramImage,
                                          final String currentDate,
                                          final String currentYear,
                                          final DMNDocumentationI18n i18n) {

        final DMNDocumentation dmn = new DMNDocumentation();

        dmn.droolsLogoURI = getURI(INSTANCE.droolsLogo());
        dmn.namespace = namespace;
        dmn.diagramName = diagramName;
        dmn.diagramDescription = diagramDescription;
        dmn.currentDate = currentDate;
        dmn.currentYear = currentYear;
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
    public final String getCurrentDate() {
        return currentDate;
    }

    @JsOverlay
    public final String getCurrentYear() {
        return currentYear;
    }

    @JsOverlay
    public final String getModuleName() {
        return moduleName;
    }

    @JsOverlay
    public final JsArray<DMNDocumentationDataType> getDataTypes() {
        return dataTypes;
    }

    @JsOverlay
    public final JsArray<DMNDocumentationDRD> getDrds() {
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
    public final String getDroolsLogoURI() {
        return droolsLogoURI;
    }

    @JsOverlay
    private static String getURI(final DataResource dataResource) {
        return dataResource.getSafeUri().asString();
    }

    @JsOverlay
    @SuppressWarnings("unchecked")
    static <T> JsArray<T> asJsArray(final List<T> javaList) {
        final JsArray<T> jsArray = new JsArray<>();
        javaList.forEach(jsArray::push);
        return jsArray;
    }
}
