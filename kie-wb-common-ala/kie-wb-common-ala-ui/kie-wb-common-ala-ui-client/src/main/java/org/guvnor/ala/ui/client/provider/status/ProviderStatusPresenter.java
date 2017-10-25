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

package org.guvnor.ala.ui.client.provider.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.provider.status.runtime.RuntimePresenter;
import org.guvnor.ala.ui.model.PipelineExecutionTraceKey;
import org.guvnor.ala.ui.model.RuntimeKey;
import org.guvnor.ala.ui.model.RuntimeListItem;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class ProviderStatusPresenter {

    public interface View
            extends UberElement<ProviderStatusPresenter> {

        void addListItem(final IsElement listItem);

        void removeListItem(final IsElement listItem);

        void clear();
    }

    private final View view;

    private final ManagedInstance<RuntimePresenter> runtimePresenterInstance;

    private final List<RuntimePresenter> currentItems = new ArrayList<>();

    @Inject
    public ProviderStatusPresenter(final View view,
                                   final ManagedInstance<RuntimePresenter> runtimePresenterInstance) {
        this.view = view;
        this.runtimePresenterInstance = runtimePresenterInstance;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setupItems(final Collection<RuntimeListItem> response) {
        clear();
        response.forEach(item -> {
            final RuntimePresenter runtimePresenter = newRuntimePresenter();
            runtimePresenter.setup(item);
            currentItems.add(runtimePresenter);
            view.addListItem(runtimePresenter.getView());
        });
    }

    public boolean removeItem(final RuntimeKey runtimeKey) {
        final Optional<RuntimePresenter> value = currentItems.stream()
                .filter(presenter -> presenter.getItem().isRuntime()
                        && runtimeKey.equals(presenter.getItem().getRuntime().getKey()))
                .findFirst();
        value.ifPresent(this::removeItem);
        return value.isPresent();
    }

    public boolean removeItem(final PipelineExecutionTraceKey pipelineExecutionTraceKey) {
        final Optional<RuntimePresenter> value = currentItems.stream()
                .filter(presenter -> !presenter.getItem().isRuntime() &&
                        presenter.getItem().getPipelineTrace() != null &&
                        pipelineExecutionTraceKey.equals(presenter.getItem().getPipelineTrace().getKey()))
                .findFirst();
        value.ifPresent(this::removeItem);
        return value.isPresent();
    }

    public boolean isEmpty() {
        return currentItems.isEmpty();
    }

    public void clear() {
        view.clear();
        clearItems();
    }

    public View getView() {
        return view;
    }

    private void removeItem(final RuntimePresenter item) {
        view.removeListItem(item.getView());
        currentItems.remove(item);
        runtimePresenterInstance.destroy(item);
    }

    protected RuntimePresenter newRuntimePresenter() {
        return runtimePresenterInstance.get();
    }

    private void clearItems() {
        currentItems.forEach(runtimePresenterInstance::destroy);
        currentItems.clear();
    }
}
