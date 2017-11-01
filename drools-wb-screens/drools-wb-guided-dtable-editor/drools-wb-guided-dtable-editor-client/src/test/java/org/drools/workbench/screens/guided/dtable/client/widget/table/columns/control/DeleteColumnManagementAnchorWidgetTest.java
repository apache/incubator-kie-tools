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

package org.drools.workbench.screens.guided.dtable.client.widget.table.columns.control;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DeleteColumnManagementAnchorWidgetTest {

    @Mock
    private Command deleteCommand;

    @Mock
    private DeletePopUpPresenter deletePopUpPresenter;

    @Captor
    private ArgumentCaptor<ClickHandler> clickCaptor;

    @Captor
    private ArgumentCaptor<ParameterizedCommand> commandCaptor;

    private static final String COLUMN_HEADER = "column header";

    private DeleteColumnManagementAnchorWidget widget;

    @Before
    public void setUp() throws Exception {

        widget = spy(new DeleteColumnManagementAnchorWidget(deletePopUpPresenter));
        widget.init(COLUMN_HEADER,
                    deleteCommand);
    }

    @Test
    public void testSetupWidget() throws Exception {
        widget.setupWidget();

        verify(widget).setText(GuidedDecisionTableConstants.INSTANCE.Delete());
        verify(widget).setTitle(GuidedDecisionTableConstants.INSTANCE.DeleteThisColumn());
        verify(widget).addClickHandler(clickCaptor.capture());

        clickCaptor.getValue().onClick(null);

        verify(deletePopUpPresenter).setPrompt(GuidedDecisionTableConstants.INSTANCE.DeleteColumnWarning(COLUMN_HEADER));
        verify(deletePopUpPresenter).setCommentIsHidden(true);
        verify(deletePopUpPresenter).show(commandCaptor.capture());

        commandCaptor.getValue().execute(null);

        verify(deleteCommand).execute();
    }
}
