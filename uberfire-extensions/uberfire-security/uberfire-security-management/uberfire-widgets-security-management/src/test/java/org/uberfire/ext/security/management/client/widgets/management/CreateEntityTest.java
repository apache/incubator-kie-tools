/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client.widgets.management;

import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateEntityTest extends AbstractSecurityManagementTest {

    @Mock CreateEntity.View view;
    private CreateEntity presenter;

    @Before
    public void setup() {
        super.setup();
        presenter = new CreateEntity(userSystemManager, view);
        assertEquals(view.asWidget(), presenter.asWidget());
    }

    @Test
    public void testClear() throws Exception {
        presenter.clear();
        verify(view, times(1)).clear();
        verify(view, times(1)).setValidationState(ValidationState.NONE);
        verify(view, times(0)).show(anyString(), anyString());
        assertNull(presenter.identifier);
    }

    @Test
    public void testSetErrorState() throws Exception {
        presenter.setErrorState();
        verify(view, times(1)).setValidationState(ValidationState.ERROR);
        verify(view, times(0)).clear();
        verify(view, times(0)).show(anyString(), anyString());
    }

    @Test
    public void testShow() throws Exception {
        final String legend = "legend";
        final String ph = "placeHolder";
        presenter.show(legend, ph);
        verify(view, times(1)).clear();
        verify(view, times(1)).setValidationState(ValidationState.NONE);
        verify(view, times(1)).show(legend, ph);
        assertNull(presenter.identifier);
    }

    @Test
    public void testGetEntityIdentifierValid() throws Exception {
        presenter.identifier = "id1";
        String id = presenter.getEntityIdentifier();
        verify(view, times(1)).setValidationState(ValidationState.NONE);
        verify(view, times(0)).clear();
        verify(view, times(0)).show(anyString(), anyString());
        Assert.assertEquals("id1", id);
    }

    @Test
    public void testGetEntityIdentifierInvalid() throws Exception {
        presenter.identifier = null;
        String id = presenter.getEntityIdentifier();
        verify(view, times(1)).setValidationState(ValidationState.ERROR);
        verify(view, times(0)).clear();
        verify(view, times(0)).show(anyString(), anyString());
        Assert.assertEquals(null, id);
    }
}
