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
package org.kie.workbench.common.widgets.metadata.client.widget;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExternalLinkPresenterTest {

    @Mock
    private ExternalLinkView view;

    private ExternalLinkPresenter presenter;

    @Before
    public void setUp() throws
            Exception {
        presenter = new ExternalLinkPresenter(view);
    }

    @Test
    public void presenterSet() throws
            Exception {
        verify(view).init(presenter);
    }

    @Test
    public void setLink() throws
            Exception {
        presenter.setLink("http://www.drools.org");

        verify(view).setLinkModeVisibility(true);
        verify(view).setEditModeVisibility(false);

        verify(view).setLink("http://www.drools.org");
        verify(view).setText("http://www.drools.org");
    }

    @Test
    public void setEmptyShowsEditModeAutomatically() throws
            Exception {
        presenter.setLink("");

        verify(view).setLinkModeVisibility(false);
        verify(view).setEditModeVisibility(true);

        verify(view).setText("");
    }

    @Test
    public void setNullShowsEditModeAutomaticallyWithEmptyText() throws
            Exception {
        presenter.setLink(null);

        verify(view).setLinkModeVisibility(false);
        verify(view).setEditModeVisibility(true);

        verify(view).setText("");
    }

    @Test(expected = IllegalStateException.class)
    public void editNoCallbackSet() throws
            Exception {
        presenter.onTextChange("hello");
    }

    @Test
    public void edit() throws
            Exception {
        final Callback callback = mock(Callback.class);
        presenter.addChangeCallback(callback);
        presenter.onTextChange("hello");

        verify(callback).callback("hello");
    }

    @Test
    public void editDone() throws Exception {
        when(view.getTextBoxText()).thenReturn("hi");

        presenter.onTextChangeDone();

        verify(view).setLink("hi");
        verify(view).setLinkModeVisibility(true);
        verify(view).setEditModeVisibility(false);

    }
}