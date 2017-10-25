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

package org.guvnor.ala.ui.client.navigation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.guvnor.ala.ui.client.events.AddNewProviderTypeEvent;
import org.guvnor.ala.ui.client.events.ProviderTypeListRefreshEvent;
import org.guvnor.ala.ui.client.events.ProviderTypeSelectedEvent;
import org.guvnor.ala.ui.model.ProviderType;
import org.guvnor.ala.ui.model.ProviderTypeKey;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

import static org.guvnor.ala.ui.client.util.UIUtil.getDisplayableProviderTypeName;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@ApplicationScoped
public class ProviderTypeNavigationPresenter {

    public interface View extends UberElement<ProviderTypeNavigationPresenter> {

        void addProviderType(final ProviderTypeKey providerTypeKey,
                             final String name,
                             final Command select);

        void select(final ProviderTypeKey providerTypeKey);

        void clear();
    }

    private final View view;

    private final Event<AddNewProviderTypeEvent> addNewProviderTypeEvent;
    private final Event<ProviderTypeListRefreshEvent> providerTypeListRefreshEvent;
    private final Event<ProviderTypeSelectedEvent> providerTypeSelectedEvent;

    private Map<ProviderTypeKey, ProviderType> providerTypes = new HashMap<>();

    @Inject
    public ProviderTypeNavigationPresenter(final View view,
                                           final Event<AddNewProviderTypeEvent> addNewProviderTypeEvent,
                                           final Event<ProviderTypeListRefreshEvent> providerTypeListRefreshEvent,
                                           final Event<ProviderTypeSelectedEvent> providerTypeSelectedEvent) {
        this.view = view;
        this.addNewProviderTypeEvent = addNewProviderTypeEvent;
        this.providerTypeListRefreshEvent = providerTypeListRefreshEvent;
        this.providerTypeSelectedEvent = providerTypeSelectedEvent;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public void setup(final ProviderType firstProvider,
                      final Collection<ProviderType> providerTypes) {
        view.clear();
        this.providerTypes.clear();
        addProviderType(checkNotNull("firstProvider",
                                     firstProvider));
        providerTypes.stream()
                .filter(providerType -> !providerType.equals(firstProvider))
                .forEach(this::addProviderType);
    }

    public View getView() {
        return view;
    }

    private void addProviderType(final ProviderType providerType) {
        checkNotNull("providerType",
                     providerType);
        providerTypes.put(providerType.getKey(),
                          providerType);
        view.addProviderType(providerType.getKey(),
                             getDisplayableProviderTypeName(providerType),
                             () -> select(providerType));
    }

    protected void onSelect(@Observes final ProviderTypeSelectedEvent event) {
        if (event.getProviderTypeKey() != null &&
                providerTypes.containsKey(event.getProviderTypeKey())) {
            view.select(event.getProviderTypeKey());
        }
    }

    private void select(final ProviderType providerType) {
        providerTypeSelectedEvent.fire(new ProviderTypeSelectedEvent(providerType.getKey()));
    }

    public void clear() {
        view.clear();
    }

    public void onRefresh() {
        providerTypeListRefreshEvent.fire(new ProviderTypeListRefreshEvent());
    }

    public void onAddProviderType() {
        addNewProviderTypeEvent.fire(new AddNewProviderTypeEvent());
    }
}
