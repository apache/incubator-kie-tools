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

package org.drools.workbench.screens.scenariosimulation.client.producers;

import java.lang.annotation.Annotation;

import javax.enterprise.event.Event;

import com.google.gwt.event.shared.EventBus;
import org.drools.workbench.screens.scenariosimulation.client.commands.CommandExecutor;
import org.drools.workbench.screens.scenariosimulation.client.editor.ScenarioSimulationView;
import org.drools.workbench.screens.scenariosimulation.client.popup.DeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.popup.PreserveDeletePopupPresenter;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGrid;
import org.drools.workbench.screens.scenariosimulation.client.widgets.ScenarioGridPanel;
import org.junit.Before;
import org.mockito.Mock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public abstract class AbstractProducerTest {

    @Mock
    protected EventBusProducer eventBusProducerMock;

    @Mock
    protected ScenarioSimulationViewProducer scenarioSimulationViewProducerMock;

    @Mock
    protected EventBus eventBusMock;

    @Mock
    protected DeletePopupPresenter deletePopupPresenterMock;
    @Mock
    protected PreserveDeletePopupPresenter preserveDeletePopupPresenterMock;

    @Mock
    protected CommandExecutor commandExecutorMock;

    @Mock
    protected ScenarioSimulationView scenarioSimulationViewMock;

    @Mock
    protected ScenarioGridPanel scenarioGridPanelMock;

    @Mock
    protected ScenarioGrid scenarioGridMock;

    protected Event<NotificationEvent> notificationEventNew;

    @Before
    public void setup() {
        notificationEventNew = new Event<NotificationEvent>() {
            @Override
            public void fire(NotificationEvent notificationEvent) {

            }

            @Override
            public Event<NotificationEvent> select(Annotation... annotations) {
                return null;
            }

            @Override
            public <U extends NotificationEvent> Event<U> select(Class<U> aClass, Annotation... annotations) {
                return null;
            }
        };
        when(scenarioGridPanelMock.getScenarioGrid()).thenReturn(scenarioGridMock);
        when(eventBusProducerMock.getEventBus()).thenReturn(eventBusMock);
        when(scenarioSimulationViewMock.getScenarioGridPanel()).thenReturn(scenarioGridPanelMock);
        when(scenarioSimulationViewProducerMock.getScenarioSimulationView(isA(EventBus.class))).thenReturn(scenarioSimulationViewMock);
    }
}