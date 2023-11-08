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


package org.kie.workbench.common.stunner.core.client.session.command.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExportToPdfSessionCommandTest extends AbstractExportSessionCommandTest {

    private static final String FILE_NAME = "file-name1";

    private ExportToPdfSessionCommand tested;

    @Before
    public void setup() {
        super.setup();
        this.tested = new ExportToPdfSessionCommand(canvasFileExport);
        this.tested.bind(session);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testExport() {
        this.tested.execute(callback);

        verify(canvasFileExport,
               times(1)).exportToPdf(eq(canvasHandler),
                                     eq(FILE_NAME));
        verify(canvasFileExport,
               never()).exportToJpg(any(AbstractCanvasHandler.class),
                                    anyString());
        verify(canvasFileExport,
               never()).exportToPng(any(AbstractCanvasHandler.class),
                                    anyString());
    }

    @Override
    protected AbstractClientSessionCommand getCommand() {
        return tested;
    }
}
