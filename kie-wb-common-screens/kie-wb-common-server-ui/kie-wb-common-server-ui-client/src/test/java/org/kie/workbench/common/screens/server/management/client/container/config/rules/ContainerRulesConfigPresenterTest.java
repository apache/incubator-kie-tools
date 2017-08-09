/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.server.management.client.container.config.rules;

import java.util.concurrent.TimeUnit;
import javax.enterprise.event.Event;

import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.events.RuleConfigUpdated;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.RuleConfig;
import org.kie.workbench.common.screens.server.management.client.util.State;
import org.kie.workbench.common.screens.server.management.service.RuleCapabilitiesService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContainerRulesConfigPresenterTest {

    private static final String SUCCESS_UPGRADE = "SUCCESS UPGRADE";

    Caller<RuleCapabilitiesService> ruleCapabilitiesServiceCaller;

    @Mock
    Logger logger;

    @Mock
    RuleCapabilitiesService ruleCapabilitiesService;

    @Spy
    Event<NotificationEvent> notification = new EventSourceMock<NotificationEvent>();

    @Mock
    ContainerSpec containerSpec;

    ReleaseId releaseId;

    @Mock
    RuleConfig ruleConfig;

    @Mock
    ContainerRulesConfigPresenter.View view;

    ContainerRulesConfigPresenter presenter;

    @Before
    public void init() {
        releaseId = new ReleaseId();
        releaseId.setVersion("0.1");
        doNothing().when(notification).fire(any(NotificationEvent.class));
        ruleCapabilitiesServiceCaller = new CallerMock<RuleCapabilitiesService>(ruleCapabilitiesService);
        when(containerSpec.getReleasedId()).thenReturn(releaseId);
        when(view.getUpgradeSuccessMessage()).thenReturn(SUCCESS_UPGRADE);
        presenter = new ContainerRulesConfigPresenter(logger,
                                                      view,
                                                      ruleCapabilitiesServiceCaller,
                                                      notification);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                releaseId.setVersion(invocation.getArgumentAt(1,
                                                              ReleaseId.class).getVersion());
                return null;
            }
        }).when(ruleCapabilitiesService).upgradeContainer(any(ContainerSpecKey.class),
                                                          any(ReleaseId.class));
    }

    @Test
    public void testInit() {
        presenter.init();

        verify(view).init(presenter);
        assertEquals(view,
                     presenter.getView());
    }

    @Test
    public void testSetup() {
        when(ruleConfig.getScannerStatus()).thenReturn(KieScannerStatus.STOPPED);
        when(ruleConfig.getPollInterval()).thenReturn(null);
        releaseId.setVersion("1.x");
        presenter.setup(containerSpec,
                        ruleConfig);

        verify(view).setContent(eq(""),
                                eq("1.x"),
                                eq(State.ENABLED),
                                eq(State.DISABLED),
                                eq(State.ENABLED),
                                eq(State.ENABLED));
    }

    @Test
    public void testVersion() {
        final String version = "1.0";
        presenter.setVersion(version);

        verify(view).setVersion(version);
    }

    @Test
    public void testUpgrade() {
        final String version = "1.0";
        presenter.setup(containerSpec,
                        ruleConfig);

        presenter.upgrade(version);

        verify(view).disableActions();
        final ArgumentCaptor<ReleaseId> releaseIdCaptor = ArgumentCaptor.forClass(ReleaseId.class);
        verify(ruleCapabilitiesService).upgradeContainer(eq(containerSpec),
                                                         releaseIdCaptor.capture());
        assertEquals(version,
                     releaseIdCaptor.getValue().getVersion());
        verify(view).setStartScannerState(State.ENABLED);
        verify(view).setStopScannerState(State.DISABLED);
        verify(view).setScanNowState(State.ENABLED);
        verify(view).setUpgradeState(State.ENABLED);
        verify(notification).fire(new NotificationEvent(SUCCESS_UPGRADE,
                                                        NotificationEvent.NotificationType.SUCCESS));
    }

    @Test
    public void testUpgradeError() {
        doThrow(new RuntimeException()).when(ruleCapabilitiesService).upgradeContainer(eq(containerSpec),
                                                                                       any(ReleaseId.class));
        when(view.getUpgradeErrorMessage()).thenReturn("ERROR");

        presenter.setup(containerSpec,
                        ruleConfig);

        presenter.upgrade("LATEST");

        verify(notification).fire(new NotificationEvent("ERROR",
                                                        NotificationEvent.NotificationType.ERROR));
        verify(notification,
               never()).fire(new NotificationEvent(SUCCESS_UPGRADE,
                                                   NotificationEvent.NotificationType.SUCCESS));

        verify(view).setStartScannerState(State.ENABLED);
        verify(view).setStopScannerState(State.DISABLED);
        verify(view).setScanNowState(State.ENABLED);
        verify(view).setUpgradeState(State.ENABLED);
    }

    @Test
    public void testStopScanner() {
        presenter.stopScanner();

        verify(view).disableActions();
        verify(view).setStartScannerState(State.ENABLED);
        verify(view).setStopScannerState(State.DISABLED);
        verify(view).setScanNowState(State.ENABLED);
        verify(view).setUpgradeState(State.ENABLED);
    }

    @Test
    public void testStopScannerError() {
        doThrow(new RuntimeException()).when(ruleCapabilitiesService).stopScanner(eq(containerSpec));
        when(view.getStopScannerErrorMessage()).thenReturn("ERROR");

        presenter.setup(containerSpec,
                        ruleConfig);
        presenter.stopScanner();

        verify(notification).fire(new NotificationEvent("ERROR",
                                                        NotificationEvent.NotificationType.ERROR));

        verify(view).setStartScannerState(State.ENABLED);
        verify(view).setStopScannerState(State.DISABLED);
        verify(view).setScanNowState(State.ENABLED);
        verify(view).setUpgradeState(State.ENABLED);
    }

    @Test
    public void testScanNow() {
        presenter.scanNow();

        verify(view).disableActions();
        verify(view).setStartScannerState(State.ENABLED);
        verify(view).setStopScannerState(State.DISABLED);
        verify(view).setScanNowState(State.ENABLED);
        verify(view).setUpgradeState(State.ENABLED);
    }

    @Test
    public void testScanNowError() {
        doThrow(new RuntimeException()).when(ruleCapabilitiesService).scanNow(eq(containerSpec));
        when(view.getScanNowErrorMessage()).thenReturn("ERROR");

        presenter.setup(containerSpec,
                        ruleConfig);
        presenter.scanNow();

        verify(notification).fire(new NotificationEvent("ERROR",
                                                        NotificationEvent.NotificationType.ERROR));

        verify(view).setStartScannerState(State.ENABLED);
        verify(view).setStopScannerState(State.DISABLED);
        verify(view).setScanNowState(State.ENABLED);
        verify(view).setUpgradeState(State.ENABLED);
    }

    @Test
    public void testStartScannerEmpty() {
        presenter.startScanner("",
                               ContainerRulesConfigPresenter.MS);

        verify(view).errorOnInterval();
    }

    @Test
    public void testStartScanner() {
        presenter.setup(containerSpec,
                        ruleConfig);
        final String interval = "1";
        presenter.startScanner(interval,
                               ContainerRulesConfigPresenter.MS);

        verify(view).disableActions();

        verify(ruleCapabilitiesService).startScanner(eq(containerSpec),
                                                     eq(Long.valueOf(interval)));

        verify(view).setStartScannerState(State.DISABLED);
        verify(view).setStopScannerState(State.ENABLED);
        verify(view).setScanNowState(State.DISABLED);
        verify(view).setUpgradeState(State.DISABLED);
    }

    @Test
    public void testStartScannerError() {
        doThrow(new RuntimeException()).when(ruleCapabilitiesService).startScanner(eq(containerSpec),
                                                                                   anyLong());
        when(view.getStartScannerErrorMessage()).thenReturn("ERROR");

        presenter.setup(containerSpec,
                        ruleConfig);
        presenter.startScanner("1",
                               ContainerRulesConfigPresenter.MS);

        verify(notification).fire(new NotificationEvent("ERROR",
                                                        NotificationEvent.NotificationType.ERROR));

        verify(view).setStartScannerState(State.ENABLED);
        verify(view).setStopScannerState(State.DISABLED);
        verify(view).setScanNowState(State.ENABLED);
        verify(view).setUpgradeState(State.ENABLED);
    }

    @Test
    public void testOnConfigUpdateNoUpdate() {
        final RuleConfigUpdated ruleConfigUpdated = new RuleConfigUpdated();
        ruleConfigUpdated.setContainerSpecKey(new ContainerSpecKey());
        ruleConfigUpdated.setRuleConfig(new RuleConfig());

        presenter.setup(containerSpec,
                        ruleConfig);
        presenter.onConfigUpdate(ruleConfigUpdated);

        verify(view).setContent(anyString(),
                                anyString(),
                                any(State.class),
                                any(State.class),
                                any(State.class),
                                any(State.class));
    }

    @Test
    public void testOnConfigUpdate() {
        final RuleConfigUpdated ruleConfigUpdated = new RuleConfigUpdated();
        ruleConfigUpdated.setContainerSpecKey(containerSpec);
        ruleConfigUpdated.setRuleConfig(ruleConfig);

        presenter.setup(containerSpec,
                        ruleConfig);
        presenter.onConfigUpdate(ruleConfigUpdated);

        verify(view,
               times(2)).setContent(anyString(),
                                    anyString(),
                                    any(State.class),
                                    any(State.class),
                                    any(State.class),
                                    any(State.class));
    }

    @Test
    public void testOnRuleConfigUpdate() {
        final RuleConfigUpdated ruleConfigUpdated = new RuleConfigUpdated();
        ruleConfigUpdated.setRuleConfig(ruleConfig);
        ruleConfigUpdated.setReleasedId(releaseId);
        final Long poolInterval = 1l;
        when(ruleConfig.getPollInterval()).thenReturn(poolInterval);

        presenter.onRuleConfigUpdate(ruleConfigUpdated);

        verify(view).setContent(eq(String.valueOf(poolInterval)),
                                anyString(),
                                any(State.class),
                                any(State.class),
                                any(State.class),
                                any(State.class));
    }

    @Test
    public void testStartScannerInSeconds() {
        presenter.setup(containerSpec,
                        ruleConfig);
        final String interval = "1";
        long expected = TimeUnit.MILLISECONDS.convert(Long.valueOf(interval),
                                                      TimeUnit.SECONDS);
        presenter.startScanner(interval,
                               ContainerRulesConfigPresenter.S);

        verify(view).disableActions();

        verify(ruleCapabilitiesService).startScanner(eq(containerSpec),
                                                     eq(expected));

        verify(view).setStartScannerState(State.DISABLED);
        verify(view).setStopScannerState(State.ENABLED);
        verify(view).setScanNowState(State.DISABLED);
        verify(view).setUpgradeState(State.DISABLED);
    }

    @Test
    public void testStartScannerInMinutes() {
        presenter.setup(containerSpec,
                        ruleConfig);
        final String interval = "1";
        long expected = TimeUnit.MILLISECONDS.convert(Long.valueOf(interval),
                                                      TimeUnit.MINUTES);
        presenter.startScanner(interval,
                               ContainerRulesConfigPresenter.M);

        verify(view).disableActions();

        verify(ruleCapabilitiesService).startScanner(eq(containerSpec),
                                                     eq(expected));

        verify(view).setStartScannerState(State.DISABLED);
        verify(view).setStopScannerState(State.ENABLED);
        verify(view).setScanNowState(State.DISABLED);
        verify(view).setUpgradeState(State.DISABLED);
    }

    @Test
    public void testStartScannerInHours() {
        presenter.setup(containerSpec,
                        ruleConfig);
        final String interval = "1";
        long expected = TimeUnit.MILLISECONDS.convert(Long.valueOf(interval),
                                                      TimeUnit.HOURS);
        presenter.startScanner(interval,
                               ContainerRulesConfigPresenter.H);

        verify(view).disableActions();

        verify(ruleCapabilitiesService).startScanner(eq(containerSpec),
                                                     eq(expected));

        verify(view).setStartScannerState(State.DISABLED);
        verify(view).setStopScannerState(State.ENABLED);
        verify(view).setScanNowState(State.DISABLED);
        verify(view).setUpgradeState(State.DISABLED);
    }

    @Test
    public void testStartScannerInDays() {
        presenter.setup(containerSpec,
                        ruleConfig);
        final String interval = "1";
        long expected = TimeUnit.MILLISECONDS.convert(Long.valueOf(interval),
                                                      TimeUnit.DAYS);
        presenter.startScanner(interval,
                               ContainerRulesConfigPresenter.D);

        verify(view).disableActions();

        verify(ruleCapabilitiesService).startScanner(eq(containerSpec),
                                                     eq(expected));

        verify(view).setStartScannerState(State.DISABLED);
        verify(view).setStopScannerState(State.ENABLED);
        verify(view).setScanNowState(State.DISABLED);
        verify(view).setUpgradeState(State.DISABLED);
    }

    @Test
    public void testStartScannerEmptyTimeUnit() {
        presenter.setup(containerSpec,
                        ruleConfig);
        final String interval = "1";
        long expected = Long.valueOf(interval);
        presenter.startScanner(interval,
                               null);

        verify(view).disableActions();

        verify(ruleCapabilitiesService).startScanner(eq(containerSpec),
                                                     eq(expected));

        verify(view).setStartScannerState(State.DISABLED);
        verify(view).setStopScannerState(State.ENABLED);
        verify(view).setScanNowState(State.DISABLED);
        verify(view).setUpgradeState(State.DISABLED);
    }

    @Test
    public void testSetupScannerIntervalMillis() {
        when(view.getIntervalTimeUnit()).thenReturn(ContainerRulesConfigPresenter.MS);
        when(ruleConfig.getScannerStatus()).thenReturn(KieScannerStatus.STARTED);
        when(ruleConfig.getPollInterval()).thenReturn(1L);
        releaseId.setVersion("1.x");
        presenter.setup(containerSpec,
                        ruleConfig);

        verify(view).setContent(eq("1"),
                                eq("1.x"),
                                eq(State.DISABLED),
                                eq(State.ENABLED),
                                eq(State.DISABLED),
                                eq(State.DISABLED));
    }

    @Test
    public void testSetupScannerIntervalSeconds() {
        when(view.getIntervalTimeUnit()).thenReturn(ContainerRulesConfigPresenter.S);
        when(ruleConfig.getScannerStatus()).thenReturn(KieScannerStatus.STARTED);
        when(ruleConfig.getPollInterval()).thenReturn(TimeUnit.MILLISECONDS.convert(Long.valueOf(1),
                                                                                    TimeUnit.SECONDS));
        releaseId.setVersion("1.x");
        presenter.setup(containerSpec,
                        ruleConfig);

        verify(view).setContent(eq("1"),
                                eq("1.x"),
                                eq(State.DISABLED),
                                eq(State.ENABLED),
                                eq(State.DISABLED),
                                eq(State.DISABLED));
    }

    @Test
    public void testSetupScannerIntervalMinutes() {
        when(view.getIntervalTimeUnit()).thenReturn(ContainerRulesConfigPresenter.M);
        when(ruleConfig.getScannerStatus()).thenReturn(KieScannerStatus.STARTED);
        when(ruleConfig.getPollInterval()).thenReturn(TimeUnit.MILLISECONDS.convert(Long.valueOf(1),
                                                                                    TimeUnit.MINUTES));
        releaseId.setVersion("1.x");
        presenter.setup(containerSpec,
                        ruleConfig);

        verify(view).setContent(eq("1"),
                                eq("1.x"),
                                eq(State.DISABLED),
                                eq(State.ENABLED),
                                eq(State.DISABLED),
                                eq(State.DISABLED));
    }

    @Test
    public void testSetupScannerIntervalHours() {
        when(view.getIntervalTimeUnit()).thenReturn(ContainerRulesConfigPresenter.H);
        when(ruleConfig.getScannerStatus()).thenReturn(KieScannerStatus.STARTED);
        when(ruleConfig.getPollInterval()).thenReturn(TimeUnit.MILLISECONDS.convert(Long.valueOf(1),
                                                                                    TimeUnit.HOURS));
        releaseId.setVersion("1.x");
        presenter.setup(containerSpec,
                        ruleConfig);

        verify(view).setContent(eq("1"),
                                eq("1.x"),
                                eq(State.DISABLED),
                                eq(State.ENABLED),
                                eq(State.DISABLED),
                                eq(State.DISABLED));
    }

    @Test
    public void testSetupScannerIntervalDays() {
        when(view.getIntervalTimeUnit()).thenReturn(ContainerRulesConfigPresenter.D);
        when(ruleConfig.getScannerStatus()).thenReturn(KieScannerStatus.STARTED);
        when(ruleConfig.getPollInterval()).thenReturn(TimeUnit.MILLISECONDS.convert(Long.valueOf(1),
                                                                                    TimeUnit.DAYS));
        releaseId.setVersion("1.x");
        presenter.setup(containerSpec,
                        ruleConfig);

        verify(view).setContent(eq("1"),
                                eq("1.x"),
                                eq(State.DISABLED),
                                eq(State.ENABLED),
                                eq(State.DISABLED),
                                eq(State.DISABLED));
    }
}