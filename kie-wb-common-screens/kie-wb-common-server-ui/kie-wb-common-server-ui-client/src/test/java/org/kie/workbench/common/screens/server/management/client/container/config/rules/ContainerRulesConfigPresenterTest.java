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
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContainerRulesConfigPresenterTest {

    Caller<RuleCapabilitiesService> ruleCapabilitiesServiceCaller;

    @Mock
    RuleCapabilitiesService ruleCapabilitiesService;

    @Spy
    Event<NotificationEvent> notification = new EventSourceMock<NotificationEvent>();

    @Mock
    ContainerSpec containerSpec;

    @Mock
    ReleaseId releaseId;

    @Mock
    RuleConfig ruleConfig;

    @Mock
    ContainerRulesConfigPresenter.View view;

    ContainerRulesConfigPresenter presenter;

    @Before
    public void init() {
        doNothing().when( notification ).fire( any( NotificationEvent.class ) );
        ruleCapabilitiesServiceCaller = new CallerMock<RuleCapabilitiesService>( ruleCapabilitiesService );
        when( containerSpec.getReleasedId() ).thenReturn( releaseId );
        when( releaseId.getVersion() ).thenReturn( "0.1" );
        presenter = new ContainerRulesConfigPresenter( view, ruleCapabilitiesServiceCaller, notification );
    }

    @Test
    public void testInit() {
        presenter.init();

        verify( view ).init( presenter );
        assertEquals( view, presenter.getView() );
    }

    @Test
    public void testSetup() {
        when( ruleConfig.getScannerStatus() ).thenReturn( KieScannerStatus.STOPPED );
        when( ruleConfig.getPollInterval() ).thenReturn( null );
        when( containerSpec.getReleasedId().getVersion() ).thenReturn( "1.x" );
        presenter.setup( containerSpec, ruleConfig );

        verify( view ).setContent( eq( "" ), eq( "1.x" ), eq( State.ENABLED ), eq( State.DISABLED ), eq( State.ENABLED ), eq( State.ENABLED ) );
    }

    @Test
    public void testVersion() {
        final String version = "1.0";
        presenter.setVersion( version );

        verify( view ).setVersion( version );
    }

    @Test
    public void testUpgrade() {
        final String version = "1.0";
        presenter.setup( containerSpec, ruleConfig );

        presenter.upgrade( version );

        verify( view ).disableActions();
        final ArgumentCaptor<ReleaseId> releaseIdCaptor = ArgumentCaptor.forClass( ReleaseId.class );
        verify( ruleCapabilitiesService ).upgradeContainer( eq( containerSpec ), releaseIdCaptor.capture() );
        assertEquals( version, releaseIdCaptor.getValue().getVersion() );
        verify( view ).setStartScannerState( State.ENABLED );
        verify( view ).setStopScannerState( State.DISABLED );
        verify( view ).setScanNowState( State.ENABLED );
        verify( view ).setUpgradeState( State.ENABLED );
    }

    @Test
    public void testUpgradeError() {
        doThrow( new RuntimeException() ).when( ruleCapabilitiesService ).upgradeContainer( eq( containerSpec ), any(ReleaseId.class) );
        when( view.getUpgradeErrorMessage() ).thenReturn( "ERROR" );

        presenter.setup( containerSpec, ruleConfig );

        presenter.upgrade( "LATEST" );

        verify( notification ).fire( new NotificationEvent( "ERROR", NotificationEvent.NotificationType.ERROR ) );

        verify( view ).setStartScannerState( State.ENABLED );
        verify( view ).setStopScannerState( State.DISABLED );
        verify( view ).setScanNowState( State.ENABLED );
        verify( view ).setUpgradeState( State.ENABLED );
    }

    @Test
    public void testStopScanner() {
        presenter.stopScanner();

        verify( view ).disableActions();
        verify( view ).setStartScannerState( State.ENABLED );
        verify( view ).setStopScannerState( State.DISABLED );
        verify( view ).setScanNowState( State.ENABLED );
        verify( view ).setUpgradeState( State.ENABLED );
    }

    @Test
    public void testStopScannerError() {
        doThrow( new RuntimeException() ).when( ruleCapabilitiesService ).stopScanner( eq( containerSpec ) );
        when( view.getStopScannerErrorMessage() ).thenReturn( "ERROR" );

        presenter.setup( containerSpec, ruleConfig );
        presenter.stopScanner();

        verify( notification ).fire( new NotificationEvent( "ERROR", NotificationEvent.NotificationType.ERROR ) );

        verify( view ).setStartScannerState( State.ENABLED );
        verify( view ).setStopScannerState( State.DISABLED );
        verify( view ).setScanNowState( State.ENABLED );
        verify( view ).setUpgradeState( State.ENABLED );
    }

    @Test
    public void testScanNow() {
        presenter.scanNow();

        verify( view ).disableActions();
        verify( view ).setStartScannerState( State.ENABLED );
        verify( view ).setStopScannerState( State.DISABLED );
        verify( view ).setScanNowState( State.ENABLED );
        verify( view ).setUpgradeState( State.ENABLED );
    }

    @Test
    public void testScanNowError() {
        doThrow( new RuntimeException() ).when( ruleCapabilitiesService ).scanNow( eq( containerSpec ) );
        when( view.getScanNowErrorMessage() ).thenReturn( "ERROR" );

        presenter.setup( containerSpec, ruleConfig );
        presenter.scanNow();

        verify( notification ).fire( new NotificationEvent( "ERROR", NotificationEvent.NotificationType.ERROR ) );

        verify( view ).setStartScannerState( State.ENABLED );
        verify( view ).setStopScannerState( State.DISABLED );
        verify( view ).setScanNowState( State.ENABLED );
        verify( view ).setUpgradeState( State.ENABLED );
    }

    @Test
    public void testStartScannerEmpty() {
        presenter.startScanner( "" );

        verify( view ).errorOnInterval();
    }

    @Test
    public void testStartScanner() {
        presenter.setup( containerSpec, ruleConfig );
        final String interval = "1";
        presenter.startScanner( interval );

        verify( view ).disableActions();

        verify( ruleCapabilitiesService ).startScanner( eq( containerSpec ), eq( Long.valueOf( interval ) ) );

        verify( view ).setStartScannerState( State.DISABLED );
        verify( view ).setStopScannerState( State.ENABLED );
        verify( view ).setScanNowState( State.DISABLED );
        verify( view ).setUpgradeState( State.DISABLED );
    }

    @Test
    public void testStartScannerError() {
        doThrow( new RuntimeException() ).when( ruleCapabilitiesService ).startScanner( eq( containerSpec ), anyLong() );
        when( view.getStartScannerErrorMessage() ).thenReturn( "ERROR" );

        presenter.setup( containerSpec, ruleConfig );
        presenter.startScanner( "1" );

        verify( notification ).fire( new NotificationEvent( "ERROR", NotificationEvent.NotificationType.ERROR ) );

        verify( view ).setStartScannerState( State.ENABLED );
        verify( view ).setStopScannerState( State.DISABLED );
        verify( view ).setScanNowState( State.ENABLED );
        verify( view ).setUpgradeState( State.ENABLED );
    }

    @Test
    public void testOnConfigUpdateNoUpdate() {
        final RuleConfigUpdated ruleConfigUpdated = new RuleConfigUpdated();
        ruleConfigUpdated.setContainerSpecKey( new ContainerSpecKey() );
        ruleConfigUpdated.setRuleConfig( new RuleConfig() );

        presenter.setup( containerSpec, ruleConfig );
        presenter.onConfigUpdate( ruleConfigUpdated );

        verify( view ).setContent( anyString(), anyString(), any( State.class ), any( State.class ), any( State.class ), any( State.class ) );
    }

    @Test
    public void testOnConfigUpdate() {
        final RuleConfigUpdated ruleConfigUpdated = new RuleConfigUpdated();
        ruleConfigUpdated.setContainerSpecKey( containerSpec );
        ruleConfigUpdated.setRuleConfig( ruleConfig );

        presenter.setup( containerSpec, ruleConfig );
        presenter.onConfigUpdate( ruleConfigUpdated );

        verify( view, times( 2 ) ).setContent( anyString(), anyString(), any( State.class ), any( State.class ), any( State.class ), any( State.class ) );
    }

    @Test
    public void testOnRuleConfigUpdate() {
        final RuleConfigUpdated ruleConfigUpdated = new RuleConfigUpdated();
        ruleConfigUpdated.setRuleConfig( ruleConfig );
        ruleConfigUpdated.setReleasedId( releaseId );
        final Long poolInterval = 1l;
        when( ruleConfig.getPollInterval() ).thenReturn( poolInterval );

        presenter.onRuleConfigUpdate( ruleConfigUpdated );

        verify( view ).setContent( eq( String.valueOf( poolInterval ) ), anyString(), any( State.class ), any( State.class ), any( State.class ), any( State.class ) );
    }

}