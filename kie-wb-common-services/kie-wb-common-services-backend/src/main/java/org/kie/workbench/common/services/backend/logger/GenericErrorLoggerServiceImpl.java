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

package org.kie.workbench.common.services.backend.logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.kie.workbench.common.services.shared.logger.GenericErrorLoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@ApplicationScoped
public class GenericErrorLoggerServiceImpl implements GenericErrorLoggerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericErrorLoggerServiceImpl.class);

    private final User user;

    @Inject
    public GenericErrorLoggerServiceImpl(final User user) {
        this.user = user;
    }

    @Override
    public void log(final String errorId,
                    final String clientLocation,
                    final String errorDetails) {
        final String message = String.format("Error from user: %s Error ID: %s Location: %s Exception: %s",
                                             user.getIdentifier(),
                                             errorId,
                                             clientLocation,
                                             errorDetails);
        LOGGER.error(message);
    }
}
