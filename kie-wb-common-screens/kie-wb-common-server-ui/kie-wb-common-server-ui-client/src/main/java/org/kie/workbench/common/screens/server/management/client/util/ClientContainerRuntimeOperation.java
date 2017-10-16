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

package org.kie.workbench.common.screens.server.management.client.util;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.server.management.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.server.management.model.ContainerRuntimeOperation;

import static org.kie.soup.commons.validation.PortablePreconditions.*;

public enum ClientContainerRuntimeOperation {

    START_CONTAINER(Constants.ClientContainerRuntimeOperation_StartContainer,
                    ContainerRuntimeOperation.START_CONTAINER),
    STOP_CONTAINER(Constants.ClientContainerRuntimeOperation_StopContainer,
                   ContainerRuntimeOperation.STOP_CONTAINER),
    UPGRADE_CONTAINER(Constants.ClientContainerRuntimeOperation_UpgradeContainer,
                      ContainerRuntimeOperation.UPGRADE_CONTAINER),
    SCAN(Constants.ClientContainerRuntimeOperation_Scan,
         ContainerRuntimeOperation.SCAN),
    START_SCANNER(Constants.ClientContainerRuntimeOperation_StartScanner,
                  ContainerRuntimeOperation.START_SCANNER),
    STOP_SCANNER(Constants.ClientContainerRuntimeOperation_StopScanner,
                 ContainerRuntimeOperation.STOP_SCANNER),;

    private final String valueTranslationKey;
    private final ContainerRuntimeOperation containerRuntimeOperation;

    ClientContainerRuntimeOperation(final String valueTranslationKey,
                                    final ContainerRuntimeOperation containerRuntimeOperation) {
        this.valueTranslationKey = checkNotEmpty("valueTranslationKey",
                                                 valueTranslationKey);
        this.containerRuntimeOperation = checkNotNull("containerRuntimeOperation",
                                                      containerRuntimeOperation);
    }

    public ContainerRuntimeOperation getContainerRuntimeOperation() {
        return containerRuntimeOperation;
    }

    public String getValue(final TranslationService translationService) {
        return translationService.format(valueTranslationKey);
    }

    public static ClientContainerRuntimeOperation convert(final ContainerRuntimeOperation containerRuntimeOperation) {
        switch (containerRuntimeOperation) {
            case START_CONTAINER:
                return ClientContainerRuntimeOperation.START_CONTAINER;
            case STOP_CONTAINER:
                return ClientContainerRuntimeOperation.STOP_CONTAINER;
            case UPGRADE_CONTAINER:
                return ClientContainerRuntimeOperation.UPGRADE_CONTAINER;
            case SCAN:
                return ClientContainerRuntimeOperation.SCAN;
            case START_SCANNER:
                return ClientContainerRuntimeOperation.START_SCANNER;
            case STOP_SCANNER:
                return ClientContainerRuntimeOperation.STOP_SCANNER;
        }
        throw new RuntimeException("Invalid parameter");
    }

}
