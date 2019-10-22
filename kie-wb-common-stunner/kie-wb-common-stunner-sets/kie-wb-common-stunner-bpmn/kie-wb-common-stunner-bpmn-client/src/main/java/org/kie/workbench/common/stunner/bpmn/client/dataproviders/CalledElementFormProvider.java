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
package org.kie.workbench.common.stunner.bpmn.client.dataproviders;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.forms.dynamic.model.config.SelectorData;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.stunner.bpmn.forms.dataproviders.RequestProcessDataEvent;

public class CalledElementFormProvider implements SelectorDataProvider {

    @Inject
    ProcessesDataProvider dataProvider;

    @Inject
    Event<RequestProcessDataEvent> requestProcessDataEvent;

    @Override
    public String getProviderName() {
        return getClass().getSimpleName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SelectorData getSelectorData(final FormRenderingContext context) {
        requestProcessDataEvent.fire(new RequestProcessDataEvent());
        return new SelectorData(toMap(dataProvider.getProcessIds()), null);
    }

    private static Map<Object, String> toMap(final Iterable<String> items) {
        return StreamSupport.stream(items.spliterator(), false).collect(Collectors.toMap(s -> s, s -> s));
    }
}
