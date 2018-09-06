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
package org.kie.workbench.common.stunner.client.widgets.canvas.wires;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.canvas.view.LienzoPanel;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class WiresCanvasPresenterTest {

    @Mock
    private LienzoPanel lienzoPanel;

    @Test
    public void testFocus() {
        final WiresCanvasPresenter canvas = getCanvasTestableForFocus();

        canvas.focus();

        verify(lienzoPanel).focus();
    }

    private WiresCanvasPresenter getCanvasTestableForFocus() {
        return new WiresCanvasPresenter(null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        lienzoPanel);
    }
}
