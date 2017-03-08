/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.modal;

import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.gwtbootstrap3.client.ui.constants.ModalBackdrop;
import org.gwtbootstrap3.client.ui.html.Text;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub(Text.class)
public class Bs3ModalTest {

    @Spy
    Bs3Modal modal;

    @Test
    public void testDefaultSettings() {
        modal.setup();

        verify(modal).setDataBackdrop(ModalBackdrop.STATIC);
        verify(modal).setFade(true);
        verify(modal).setId(anyString());
        verify(modal).setRemoveOnHide(true);
    }
}
