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

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.ext.editor.commons.client.file.exports.FileExportResources;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class FileExportScriptInjectorTest {

    private FileExportScriptInjector tested;

    @Mock
    private Consumer<String> scriptInjector;

    @Before
    public void setup() {
        tested = new FileExportScriptInjector(scriptInjector);
    }

    @Test
    public void testInject() {
        tested.inject();
        final ArgumentCaptor<String> scriptCaptor = ArgumentCaptor.forClass(String.class);
        verify(scriptInjector,
               times(1)).accept(scriptCaptor.capture());
        final String script = scriptCaptor.getValue();
        final String fsNsObject = FileExportScriptInjector.buildNamespaceObject(JsFileSaver.class.getName() + ".saveAs");
        final String jsPdfNsObject = FileExportScriptInjector.buildNamespaceObject(JsPdf.class.getName());
        final String c2sNsObject = FileExportResources.INSTANCE.canvas2svg().getText();
        assertEquals("var " +
                             fsNsObject +
                             " = function(blob, fileName, disableAutoBOM) {\n" +
                             "fileSaver\n" +
                             "return saveAs(blob, fileName, disableAutoBOM);};\n" +
                             jsPdfNsObject +
                             " = function(settings) {\n" +
                             "jsPdf\n" +
                             "var saveAs = org.uberfire.ext.editor.commons.client.file.exports.jso.JsFileSaver.saveAs; " +
                             "return new jsPDF(settings);};" + "\n" +
                             c2sNsObject + "\n",
                     script);
    }

    @Test
    public void testNamespaces() {
        assertEquals(
                "org = org || {};\n" +
                        "org.uberfire = org.uberfire || {};\n" +
                        "org.uberfire.ext = org.uberfire.ext || {};\n" +
                        "org.uberfire.ext.editor = org.uberfire.ext.editor || {};\n" +
                        "org.uberfire.ext.editor.commons = org.uberfire.ext.editor.commons || {};\n" +
                        "org.uberfire.ext.editor.commons.client = org.uberfire.ext.editor.commons.client || {};\n" +
                        "org.uberfire.ext.editor.commons.client.file = org.uberfire.ext.editor.commons.client.file || {};\n" +
                        "org.uberfire.ext.editor.commons.client.file.exports = org.uberfire.ext.editor.commons.client.file.exports || {};\n" +
                        "org.uberfire.ext.editor.commons.client.file.exports.jso = org.uberfire.ext.editor.commons.client.file.exports.jso || {};\n" +
                        "org.uberfire.ext.editor.commons.client.file.exports.jso.JsFileSaver",
                FileExportScriptInjector.buildNamespaceObject(JsFileSaver.class.getName())
        );
        assertEquals(
                "org = org || {};\n" +
                        "org.uberfire = org.uberfire || {};\n" +
                        "org.uberfire.ext = org.uberfire.ext || {};\n" +
                        "org.uberfire.ext.editor = org.uberfire.ext.editor || {};\n" +
                        "org.uberfire.ext.editor.commons = org.uberfire.ext.editor.commons || {};\n" +
                        "org.uberfire.ext.editor.commons.client = org.uberfire.ext.editor.commons.client || {};\n" +
                        "org.uberfire.ext.editor.commons.client.file = org.uberfire.ext.editor.commons.client.file || {};\n" +
                        "org.uberfire.ext.editor.commons.client.file.exports = org.uberfire.ext.editor.commons.client.file.exports || {};\n" +
                        "org.uberfire.ext.editor.commons.client.file.exports.jso = org.uberfire.ext.editor.commons.client.file.exports.jso || {};\n" +
                        "org.uberfire.ext.editor.commons.client.file.exports.jso.JsPdf",
                FileExportScriptInjector.buildNamespaceObject(JsPdf.class.getName())
        );
        assertEquals("nonamespace",
                     FileExportScriptInjector.buildNamespaceObject("nonamespace"));
    }
}
