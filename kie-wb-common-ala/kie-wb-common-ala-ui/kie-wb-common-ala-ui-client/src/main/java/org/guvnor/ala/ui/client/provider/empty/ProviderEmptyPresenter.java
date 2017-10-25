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

package org.guvnor.ala.ui.client.provider.empty;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.events.AddNewProviderEvent;
import org.guvnor.ala.ui.model.ProviderType;
import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.client.mvp.UberElement;

import static org.guvnor.ala.ui.client.util.UIUtil.getDisplayableProviderTypeName;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

public class ProviderEmptyPresenter {

    public interface View extends UberElement<ProviderEmptyPresenter> {

        void setProviderTypeName(String providerTypeName);
    }

    private final View view;
    private final Event<AddNewProviderEvent> addNewProviderEvent;
    private ProviderType providerType;

    @Inject
    public ProviderEmptyPresenter(final View view,
                                  final Event<AddNewProviderEvent> addNewProviderEvent) {
        this.view = view;
        this.addNewProviderEvent = addNewProviderEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setProviderType(final ProviderType providerType) {
        this.providerType = checkNotNull("providerType",
                                         providerType);
        this.view.setProviderTypeName(getDisplayableProviderTypeName(providerType));
    }

    public IsElement getView() {
        return view;
    }

    public void addProvider() {
        addNewProviderEvent.fire(new AddNewProviderEvent(providerType));
    }
}
