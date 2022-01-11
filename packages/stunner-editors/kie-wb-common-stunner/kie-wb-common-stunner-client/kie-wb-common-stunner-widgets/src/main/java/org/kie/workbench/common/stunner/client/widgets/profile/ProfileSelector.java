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

package org.kie.workbench.common.stunner.client.widgets.profile;

import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.client.widgets.presenters.session.RequestSessionRefreshEvent;
import org.kie.workbench.common.stunner.client.widgets.views.Selector;
import org.kie.workbench.common.stunner.client.widgets.views.SelectorDelegate;
import org.kie.workbench.common.stunner.client.widgets.views.SelectorImpl;
import org.kie.workbench.common.stunner.core.api.ProfileManager;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractSession;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.profile.Profile;

@Dependent
public class ProfileSelector extends SelectorDelegate<Profile> {

    private final SelectorImpl<Profile> selector;
    private final ProfileManager profileManager;
    private final Event<RequestSessionRefreshEvent> requestSessionRefreshEvent;

    @Inject
    public ProfileSelector(final SelectorImpl<Profile> selector,
                           final ProfileManager profileManager,
                           final Event<RequestSessionRefreshEvent> requestSessionRefreshEvent) {
        this.selector = selector;
        this.profileManager = profileManager;
        this.requestSessionRefreshEvent = requestSessionRefreshEvent;
    }

    @PostConstruct
    public void init() {
        selector
                .setTextProvider(Profile::getName)
                .setValueProvider(Profile::getProfileId)
                .setItemProvider(this::getProfile);
    }

    public ProfileSelector bind(final Supplier<AbstractSession> sessionSupplier) {
        final AbstractSession session = sessionSupplier.get();
        final Metadata metadata = session.getCanvasHandler().getDiagram().getMetadata();
        final String definitionSetId = metadata.getDefinitionSetId();
        final String profileId = metadata.getProfileId();
        useDefinitionSet(definitionSetId);
        useProfile(definitionSetId, profileId);
        selector.setValueChangedCommand(() -> {
            final Profile item = selector.getSelectedItem();
            metadata.setProfileId(item.getProfileId());
            requestSessionRefreshEvent.fire(new RequestSessionRefreshEvent(session.getSessionUUID()));
        });
        return this;
    }

    private void useDefinitionSet(final String defSetId) {
        selector.clear();
        profileManager
                .getProfiles(defSetId)
                .forEach(this::addItem);
    }

    private void useProfile(final String defSetId,
                            final String profileId) {
        final Profile profile = profileManager.getProfile(defSetId, profileId);
        setSelectedItem(profile);
    }

    @Override
    protected Selector<Profile> getDelegate() {
        return selector;
    }

    private Profile getProfile(final String id) {
        return profileManager.getProfile(id);
    }
}
