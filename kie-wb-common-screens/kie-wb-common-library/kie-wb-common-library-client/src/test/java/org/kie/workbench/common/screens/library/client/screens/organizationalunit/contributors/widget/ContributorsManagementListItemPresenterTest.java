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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.widget;

import org.ext.uberfire.social.activities.model.SocialUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContributorsManagementListItemPresenterTest {

    @Mock
    private ContributorsManagementListItemPresenter.View view;

    private ContributorsManagementListItemPresenter presenter;

    @Before
    public void setup() {
        presenter = new ContributorsManagementListItemPresenter(view);
    }

    @Test
    public void setupUserWithNameTest() {
        presenter.setup(new SocialUser("user",
                                       "John Admin",
                                       null,
                                       null,
                                       null));

        verify(view).init(presenter);
        verify(view).setUserName("John Admin");
        verify(view,
               never()).setSelected(anyBoolean());
    }

    @Test
    public void setupSelectedUserWithoutNameTest() {
        presenter.setup(new SocialUser("user"),
                        true);

        verify(view).init(presenter);
        verify(view).setUserName("user");
        verify(view).setSelected(true);
    }
}
