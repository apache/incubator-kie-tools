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
import static org.uberfire.ext.editor.commons.client.file.exports.jso.FileExportScriptInjector.buildNamespaceObject;

@RunWith(GwtMockitoTestRunner.class)
public class FileExportScriptInjectorTest {

    public static final String NS = "window.";
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
        final String fsNsObject = buildNamespaceObject(NS + JsFileSaver.class.getSimpleName() + ".saveAs");
        final String jsPdfNsObject = buildNamespaceObject(NS + JsPdf.class.getSimpleName());
        final String c2sNsObject = FileExportResources.INSTANCE.canvas2svg().getText();
        assertEquals("var " +
                             fsNsObject +
                             " = function(blob, fileName, disableAutoBOM) {\n" +
                             "fileSaver\n" +
                             "return saveAs(blob, fileName, disableAutoBOM);};\n" +
                             jsPdfNsObject +
                             " = function(settings) {\n" +
                             "jsPdf\n" +
                             "var saveAs = " + NS + "JsFileSaver.saveAs; " +
                             "return new jsPDF(settings);};" + "\n" +
                             c2sNsObject + "\n",
                     script);
    }

    @Test
    public void testNamespaces() {
        assertEquals("window = window || {};\n" + NS + "JsFileSaver",
                     buildNamespaceObject(NS + JsFileSaver.class.getSimpleName()));
        assertEquals("window = window || {};\n" + NS + "JsPdf",
                     buildNamespaceObject(NS + JsPdf.class.getSimpleName()));
        assertEquals("nonamespace", buildNamespaceObject("nonamespace"));
    }
}
