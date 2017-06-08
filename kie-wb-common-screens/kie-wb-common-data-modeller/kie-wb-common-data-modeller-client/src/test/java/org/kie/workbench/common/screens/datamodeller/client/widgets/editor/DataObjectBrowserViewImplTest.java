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

package org.kie.workbench.common.screens.datamodeller.client.widgets.editor;

import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.validation.DataObjectValidationService;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataObjectBrowserViewImplTest {

    @Mock
    private DataObjectValidationService dataObjectValidationService;

    @Mock
    private ValidationPopup validationPopup;

    private DataObjectBrowserViewImpl view;

    @Before
    public void setUp() {
        this.view = new DataObjectBrowserViewImpl(
                validationPopup);
    }

    @Test
    public void showValidationPopupForDeletion() {
        List<ValidationMessage> validationMessages = Collections.EMPTY_LIST;
        Command yesCommand = () -> {};
        Command noCommand = () -> {};

        view.showValidationPopupForDeletion(validationMessages,
                                            yesCommand,
                                            noCommand);

        verify(validationPopup,
               Mockito.times(1)).showDeleteValidationMessages(
                yesCommand,
                noCommand,
                validationMessages);
    }
}
