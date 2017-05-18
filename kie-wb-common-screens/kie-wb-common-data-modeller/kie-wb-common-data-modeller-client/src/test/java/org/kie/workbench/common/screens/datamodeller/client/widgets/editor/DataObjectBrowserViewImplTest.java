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

import java.util.Arrays;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.datamodeller.client.handlers.DomainHandler;
import org.kie.workbench.common.services.datamodeller.core.ObjectProperty;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataObjectBrowserViewImplTest {

    @Test
    public void isRemoveButtonEnabled() {
        DataObjectBrowserViewImpl view = new DataObjectBrowserViewImpl();

        ObjectProperty objectProperty = mock(ObjectProperty.class);

        DomainHandler matchingHandler = mock(DomainHandler.class);
        when(matchingHandler.isDomainSpecificProperty(objectProperty)).thenReturn(true);

        DomainHandler nonMatchingHandler = mock(DomainHandler.class);
        when(nonMatchingHandler.isDomainSpecificProperty(objectProperty)).thenReturn(false);

        view.setDomainHandlers(Arrays.asList(matchingHandler,
                                             nonMatchingHandler));

        assertFalse(view.isRemoveButtonEnabled(objectProperty));

        view.setDomainHandlers(Arrays.asList(matchingHandler));

        assertFalse(view.isRemoveButtonEnabled(objectProperty));

        view.setDomainHandlers(Arrays.asList(nonMatchingHandler));

        assertTrue(view.isRemoveButtonEnabled(objectProperty));
    }
}
