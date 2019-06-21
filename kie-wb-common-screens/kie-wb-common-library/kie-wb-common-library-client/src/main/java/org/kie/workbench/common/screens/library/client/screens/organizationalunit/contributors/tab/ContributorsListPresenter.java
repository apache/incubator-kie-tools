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
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.client.promise.Promises;

public class ContributorsListPresenter {

    public interface View extends UberElement<ContributorsListPresenter> {

        void clearContributors();

        void addContributor(HTMLElement contributor);

        void addNewContributor(HTMLElement newContributorView);

        void clearFilterText();

        void showAddContributor();

        void hideAddContributor();
    }

    private View view;

    private ManagedInstance<ContributorsListItemPresenter> contributorsListItemPresenters;

    private Elemental2DomUtil elemental2DomUtil;

    Promises promises;

    ContributorsListService contributorsListService;

    Consumer<Integer> contributorsCountChangedCallback;

    List<Contributor> contributors;

    List<ContributorsListItemPresenter> items = new ArrayList<>();

    List<String> validUsernames;

    @Inject
    public ContributorsListPresenter(final View view,
                                     final ManagedInstance<ContributorsListItemPresenter> contributorsListItemPresenters,
                                     final Elemental2DomUtil elemental2DomUtil,
                                     final Promises promises) {
        this.view = view;
        this.contributorsListItemPresenters = contributorsListItemPresenters;
        this.elemental2DomUtil = elemental2DomUtil;
        this.promises = promises;
    }

    public void setup(final ContributorsListService contributorsListService,
                      final Consumer<Integer> contributorsCountChangedCallback) {
        this.contributorsListService = contributorsListService;
        this.contributorsCountChangedCallback = contributorsCountChangedCallback;

        refresh();
        this.contributorsListService.onExternalChange(this::refresh);
    }

    public void refresh() {
        contributorsListService.getContributors(this::refresh);
    }

    private void refresh(final Collection<Contributor> contributors) {
        this.contributors = new ArrayList<>(contributors);
        this.contributors.sort(Contributor.COMPARATOR);
        view.init(this);

        contributorsListService.getValidUsernames(validUsernames -> {
            this.validUsernames = validUsernames;
            updateContributors();
            contributorsCountChangedCallback.accept(contributors.size());
        });
    }

    public void updateContributors() {
        updateView(contributors);
    }

    public void filterContributors(final String filter) {
        List<Contributor> filteredContributors = contributors.stream()
                .filter(c -> c.getUsername().toUpperCase().contains(filter.toUpperCase()))
                .collect(Collectors.toList());

        updateView(filteredContributors);
    }

    private void updateView(final List<Contributor> contributors) {
        view.clearContributors();
        items = new ArrayList<>();

        contributors.stream()
                .forEach(contributor -> {
                    final ContributorsListItemPresenter contributorsListItemPresenter = contributorsListItemPresenters.get();
                    contributorsListItemPresenter.setup(contributor, ContributorsListPresenter.this, contributorsListService);
                    view.addContributor(elemental2DomUtil.asHTMLElement(contributorsListItemPresenter.getView().getElement()));
                    items.add(contributorsListItemPresenter);
                });
    }

    public void addContributor() {
        canEditContributors(ContributorType.CONTRIBUTOR).then(canEditContributors -> {
            if (canEditContributors) {
                itemIsBeingEdited();
                final ContributorsListItemPresenter newContributorItem = contributorsListItemPresenters.get();
                newContributorItem.setupNew(this, contributorsListService);
                view.addNewContributor(elemental2DomUtil.asHTMLElement(newContributorItem.getView().getElement()));
                items.add(newContributorItem);
            }

            return promises.resolve();
        });
    }

    public void itemIsBeingEdited() {
        items.stream().forEach(i -> i.hideActions());
        view.hideAddContributor();
    }

    public void itemIsNotBeingEdited() {
        items.stream().forEach(i -> i.showActions());
        view.showAddContributor();
    }

    public Promise<Boolean> canEditContributors(final ContributorType type) {
        return contributorsListService.canEditContributors(contributors, type);
    }

    public List<String> getValidUsernames() {
        return validUsernames;
    }

    public View getView() {
        return view;
    }
}
