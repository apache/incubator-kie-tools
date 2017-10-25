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

package org.guvnor.ala.ui.client.provider.status.empty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.events.RefreshRuntimeEvent;
import org.guvnor.ala.ui.model.ProviderKey;
import org.uberfire.client.mvp.UberElement;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class ProviderStatusEmptyPresenter {

    public interface View
            extends UberElement<ProviderStatusEmptyPresenter> {

    }

    private final View view;

    private final Event<RefreshRuntimeEvent> refreshRuntimeEvent;

    private ProviderKey providerKey;

    @Inject
    public ProviderStatusEmptyPresenter(final View view,
                                        final Event<RefreshRuntimeEvent> refreshRuntimeEvent) {
        this.view = view;
        this.refreshRuntimeEvent = refreshRuntimeEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void setup(final ProviderKey providerKey) {
        this.providerKey = checkNotNull("providerKey",
                                        providerKey);
    }

    public void onRefresh() {
        refreshRuntimeEvent.fire(new RefreshRuntimeEvent(providerKey));
    }
}
