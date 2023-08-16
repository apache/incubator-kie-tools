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


package org.kie.workbench.common.stunner.bpmn.documentation.model.general;

import java.util.List;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Imports {

    private String defaultImportsHidden;
    private String wsdlImportsHidden;
    private String noImportsHidden;
    private String importsTableHidden;
    private Integer totalDefaultImports;
    private Integer totalWSDLImports;
    private DefaultImport[] defaultImports;
    private WSDLImport[] wsdlImports;

    private Imports() {
    }

    @JsOverlay
    public static final Imports create(List<DefaultImport> defaultImports,
                                       List<WSDLImport> wsdlImports) {
        final String hidden = "hidden";
        final String visible = "";
        final Imports instance = new Imports();

        instance.totalDefaultImports = defaultImports.size();
        instance.totalWSDLImports = wsdlImports.size();
        instance.defaultImports = defaultImports.toArray(new DefaultImport[defaultImports.size()]);
        instance.wsdlImports = wsdlImports.toArray(new WSDLImport[wsdlImports.size()]);

        instance.defaultImportsHidden = hidden;
        instance.wsdlImportsHidden = hidden;
        instance.noImportsHidden = visible;
        instance.importsTableHidden = hidden;

        if (!defaultImports.isEmpty()) {
            instance.defaultImportsHidden = visible;
            instance.importsTableHidden = visible;
            instance.noImportsHidden = hidden;
        }

        if (!wsdlImports.isEmpty()) {
            instance.wsdlImportsHidden = visible;
            instance.importsTableHidden = visible;
            instance.noImportsHidden = hidden;
        }

        return instance;
    }

    @JsOverlay
    public final String getDefaultImportsHidden() {
        return defaultImportsHidden;
    }

    @JsOverlay
    public final String getWSDLImportsHidden() {
        return wsdlImportsHidden;
    }

    @JsOverlay
    public final String getImportsTableHidden() {
        return importsTableHidden;
    }

    @JsOverlay
    public final String getNoImportsHidden() {
        return noImportsHidden;
    }

    @JsOverlay
    public final Integer getTotalDefaultImports() {
        return totalDefaultImports;
    }

    @JsOverlay
    public final Integer getTotalWSDLImports() {
        return totalWSDLImports;
    }

    @JsOverlay
    public final DefaultImport[] getDefaultImports() {
        return defaultImports;
    }

    @JsOverlay
    public final WSDLImport[] getWSDLImports() {
        return wsdlImports;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class DefaultImport {

        private String className;

        private DefaultImport() {
        }

        @JsOverlay
        public static final DefaultImport create(final String className) {
            final DefaultImport instance = new DefaultImport();
            instance.className = className;
            return instance;
        }

        @JsOverlay
        public final String getClassName() {
            return className;
        }
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class WSDLImport {

        private String location;

        private String namespace;

        private WSDLImport() {
        }

        @JsOverlay
        public static final WSDLImport create(final String location, final String namespace) {
            final WSDLImport instance = new WSDLImport();
            instance.location = location;
            instance.namespace = namespace;
            return instance;
        }

        @JsOverlay
        public final String getLocation() {
            return location;
        }

        @JsOverlay
        public final String getNamespace() {
            return namespace;
        }
    }
}
