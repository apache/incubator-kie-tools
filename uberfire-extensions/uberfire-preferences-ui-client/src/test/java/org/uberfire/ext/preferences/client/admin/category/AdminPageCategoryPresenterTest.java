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

package org.uberfire.ext.preferences.client.admin.category;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.ext.preferences.client.admin.item.AdminPageItemPresenter;
import org.uberfire.ext.preferences.client.admin.page.AdminTool;

import static org.mockito.Mockito.*;

public class AdminPageCategoryPresenterTest {

    private ManagedInstance<AdminPageItemPresenter> adminPageItemPresenterProvider;

    private AdminPageCategoryPresenter.View view;

    private AdminPageCategoryPresenter presenter;

    @Before
    public void setup() {
        view = mock(AdminPageCategoryPresenter.View.class);
        adminPageItemPresenterProvider = mock(ManagedInstance.class);
        doReturn(mock(AdminPageItemPresenter.class)).when(adminPageItemPresenterProvider).get();

        presenter = new AdminPageCategoryPresenter(view,
                                                   adminPageItemPresenterProvider);
    }

    @Test
    public void setupTest() {
        List<AdminTool> adminTools = new ArrayList<>();
        adminTools.add(mock(AdminTool.class));
        adminTools.add(mock(AdminTool.class));

        presenter.setup(adminTools,
                        "screen",
                        null);

        verify(adminPageItemPresenterProvider,
               times(2)).get();
        verify(view,
               times(2)).add(any());
    }
}
