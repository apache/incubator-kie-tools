/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.screens.server.management.client.container.config.rules;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.server.api.model.KieScannerStatus;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.events.RuleConfigUpdated;
import org.kie.server.controller.api.model.spec.ContainerSpec;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;
import org.kie.server.controller.api.model.spec.RuleConfig;
import org.kie.workbench.common.screens.server.management.client.util.State;
import org.kie.workbench.common.screens.server.management.service.RuleCapabilitiesService;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.server.management.client.util.State.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;

@Dependent
public class ContainerRulesConfigPresenter {

    public interface View extends UberView<ContainerRulesConfigPresenter> {

        void setContent( final String interval,
                         final String version,
                         final State startScanner,
                         final State stopScanner,
                         final State scanNow,
                         final State upgrade );

        String getInterval();

        String getVersion();

        void setVersion( String version );

        void setStartScannerState( final State state );

        void setStopScannerState( final State state );

        void setScanNowState( final State state );

        void setUpgradeState( final State state );

        void disableActions();

        void errorOnInterval();

        String getStartScannerErrorMessage();

        String getStopScannerErrorMessage();

        String getScanNowErrorMessage();

        String getUpgradeErrorMessage();
    }

    private final View view;
    private final Caller<RuleCapabilitiesService> ruleCapabilitiesService;
    private final Event<NotificationEvent> notification;

    private ContainerSpec containerSpec;

    private String pollInterval;
    private KieScannerStatus scannerStatus;

    private State startScannerState;
    private State stopScannerState;
    private State scanNowState;
    private State upgradeState;

    @Inject
    public ContainerRulesConfigPresenter( final View view,
                                          final Caller<RuleCapabilitiesService> ruleCapabilitiesService,
                                          final Event<NotificationEvent> notification ) {
        this.view = view;
        this.ruleCapabilitiesService = ruleCapabilitiesService;
        this.notification = notification;
    }

    @PostConstruct
    public void init() {
        view.init( this );
    }

    public View getView() {
        return view;
    }

    public void setup( final ContainerSpec containerSpec,
                       final RuleConfig ruleConfig ) {
        this.containerSpec = checkNotNull( "containerSpec", containerSpec );
        setRuleConfig( ruleConfig, containerSpec.getReleasedId().getVersion() );
    }

    public void setVersion( final String version ) {
        this.view.setVersion( version );
    }

    public void startScanner( final String interval ) {
        if ( interval.trim().isEmpty() ) {
            view.errorOnInterval();
            return;
        }
        view.disableActions();
        ruleCapabilitiesService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void response ) {
                scannerStatus = KieScannerStatus.STARTED;
                setScannerStatus();
                updateViewState();
            }
        }, new ErrorCallback<Object>() {
            @Override
            public boolean error( final Object o,
                                  final Throwable throwable ) {
                notification.fire( new NotificationEvent( view.getStartScannerErrorMessage(), NotificationEvent.NotificationType.ERROR ) );
                updateViewState();
                return false;
            }
        } ).startScanner( containerSpec, Integer.valueOf( checkNotEmpty( "interval", interval ) ) );
    }

    public void stopScanner() {
        view.disableActions();
        ruleCapabilitiesService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void response ) {
                scannerStatus = KieScannerStatus.STOPPED;
                setScannerStatus();
                updateViewState();
            }
        }, new ErrorCallback<Object>() {
            @Override
            public boolean error( final Object o,
                                  final Throwable throwable ) {
                notification.fire( new NotificationEvent( view.getStopScannerErrorMessage(), NotificationEvent.NotificationType.ERROR ) );
                updateViewState();
                return false;
            }
        } ).stopScanner( containerSpec );
    }

    public void scanNow() {
        view.disableActions();
        ruleCapabilitiesService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void response ) {
                scannerStatus = KieScannerStatus.STOPPED;
                setScannerStatus();
                updateViewState();
            }
        }, new ErrorCallback<Object>() {
            @Override
            public boolean error( final Object o,
                                  final Throwable throwable ) {
                notification.fire( new NotificationEvent( view.getScanNowErrorMessage(), NotificationEvent.NotificationType.ERROR ) );
                updateViewState();
                return false;
            }
        } ).scanNow( containerSpec );
    }

    public void upgrade( final String version ) {
        view.disableActions();
        ruleCapabilitiesService.call( new RemoteCallback<Void>() {
            @Override
            public void callback( final Void response ) {
                updateViewState();
            }
        }, new ErrorCallback<Object>() {
            @Override
            public boolean error( final Object o,
                                  final Throwable throwable ) {
                notification.fire( new NotificationEvent( view.getUpgradeErrorMessage(), NotificationEvent.NotificationType.ERROR ) );
                updateViewState();
                return false;
            }
        } ).upgradeContainer( containerSpec,
                              new ReleaseId( containerSpec.getReleasedId().getGroupId(),
                                             containerSpec.getReleasedId().getArtifactId(),
                                             version ) );
    }

    public void onConfigUpdate( @Observes final RuleConfigUpdated configUpdated ) {
        checkNotNull( "configUpdated", configUpdated );
        if ( configUpdated.getContainerSpecKey().equals( containerSpec ) ) {
            setup( containerSpec, configUpdated.getRuleConfig() );
        }
    }

    public void onRuleConfigUpdate( @Observes final RuleConfigUpdated configUpdate ) {
        checkNotNull( "configUpdate", configUpdate );
        setRuleConfig( configUpdate.getRuleConfig(),
                       configUpdate.getReleasedId().getVersion() );
    }

    public void setRuleConfig( final RuleConfig ruleConfig,
                               final String version ) {
        checkNotNull( "ruleConfig", ruleConfig );
        checkNotEmpty( "version", version );

        this.scannerStatus = ruleConfig.getScannerStatus();

        if ( ruleConfig.getPollInterval() != null ) {
            this.pollInterval = String.valueOf( ruleConfig.getPollInterval().longValue() );
        } else {
            this.pollInterval = "";
        }

        setScannerStatus();

        view.setContent( pollInterval,
                         version,
                         startScannerState,
                         stopScannerState,
                         scanNowState,
                         upgradeState );
    }

    private void setScannerStatus() {
        if ( scannerStatus == null ) {
            this.scannerStatus = KieScannerStatus.UNKNOWN;
        }

        switch ( scannerStatus ) {
            case CREATED:
            case STARTED:
            case SCANNING:
                this.startScannerState = DISABLED;
                this.stopScannerState = ENABLED;
                this.scanNowState = DISABLED;
                this.upgradeState = DISABLED;
                break;
            case STOPPED:
            case DISPOSED:
                this.startScannerState = ENABLED;
                this.stopScannerState = DISABLED;
                this.scanNowState = ENABLED;
                this.upgradeState = ENABLED;
                break;
            case UNKNOWN:
            default:
                this.startScannerState = ENABLED;
                this.stopScannerState = DISABLED;
                this.scanNowState = ENABLED;
                this.upgradeState = ENABLED;
                break;
        }
    }

    void updateViewState() {
        view.setStartScannerState( this.startScannerState );
        view.setStopScannerState( this.stopScannerState );
        view.setScanNowState( this.scanNowState );
        view.setUpgradeState( this.upgradeState );
    }

    private ContainerSpecKey toId( final ContainerSpec containerSpec ) {
        return new ContainerSpecKey( containerSpec.getId(),
                                     containerSpec.getContainerName(),
                                     containerSpec.getServerTemplateKey() );
    }

}
