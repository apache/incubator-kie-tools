/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.included.grid;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.client.docks.navigator.events.RefreshDecisionComponents;
import org.kie.workbench.common.dmn.client.editors.included.DefaultIncludedModelActiveRecord;

@Dependent
public class DefaultCardComponent extends BaseCardComponent<DefaultIncludedModelActiveRecord, BaseCardComponent.ContentView> {

    @Inject
    public DefaultCardComponent(final @Default BaseCardComponent.ContentView contentView,
                                final Event<RefreshDecisionComponents> refreshDecisionComponentsEvent) {
        super(contentView,
              refreshDecisionComponentsEvent);
    }

    @PostConstruct
    public void init() {
        contentView.init(this);
    }
}
