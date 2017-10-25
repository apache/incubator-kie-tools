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

package org.guvnor.ala.ui.client.wizard.providertype.item;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.handler.ClientProviderHandler;
import org.guvnor.ala.ui.client.handler.ClientProviderHandlerRegistry;
import org.guvnor.ala.ui.client.util.ContentChangeHandler;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.guvnor.ala.ui.model.ProviderTypeStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.IMAGE_URL;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.PROVIDER_ID;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.PROVIDER_NAME;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.PROVIDER_VERSION;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ProviderTypeItemPresenterTest {

    @Mock
    private ProviderTypeItemPresenter.View view;

    @Mock
    private ClientProviderHandlerRegistry handlerRegistry;

    @Mock
    private ClientProviderHandler providerHandler;

    private ProviderTypeItemPresenter presenter;

    private ProviderType providerType;

    @Before
    public void setUp() {
        providerType = new ProviderType(new ProviderTypeKey(PROVIDER_ID,
                                                            PROVIDER_VERSION),
                                        PROVIDER_NAME);

        when(handlerRegistry.getProviderHandler(providerType.getKey())).thenReturn(providerHandler);
        when(providerHandler.getProviderTypeImageURL()).thenReturn(IMAGE_URL);

        presenter = new ProviderTypeItemPresenter(view,
                                                  handlerRegistry);
        presenter.init();
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testSetupEnabled() {
        presenter.setup(providerType,
                        ProviderTypeStatus.ENABLED);
        verify(view,
               times(1)).disable();
        verifyCommons();
    }

    @Test
    public void testSetupDisabled() {
        presenter.setup(providerType,
                        ProviderTypeStatus.DISABLED);
        verify(view,
               never()).disable();
        verifyCommons();
    }

    @Test
    public void testGetProviderType() {
        presenter.setup(providerType,
                        mock(ProviderTypeStatus.class));
        assertEquals(providerType,
                     presenter.getProviderType());
    }

    @Test
    public void testIsSelected() {
        when(view.isSelected()).thenReturn(true);
        assertTrue(presenter.isSelected());
        verify(view,
               times(1)).isSelected();
    }

    @Test
    public void testNotIsSelected() {
        when(view.isSelected()).thenReturn(false);
        assertFalse(presenter.isSelected());
        verify(view,
               times(1)).isSelected();
    }

    @Test
    public void testContentChange() {
        ContentChangeHandler changeHandler = mock(ContentChangeHandler.class);
        presenter.addContentChangeHandler(changeHandler);
        presenter.fireChangeHandlers();
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnItemClickWhenSelected() {
        ContentChangeHandler changeHandler = mock(ContentChangeHandler.class);
        presenter.addContentChangeHandler(changeHandler);
        when(view.isSelected()).thenReturn(true);
        presenter.onItemClick();
        verify(view,
               times(1)).setSelected(false);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    @Test
    public void testOnItemClickWhenNotSelected() {
        ContentChangeHandler changeHandler = mock(ContentChangeHandler.class);
        presenter.addContentChangeHandler(changeHandler);
        when(view.isSelected()).thenReturn(false);
        presenter.onItemClick();
        verify(view,
               times(1)).setSelected(true);
        verify(changeHandler,
               times(1)).onContentChange();
    }

    private void verifyCommons() {
        verify(view,
               times(1)).setProviderTypeName(PROVIDER_NAME + " " + PROVIDER_VERSION);
        verify(view,
               times(1)).setImage(IMAGE_URL);

        verify(handlerRegistry,
               times(1)).getProviderHandler(providerType.getKey());
        verify(providerHandler,
               times(1)).getProviderTypeImageURL();
    }
}
