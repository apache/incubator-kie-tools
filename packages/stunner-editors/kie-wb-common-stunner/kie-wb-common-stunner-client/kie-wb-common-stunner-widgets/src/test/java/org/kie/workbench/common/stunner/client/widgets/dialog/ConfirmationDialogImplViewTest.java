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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLParagraphElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class ConfirmationDialogImplViewTest {

    private ConfirmationDialogImplView confirmationDialog;

    @Mock
    private HTMLDivElement header;

    @Mock
    private HTMLDivElement body;

    @Mock
    private HTMLDivElement footer;

    @Mock
    private HTMLButtonElement yesButton;

    @Mock
    private HTMLButtonElement noButton;

    @Mock
    private HTMLParagraphElement boldDescription;

    @Mock
    private HTMLParagraphElement question;

    @Mock
    private ConfirmationDialogImpl presenter;

    private static final String TitleString = "title";

    private static final String BoldDescriptionString = "description";

    private static final String QuestionString = "question";

    @Mock
    private Command onConfirmAction;

    @Mock
    private Command onCancelAction;

    @Before
    public void setup() {
        confirmationDialog = new ConfirmationDialogImplView(header,
                                                            body,
                                                            footer,
                                                            yesButton,
                                                            noButton,
                                                            boldDescription,
                                                            question);
        confirmationDialog.init(presenter);
        confirmationDialog.initialize(TitleString,
                                      BoldDescriptionString,
                                      QuestionString,
                                      onConfirmAction,
                                      onCancelAction);
    }

    @Test
    public void testGetHeader() {

        final String textContent = "some text";
        header.textContent = textContent;

        final String actualHeader = confirmationDialog.getHeader();

        assertEquals(textContent, actualHeader);
    }

    @Test
    public void testGetBody() {

        final HTMLElement actualBody = confirmationDialog.getBody();

        assertEquals(body, actualBody);
    }

    @Test
    public void testGetFooter() {

        final HTMLElement actualFooter = confirmationDialog.getFooter();

        assertEquals(footer, actualFooter);
    }

    @Test
    public void testOnYesButtonClick() {

        final ClickEvent clickEvent = mock(ClickEvent.class);

        confirmationDialog.onYesButtonClick(clickEvent);

        verify(presenter).hide();
        verify(onConfirmAction).execute();
    }

    @Test
    public void testOnNoButtonClick() {

        final ClickEvent clickEvent = mock(ClickEvent.class);

        confirmationDialog.onNoButtonClick(clickEvent);

        verify(presenter).hide();
        verify(onCancelAction).execute();
    }
}
