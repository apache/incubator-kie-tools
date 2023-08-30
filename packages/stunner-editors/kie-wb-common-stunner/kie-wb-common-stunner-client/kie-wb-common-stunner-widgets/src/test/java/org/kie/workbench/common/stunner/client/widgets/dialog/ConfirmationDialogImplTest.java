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


package org.kie.workbench.common.stunner.client.widgets.dialog;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(GwtMockitoTestRunner.class)
public class ConfirmationDialogImplTest {

    @Mock
    private ConfirmationDialogImpl.View view;

    private ConfirmationDialogImpl confirmationDialog;

    @Before
    public void setup() {
        confirmationDialog = spy(new ConfirmationDialogImpl(view));
    }

    @Test
    public void testShow() {

        final String title = "title";
        final String boldDescription = "bold description";
        final String question = "question";
        final Command onYesAction = mock(Command.class);
        final Command onNoAction = mock(Command.class);

        doNothing().when(confirmationDialog).superSetup();
        doNothing().when(confirmationDialog).show();
        doNothing().when(confirmationDialog).setModalWidth();

        final InOrder inOrder = Mockito.inOrder(confirmationDialog, view);

        confirmationDialog.show(title,
                                boldDescription,
                                question,
                                onYesAction,
                                onNoAction);

        inOrder.verify(view).init(confirmationDialog);
        inOrder.verify(view).initialize(title, boldDescription, question, onYesAction, onNoAction);
        inOrder.verify(confirmationDialog).superSetup();
        inOrder.verify(confirmationDialog).show();
    }
}
