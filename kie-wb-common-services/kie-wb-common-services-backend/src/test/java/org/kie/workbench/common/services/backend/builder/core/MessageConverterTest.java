/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.backend.builder.core;

import java.util.function.Supplier;

import org.drools.core.kie.impl.MessageImpl;
import org.guvnor.common.services.project.builder.model.BuildMessage;
import org.guvnor.common.services.shared.message.Level;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.builder.Message;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageConverterTest {

    private static final int ID = 1;
    private static final String FILE = "path";
    private static final String TEXT = "text";
    private static final String KIE_BASE_NAME = "kieBaseName";

    @Mock
    private Path path;

    private Handles handles = new Handles();

    @Before
    public void setup() {
        handles.put(FILE,
                    path);
        when(path.getFileName()).thenReturn(FILE);
    }

    @Test
    public void checkMessageWithKieBase() {
        final MessageImpl m = new MessageImpl(ID,
                                              Message.Level.ERROR,
                                              FILE,
                                              TEXT);
        m.setKieBaseName(KIE_BASE_NAME);
        final BuildMessage bm = MessageConverter.convertMessage(m,
                                                                handles);

        assertConversion(bm,
                         () -> "[KBase: " + KIE_BASE_NAME + "]: " + TEXT);
    }

    @Test
    public void checkMessageWithoutKieBase() {
        final Message m = new MessageImpl(ID,
                                          Message.Level.ERROR,
                                          FILE,
                                          TEXT);
        final BuildMessage bm = MessageConverter.convertMessage(m,
                                                                handles);

        assertConversion(bm,
                         () -> TEXT);
    }

    private void assertConversion(final BuildMessage bm,
                                  final Supplier<String> text) {
        assertEquals(ID,
                     bm.getId());
        assertEquals(Level.ERROR,
                     bm.getLevel());
        assertEquals(FILE,
                     bm.getPath().toString());
        assertEquals(text.get(),
                     bm.getText());
    }
}
