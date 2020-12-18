/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.widgets.common.client.breadcrumbs.widget;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DefaultBreadcrumbsPresenterTest {

    @Mock
    private DefaultBreadcrumbsPresenter.View view;

    private DefaultBreadcrumbsPresenter presenter;

    @Before
    public void setup() {
        presenter = new DefaultBreadcrumbsPresenter(view);
    }

    @Test
    public void setupWithActionTest() {
        final Command selectCommand = () -> {};

        presenter.setup("label", true, selectCommand);

        verify(view).setup("label");
        verify(view, never()).setNoAction();
    }

    @Test
    public void setupWithNoActionTest() {
        presenter.setup("label", true, null);

        verify(view).setup("label");
        verify(view).setNoAction();
    }
}
