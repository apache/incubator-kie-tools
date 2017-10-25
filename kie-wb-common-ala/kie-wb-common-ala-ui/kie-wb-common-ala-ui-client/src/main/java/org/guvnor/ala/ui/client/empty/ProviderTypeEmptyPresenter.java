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

package org.guvnor.ala.ui.client.empty;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.events.AddNewProviderTypeEvent;
import org.uberfire.client.mvp.UberElement;

@ApplicationScoped
public class ProviderTypeEmptyPresenter {

    public interface View
            extends UberElement<ProviderTypeEmptyPresenter> {

    }

    private final View view;

    private final Event<AddNewProviderTypeEvent> addNewProviderTypeEvent;

    @Inject
    public ProviderTypeEmptyPresenter(final View view,
                                      final Event<AddNewProviderTypeEvent> addNewProviderTypeEvent) {
        this.view = view;
        this.addNewProviderTypeEvent = addNewProviderTypeEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void onAddProviderType() {
        addNewProviderTypeEvent.fire(new AddNewProviderTypeEvent());
    }
}
