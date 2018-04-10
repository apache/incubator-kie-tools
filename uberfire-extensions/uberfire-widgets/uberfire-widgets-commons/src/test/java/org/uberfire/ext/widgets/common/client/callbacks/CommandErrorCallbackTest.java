/*
 * Copyright 2018 JBoss, by Red Hat, Inc
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
package org.uberfire.ext.widgets.common.client.callbacks;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Modal.class})
public class CommandErrorCallbackTest {

    @Test
    public void verifyCommandIsRanOnError() throws Exception {
        final Command command = mock(Command.class);

        new CommandErrorCallback(command).error(mock(Message.class), mock(org.uberfire.java.nio.file.AccessDeniedException.class));

        verify(command).execute();
    }

    @Test(expected = IllegalArgumentException.class)
    public void doNotAcceptNulls() throws Exception {
        new CommandErrorCallback(null);
    }
}