/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.dtable.client.editor.page;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.callbacks.Callback;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ShowRuleNameOptionPresenterTest {

    @Mock
    ShowRuleNameOptionPresenter.View view;

    ShowRuleNameOptionPresenter presenter;
    @Mock
    Callback<Boolean> callback;

    @Before
    public void setUp() throws Exception {
        presenter = new ShowRuleNameOptionPresenter(view);

        // Check no NPE is thrown
        presenter.onRuleNameCheckboxChanged(true);

        presenter.addOptionChangeCallback(callback);
    }

    @Test
    public void columnShown() {
        presenter.setShowRuleName(true);
        verify(view).setShowRuleName(true);
    }

    @Test
    public void columnHidden() {
        presenter.setShowRuleName(false);
        verify(view).setShowRuleName(false);
    }

    @Test
    public void setVisible() {
        presenter.onRuleNameCheckboxChanged(true);
        verify(callback).callback(true);
    }

    @Test
    public void hide() {
        presenter.onRuleNameCheckboxChanged(false);
        verify(callback).callback(false);
    }
}