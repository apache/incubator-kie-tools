/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ExportToPngSessionCommandTest extends AbstractExportSessionCommandTest {

    private ExportToPngSessionCommand tested;

    @Before
    public void setup() {
        super.setup();
        this.tested = new ExportToPngSessionCommand(canvasFileExport);
        this.tested.bind(session);
        setTimer(this.tested);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExport() {
        this.tested.execute(callback);

        verify(canvasFileExport,
               times(1)).exportToPng(eq(canvasHandler),
                                     eq(FILE_NAME));
        verify(canvasFileExport,
               never()).exportToJpg(any(AbstractCanvasHandler.class),
                                    anyString());
        verify(canvasFileExport,
               never()).exportToPdf(any(AbstractCanvasHandler.class),
                                    anyString());
    }
}
