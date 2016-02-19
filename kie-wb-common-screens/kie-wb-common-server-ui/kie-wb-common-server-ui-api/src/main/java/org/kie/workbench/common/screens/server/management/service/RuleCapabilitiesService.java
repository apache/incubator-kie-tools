package org.kie.workbench.common.screens.server.management.service;

import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.controller.api.model.spec.ContainerSpecKey;

@Remote
public interface RuleCapabilitiesService {

    void scanNow(final ContainerSpecKey containerSpecKey);

    void startScanner(final ContainerSpecKey containerSpecKey, long interval);

    void stopScanner(final ContainerSpecKey containerSpecKey);

    void upgradeContainer(final ContainerSpecKey containerSpecKey, final ReleaseId releaseId);

}
