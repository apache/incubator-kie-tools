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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContributorsListPresenterTest {

    @Mock
    private ContributorsListPresenter.View view;

    @Mock
    private ManagedInstance<ContributorsListItemPresenter> contributorsListItemPresenters;

    @Mock
    private Elemental2DomUtil elemental2DomUtil;

    @Mock
    private ContributorsListItemPresenter contributorsListItemPresenter;

    @Mock
    private ContributorsListService contributorsListService;

    @Mock
    private Consumer<Integer> contributorsCountChangedCallback;

    private Promises promises;

    private ContributorsListPresenter presenter;

    private List<Contributor> contributors;

    @Before
    public void setup() {
        promises = new SyncPromises();

        doReturn(contributorsListItemPresenter).when(contributorsListItemPresenters).get();
        doReturn(mock(ContributorsListItemPresenter.View.class)).when(contributorsListItemPresenter).getView();

        presenter = spy(new ContributorsListPresenter(view,
                                                      contributorsListItemPresenters,
                                                      elemental2DomUtil,
                                                      promises));

        contributors = new ArrayList<>();
        contributors.add(new Contributor("admin", ContributorType.OWNER));
        contributors.add(new Contributor("user", ContributorType.CONTRIBUTOR));
        contributors.add(new Contributor("Director", ContributorType.ADMIN));
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(0, Consumer.class).accept(contributors);
            return null;
        }).when(contributorsListService).getContributors(any());

        final List<String> userNames = Arrays.asList("admin", "user", "Director");
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(0, Consumer.class).accept(userNames);
            return null;
        }).when(contributorsListService).getValidUsernames(any());
    }

    @Test
    public void setupTest() {
        final InOrder order = inOrder(contributorsListItemPresenter);

        presenter.setup(contributorsListService, contributorsCountChangedCallback);

        verify(view).init(presenter);
        verify(contributorsCountChangedCallback).accept(3);
        verify(view).clearContributors();
        order.verify(contributorsListItemPresenter).setup(contributors.get(0), presenter, contributorsListService);
        order.verify(contributorsListItemPresenter).setup(contributors.get(2), presenter, contributorsListService);
        order.verify(contributorsListItemPresenter).setup(contributors.get(1), presenter, contributorsListService);
        verify(view, times(3)).addContributor(any());
    }

    @Test
    public void filterContributorsTest() {
        final InOrder order = inOrder(contributorsListItemPresenter);

        presenter.contributors = new ArrayList<>();
        presenter.contributors.add(new Contributor("John", ContributorType.OWNER));
        presenter.contributors.add(new Contributor("Mary", ContributorType.ADMIN));
        presenter.contributors.add(new Contributor("Jonathan", ContributorType.CONTRIBUTOR));

        presenter.filterContributors("h");

        verify(view).clearContributors();
        order.verify(contributorsListItemPresenter).setup(presenter.contributors.get(0), presenter, null);
        order.verify(contributorsListItemPresenter).setup(presenter.contributors.get(2), presenter, null);
        verify(view, times(2)).addContributor(any());
    }
}
