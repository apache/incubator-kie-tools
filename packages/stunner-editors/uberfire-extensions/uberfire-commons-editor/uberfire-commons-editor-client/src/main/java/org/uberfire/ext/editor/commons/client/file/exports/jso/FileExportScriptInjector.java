/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.editor.commons.client.file.exports.jso;

import java.util.Arrays;
import java.util.function.Consumer;

import javax.enterprise.context.ApplicationScoped;

import com.google.gwt.core.client.ScriptInjector;
import org.uberfire.ext.editor.commons.client.file.exports.FileExportResources;

/**
 * This bean wraps third party libraries related to file exporting
 * into javascript objects, by following the namespace pattern.
 * This bean allows:
 * - avoiding namespaces collisions within any other libraries
 * - no need to manually apply changes into third party js source files
 * - no conflicts during third party libraries version updates
 */
@ApplicationScoped
public class FileExportScriptInjector {

    public static final String NS_SEPARATOR = ".";
    public static final String NS = "window" + NS_SEPARATOR;
    public static final String JS_OBJ_SUFFIX = " || {};";

    private final Consumer<String> scriptInjector;

    public FileExportScriptInjector() {
        this.scriptInjector = FileExportScriptInjector::inject;
    }

    FileExportScriptInjector(final Consumer<String> scriptInjector) {
        this.scriptInjector = scriptInjector;
    }

    public void inject() {
        final String fileSaver = getFileSaverSource();
        final String jsPdf = getJsPdfSource();
        final String c2sSource = getC2SSource();
        scriptInjector.accept("var " + fileSaver + "\n" +
                                      jsPdf + "\n" +
                                      c2sSource + "\n");
    }

    private String getFileSaverSource() {
        final String fsScript = FileExportResources.INSTANCE.fileSaver().getText();
        final String fsNsObject = buildNamespaceObject(NS + "JsFileSaver.saveAs");
        return fsNsObject + " = function(blob, fileName, disableAutoBOM) {" + "\n" +
                fsScript + "\n" +
                "return saveAs(blob, fileName, disableAutoBOM);};";
    }

    private String getJsPdfSource() {
        final String jsPdfScript = FileExportResources.INSTANCE.jsPdf().getText();
        final String jsPdfNsObject = buildNamespaceObject(NS + "JsPdf");
        return jsPdfNsObject + " = function(settings) {" + "\n" +
                jsPdfScript + "\n" +
                "var saveAs = " + NS + "JsFileSaver.saveAs; " +
                "return new jsPDF(settings);};";
    }

    private String getC2SSource() {
        return FileExportResources.INSTANCE.canvas2svg().getText();
    }

    private static void inject(final String raw) {
        final ScriptInjector.FromString jsPdfScript = ScriptInjector.fromString(raw);
        jsPdfScript.setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(false).inject();
    }

    static String buildNamespaceObject(final String namespace) {
        final int pkgSepIndex = namespace.lastIndexOf(NS_SEPARATOR);
        String raw = "";
        if (pkgSepIndex > 0) {
            final String nsPkg = namespace.substring(0,
                                                     pkgSepIndex);
            final String[] nsPkgObject = {"", ""};
            Arrays.asList(nsPkg.split("\\."))
                    .forEach(p -> {
                        nsPkgObject[0] += p;
                        nsPkgObject[1] += nsPkgObject[0] + " = " + nsPkgObject[0] + JS_OBJ_SUFFIX + "\n";
                        nsPkgObject[0] += NS_SEPARATOR;
                    });
            raw = nsPkgObject[1];
        }
        raw += namespace;
        return raw;
    }
}

