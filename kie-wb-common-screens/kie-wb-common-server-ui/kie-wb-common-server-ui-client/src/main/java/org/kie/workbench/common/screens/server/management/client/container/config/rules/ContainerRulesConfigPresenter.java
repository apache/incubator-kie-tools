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
import org.kie.server.controller.api.model.spec.RuleConfig;
import org.kie.workbench.common.screens.server.management.client.util.State;
import org.kie.workbench.common.screens.server.management.service.RuleCapabilitiesService;
import org.slf4j.Logger;
import org.uberfire.client.mvp.UberView;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.screens.server.management.client.util.State.*;
import static org.kie.soup.commons.validation.PortablePreconditions.*;

@Dependent
public class ContainerRulesConfigPresenter {

    public interface View extends UberView<ContainerRulesConfigPresenter> {

        void setContent(final String interval,
                        final String version,
                        final State startScanner,
                        final State stopScanner,
                        final State scanNow,
                        final State upgrade);

        String getInterval();

        String getIntervalTimeUnit();

        String getVersion();

        void setVersion(String version);

        void setStartScannerState(final State state);

        void setStopScannerState(final State state);

        void setScanNowState(final State state);

        void setUpgradeState(final State state);

        void disableActions();

        void errorOnInterval();

        String getStartScannerErrorMessage();

        String getStopScannerErrorMessage();

        String getScanNowErrorMessage();

        String getUpgradeErrorMessage();

        String getUpgradeSuccessMessage();
    }

    protected static final String MS = "ms";
    protected static final String S = "s";
    protected static final String M = "m";
    protected static final String H = "h";
    protected static final String D = "d";

    private static final long SECOND_IN_MILLIS = 1000;
    private static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    private static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    private static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;

    private final Logger logger;
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
    public ContainerRulesConfigPresenter(final Logger logger,
                                         final View view,
                                         final Caller<RuleCapabilitiesService> ruleCapabilitiesService,
                                         final Event<NotificationEvent> notification) {
        this.logger = logger;
        this.view = view;
        this.ruleCapabilitiesService = ruleCapabilitiesService;
        this.notification = notification;
    }

    @PostConstruct
    public void init() {
        view.init(this);
    }

    public View getView() {
        return view;
    }

    public void setup(final ContainerSpec containerSpec,
                      final RuleConfig ruleConfig) {
        this.containerSpec = checkNotNull("containerSpec",
                                          containerSpec);
        setRuleConfig(ruleConfig,
                      containerSpec.getReleasedId().getVersion());
    }

    public void setVersion(final String version) {
        this.view.setVersion(version);
    }

    public void startScanner(final String interval,
                             final String timeUnit) {
        if (interval.trim().isEmpty()) {
            view.errorOnInterval();
            return;
        }
        Long actualInterval = calculateInterval(Long.valueOf(checkNotEmpty("interval",
                                                                           interval)),
                                                timeUnit);

        view.disableActions();
        ruleCapabilitiesService.call(new RemoteCallback<Void>() {
                                         @Override
                                         public void callback(final Void response) {
                                             scannerStatus = KieScannerStatus.STARTED;
                                             setScannerStatus();
                                             updateViewState();
                                         }
                                     },
                                     new ErrorCallback<Object>() {
                                         @Override
                                         public boolean error(final Object o,
                                                              final Throwable throwable) {
                                             notification.fire(new NotificationEvent(view.getStartScannerErrorMessage(),
                                                                                     NotificationEvent.NotificationType.ERROR));
                                             updateViewState();
                                             return false;
                                         }
                                     }).startScanner(containerSpec,
                                                     actualInterval);
    }

    private Long calculateInterval(final Long interval,
                                   final String timeUnit) {
        if (timeUnit == null || timeUnit.equalsIgnoreCase(MS)) {
            return interval;
        } else if (timeUnit.equalsIgnoreCase(S)) {
            return interval * SECOND_IN_MILLIS;
        } else if (timeUnit.equalsIgnoreCase(M)) {
            return interval * MINUTE_IN_MILLIS;
        } else if (timeUnit.equalsIgnoreCase(H)) {
            return interval * HOUR_IN_MILLIS;
        } else if (timeUnit.equalsIgnoreCase(D)) {
            return interval * DAY_IN_MILLIS;
        }

        logger.warn("Unexpected time unit, returning as milliseconds");
        return interval;
    }

    private Long adjustInterval(final Long interval,
                                final String timeUnit) {
        if (timeUnit == null || timeUnit.equalsIgnoreCase(MS)) {
            return interval;
        } else if (timeUnit.equalsIgnoreCase(S)) {
            return interval / SECOND_IN_MILLIS;
        } else if (timeUnit.equalsIgnoreCase(M)) {
            return interval / MINUTE_IN_MILLIS;
        } else if (timeUnit.equalsIgnoreCase(H)) {
            return interval / HOUR_IN_MILLIS;
        } else if (timeUnit.equalsIgnoreCase(D)) {
            return interval / DAY_IN_MILLIS;
        }

        logger.warn("Unexpected time unit, returning as milliseconds");
        return interval;
    }

