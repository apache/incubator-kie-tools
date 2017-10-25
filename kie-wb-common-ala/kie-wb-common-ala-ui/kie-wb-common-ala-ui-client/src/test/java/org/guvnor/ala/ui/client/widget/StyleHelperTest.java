/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.widget;

import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DOMUtil.class)
public class StyleHelperTest {

    @Mock
    HTMLElement form;

    @Test
    public void testSetFormStatusValid() {
        PowerMockito.mockStatic(DOMUtil.class);
        StyleHelper.setFormStatus(form,
                                  FormStatus.VALID);
        verifyStatic();
        DOMUtil.addUniqueEnumStyleName(form,
                                       ValidationState.class,
                                       ValidationState.NONE);
    }

    @Test
    public void testSetFormStatusError() {
        PowerMockito.mockStatic(DOMUtil.class);
        StyleHelper.setFormStatus(form,
                                  FormStatus.ERROR);
        verifyStatic();
        DOMUtil.addUniqueEnumStyleName(form,
                                       ValidationState.class,
                                       ValidationState.ERROR);
    }
}
