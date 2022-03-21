/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.popup;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.InlineNotification;
import org.uberfire.mvp.Command;

@RunWith(LienzoMockitoTestRunner.class)
public abstract class AbstractYesNoConfirmYesNoConfirmPopupViewTest {

    @Mock
    protected Command okCommandMock;

    @Mock
    protected Command yesCommandMock;

    @Mock
    protected Command noCommandMock;

    protected final Button.ButtonStyleType BUTTON_STYLE_TYPE = Button.ButtonStyleType.SUCCESS;
    protected final InlineNotification.InlineNotificationType INLINE_NOTIFICATION_TYPE = InlineNotification.InlineNotificationType.SUCCESS;
}