    public void stopScanner() {
        view.disableActions();
        ruleCapabilitiesService.call(new RemoteCallback<Void>() {
                                         @Override
                                         public void callback(final Void response) {
                                             scannerStatus = KieScannerStatus.STOPPED;
                                             setScannerStatus();
                                             updateViewState();
                                         }
                                     },
                                     new ErrorCallback<Object>() {
                                         @Override
                                         public boolean error(final Object o,
                                                              final Throwable throwable) {
                                             notification.fire(new NotificationEvent(view.getStopScannerErrorMessage(),
                                                                                     NotificationEvent.NotificationType.ERROR));
                                             updateViewState();
                                             return false;
                                         }
                                     }).stopScanner(containerSpec);
    }

    public void scanNow() {
        view.disableActions();
        ruleCapabilitiesService.call(new RemoteCallback<Void>() {
                                         @Override
                                         public void callback(final Void response) {
                                             scannerStatus = KieScannerStatus.STOPPED;
                                             setScannerStatus();
                                             updateViewState();
                                         }
                                     },
                                     new ErrorCallback<Object>() {
                                         @Override
                                         public boolean error(final Object o,
                                                              final Throwable throwable) {
                                             notification.fire(new NotificationEvent(view.getScanNowErrorMessage(),
                                                                                     NotificationEvent.NotificationType.ERROR));
                                             updateViewState();
                                             return false;
                                         }
                                     }).scanNow(containerSpec);
    }

    public void upgrade(final String version) {
        view.disableActions();
        ruleCapabilitiesService.call(new RemoteCallback<Void>() {
                                         @Override
                                         public void callback(final Void response) {
                                             if (version != null && !version.isEmpty() &&
                                                     version.compareTo(containerSpec.getReleasedId().getVersion()) == 0) {
                                                 notification.fire(new NotificationEvent(view.getUpgradeSuccessMessage(),
                                                                                         NotificationEvent.NotificationType.SUCCESS));
                                             }

                                             updateViewState();
                                         }
                                     },
                                     new ErrorCallback<Object>() {
                                         @Override
                                         public boolean error(final Object o,
                                                              final Throwable throwable) {
                                             notification.fire(new NotificationEvent(view.getUpgradeErrorMessage(),
                                                                                     NotificationEvent.NotificationType.ERROR));
                                             updateViewState();
                                             return false;
                                         }
                                     }).upgradeContainer(containerSpec,
                                                         new ReleaseId(containerSpec.getReleasedId().getGroupId(),
                                                                       containerSpec.getReleasedId().getArtifactId(),
                                                                       version));
    }

    public void onConfigUpdate(@Observes final RuleConfigUpdated configUpdated) {
        if (configUpdated != null &&
                configUpdated.getContainerSpecKey() != null &&
                configUpdated.getContainerSpecKey().equals(containerSpec)) {
            setup(containerSpec,
                  configUpdated.getRuleConfig());
        } else {
            logger.warn("Illegal event argument.");
        }
    }

    public void onRuleConfigUpdate(@Observes final RuleConfigUpdated configUpdate) {
        if (configUpdate != null &&
                configUpdate.getRuleConfig() != null &&
                configUpdate.getReleasedId() != null) {
            setRuleConfig(configUpdate.getRuleConfig(),
                          configUpdate.getReleasedId().getVersion());
        } else {
            logger.warn("Illegal event argument.");
        }
    }

    private void setRuleConfig(final RuleConfig ruleConfig,
                               final String version) {
        checkNotNull("ruleConfig",
                     ruleConfig);
        checkNotEmpty("version",
                      version);

        this.scannerStatus = ruleConfig.getScannerStatus();

        if (ruleConfig.getPollInterval() != null) {
            this.pollInterval = String.valueOf(adjustInterval(ruleConfig.getPollInterval().longValue(),
                                                              view.getIntervalTimeUnit()));
        } else {
            this.pollInterval = "";
        }

        setScannerStatus();

        view.setContent(pollInterval,
                        version,
                        startScannerState,
                        stopScannerState,
                        scanNowState,
                        upgradeState);
    }

    private void setScannerStatus() {
        if (scannerStatus == null) {
            this.scannerStatus = KieScannerStatus.UNKNOWN;
        }

        switch (scannerStatus) {
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
        view.setStartScannerState(this.startScannerState);
        view.setStopScannerState(this.stopScannerState);
        view.setScanNowState(this.scanNowState);
        view.setUpgradeState(this.upgradeState);
    }
}
