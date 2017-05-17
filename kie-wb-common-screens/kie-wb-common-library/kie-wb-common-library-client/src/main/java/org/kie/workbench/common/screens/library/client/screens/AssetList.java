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
package org.kie.workbench.common.screens.library.client.screens;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

public class AssetList
        implements IsElement {

    public interface View
            extends UberElement<AssetList> {

        void setForwardDisabled(final boolean disabled);

        void setBackwardDisabled(final boolean disabled);

        void range(final int from,
                   final int to);

        void clearAssets();

        void hideEmptyState();

        void add(final HTMLElement element);

        void showEmptyStateMessage(final String topic,
                                   final String message);

        void setPageNumber(final int pageNumber);

        void setStep(final int step);
    }

    private static final int DEFAULT_STEP = 15;

    private View view;
    private Command command;

    private List<HTMLElement> elements = new ArrayList<>();
    private int step = DEFAULT_STEP;
    private int pageNumber = 1;

    @Inject
    public AssetList(final View view) {
        this.view = view;
        view.init(this);
        view.setStep(step);
        view.setPageNumber(pageNumber);
    }

    /**
     * Resets the component to default state.
     */
    public void reset() {

        clear();

        step = DEFAULT_STEP;
        view.setStep(step);

        updateToFirstPage();
        view.range(1,
                   step);
    }

    /**
     * Clears the asset list.
     */
    public void clear() {
        elements.clear();
        view.clearAssets();
        view.hideEmptyState();
    }

    public void add(final HTMLElement element) {

        elements.add(element);

        view.add(element);

        view.range(getFirstIndex() + 1,
                   getFirstIndex() + elements.size());
        setupForwardBackwardButtons();
    }

    private void setupForwardBackwardButtons() {
        view.setBackwardDisabled(getFirstIndex() == 0);
        view.setForwardDisabled(isThereRoomOnThisPage());
    }

    private boolean isThereRoomOnThisPage() {
        return step > elements.size();
    }

    public void showEmptyState(final String topic,
                               final String message) {
        view.showEmptyStateMessage(topic,
                                   message);
    }

    public Integer getStep() {
        return step;
    }

    public void onChangeAmountOfItemsShown(final int step) {
        this.step = step;
        updateToFirstPage();
        command.execute();
    }

    private void updateToFirstPage() {
        pageNumber = 1;
        view.setPageNumber(pageNumber);
    }

    public void onToFirstPage() {
        pageNumber = 1;
        view.setPageNumber(pageNumber);
        command.execute();
    }

    public void onToNextPage() {
        if (!isThereRoomOnThisPage()) {
            pageNumber++;
            view.setPageNumber(pageNumber);
            command.execute();
        }
    }

    public void onToPrevious() {
        if (pageNumber > 1) {
            pageNumber--;
            view.setPageNumber(pageNumber);
            command.execute();
        }
    }

    public void onPageNumberChange(final int pageNumber) {

        if (pageNumber > 0) {
            this.pageNumber = pageNumber;
        } else {
            this.pageNumber = 1;
        }

        command.execute();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    public void addChangeHandler(final Command command) {
        this.command = command;
    }

    public int getFirstIndex() {
        final int result = (pageNumber * step) - step;

        if (result < 1) {
            return 0;
        } else {
            return result;
        }
    }
}
