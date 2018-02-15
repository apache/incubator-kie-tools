/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid.controls.list;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.HasListSelectorControl.ListSelectorItem;

@ApplicationScoped
public class ListSelector implements ListSelectorView.Presenter {

    private ListSelectorView view;
    private Optional<HasListSelectorControl> binding = Optional.empty();

    public ListSelector() {
        //CDI proxy
    }

    @Inject
    public ListSelector(final ListSelectorView view) {
        this.view = view;
        this.view.init(this);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @Override
    public void onItemSelected(final ListSelectorItem item) {
        binding.ifPresent(b -> b.onItemSelected(item));
    }

    @Override
    public void bind(final HasListSelectorControl bound) {
        binding = Optional.ofNullable(bound);
        binding.ifPresent(b -> view.setItems(b.getItems()));
    }

    @Override
    public void show() {
        view.show();
    }

    @Override
    public void hide() {
        view.hide();
    }
}
