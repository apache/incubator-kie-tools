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

package org.guvnor.ala.ui.client.wizard.providertype;

import java.util.Collection;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.ala.ui.client.util.ContentChangeHandler;
import org.guvnor.ala.ui.client.wizard.providertype.item.ProviderTypeItemPresenter;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeStatus;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.buildProviderTypeStatusList;
import static org.guvnor.ala.ui.ProvisioningManagementTestCommons.mockProviderTypeList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class EnableProviderTypePagePresenterTest {

    @Mock
    private EnableProviderTypePagePresenter.View view;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> wizardPageStatusChangeEvent;

    @Mock
    private ManagedInstance<ProviderTypeItemPresenter> providerTypeItemPresenterInstance;

    private EnableProviderTypePagePresenter presenter;

    private List<ProviderType> providerTypes;

    private List<Pair<ProviderType, ProviderTypeStatus>> providerTypeStatus;

    private static int PROVIDERS_COUNT = 3;

    @Before
    public void setUp() {

        //mock an arbitrary set of provider types.
        providerTypes = mockProviderTypeList(PROVIDERS_COUNT);
        providerTypeStatus = buildProviderTypeStatusList(providerTypes,
                                                         ProviderTypeStatus.DISABLED);
        presenter = new EnableProviderTypePagePresenter(view,
                                                        wizardPageStatusChangeEvent,
                                                        providerTypeItemPresenterInstance) {
            @Override
            protected ProviderTypeItemPresenter newProviderTypeItemPresenter() {
                ProviderTypeItemPresenter itemPresenter = mock(ProviderTypeItemPresenter.class);
                when(itemPresenter.getView()).thenReturn(mock(IsElement.class));
                when(providerTypeItemPresenterInstance.get()).thenReturn(itemPresenter);
                return super.newProviderTypeItemPresenter();
            }
        };
        presenter.init();
        verify(view,
               times(1)).init(presenter);
    }

    @Test
    public void testSetup() {
        presenter.setup(providerTypeStatus);

        verify(view,
               times(1)).clear();
        assertEquals(providerTypeStatus.size(),
                     presenter.getItemPresenters().size());
        for (int i = 0; i < presenter.getItemPresenters().size(); i++) {
            ProviderTypeItemPresenter itemPresenter = presenter.getItemPresenters().get(i);
            Pair<ProviderType, ProviderTypeStatus> pair = providerTypeStatus.get(i);
            verify(itemPresenter,
                   times(1)).setup(pair.getK1(),
                                   pair.getK2());
            verify(itemPresenter,
                   times(1)).addContentChangeHandler(any(ContentChangeHandler.class));
            verify(view,
                   times(1)).addProviderType(itemPresenter.getView());
        }
        verify(providerTypeItemPresenterInstance,
               times(providerTypeStatus.size())).get();
    }

    @Test
    public void testPageNotCompleted() {
        presenter.setup(providerTypeStatus);
        presenter.getItemPresenters().forEach(itemPresenter -> when(itemPresenter.isSelected()).thenReturn(false));
        //no item is selected
        presenter.isComplete(Assert::assertFalse);
    }

    @Test
    public void testPageCompleted() {
        presenter.setup(providerTypeStatus);
        presenter.getItemPresenters().forEach(itemPresenter -> when(itemPresenter.isSelected()).thenReturn(false));
        //select some items.
        int selected1 = 0;
        int selected2 = 2;
        when(presenter.getItemPresenters().get(selected1).isSelected()).thenReturn(true);
        when(presenter.getItemPresenters().get(selected1).getProviderType()).thenReturn(providerTypes.get(selected1));

        when(presenter.getItemPresenters().get(selected2).isSelected()).thenReturn(true);
        when(presenter.getItemPresenters().get(selected2).getProviderType()).thenReturn(providerTypes.get(selected2));

        //there are selected items, the page must be completed.
        presenter.isComplete(Assert::assertTrue);

        Collection<ProviderType> selectedItems = presenter.getSelectedProviderTypes();
        assertEquals(providerTypes.get(selected1),
                     selectedItems.iterator().next());
        assertEquals(providerTypes.get(selected1),
                     selectedItems.iterator().next());
    }
}
