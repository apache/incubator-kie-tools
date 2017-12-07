/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.ext.security.management.api.exception.EntityNotFoundException;
import org.uberfire.ext.security.management.api.exception.GroupNotFoundException;
import org.uberfire.ext.security.management.api.exception.NoImplementationAvailableException;
import org.uberfire.ext.security.management.api.exception.RealmManagementNotAuthorizedException;
import org.uberfire.ext.security.management.api.exception.SecurityManagementException;
import org.uberfire.ext.security.management.api.exception.UnsupportedServiceCapabilityException;
import org.uberfire.ext.security.management.api.exception.UserAlreadyExistsException;
import org.uberfire.ext.security.management.api.exception.UserNotFoundException;
import org.uberfire.ext.security.management.client.resources.i18n.UsersManagementClientConstants;

/**
 * It resolves the security management exception messages
 * for being presented to the end users,
 */
@ApplicationScoped
public class ClientSecurityExceptionMessageResolver {

    private static final String ARG_SEPARATOR = ": ";

    private final Map<Class<?>, Function<SecurityManagementException, String>> messageResolvers = new HashMap<>(6);

    @PostConstruct
    public void registerMessageResolvers() {
        register(EntityNotFoundException.class,
                 e -> getArgMessage(UsersManagementClientConstants.INSTANCE.entityNotFound(),
                                    e.getIdentifier()));
        register(UserNotFoundException.class,
                 e -> getArgMessage(UsersManagementClientConstants.INSTANCE.userNotFound(),
                                    e.getIdentifier()));
        register(GroupNotFoundException.class,
                 e -> getArgMessage(UsersManagementClientConstants.INSTANCE.groupNotFound(),
                                    e.getIdentifier()));
        register(NoImplementationAvailableException.class,
                 e -> UsersManagementClientConstants.INSTANCE.noUserSystemManagerActive());
        register(UnsupportedServiceCapabilityException.class,
                 e -> getArgMessage(UsersManagementClientConstants.INSTANCE.unsupportedCapability(),
                                    e.getCapability().name()));
        register(UserAlreadyExistsException.class,
                 e -> getArgMessage(UsersManagementClientConstants.INSTANCE.userAlreadyExists(),
                                    e.getUserId()));
        register(RealmManagementNotAuthorizedException.class,
                 e -> getArgMessage(UsersManagementClientConstants.INSTANCE.realmManagementNotAuthorized(),
                                    e.getRealmResource()));
    }

    /**
     * Main entry point for handling all security management related errors.
     * It uses the error's message, if any, otherwise uses the exception generic message.
     * It also skips certain exception types for being displayed to the user and
     * constantly showing popup error messages.
     * @param exception The exception for being displayed
     * @param messageConsumer Consumes the message to display, if applies. Otherwise it's not called.
     */
    public void consumeExceptionMessage(final Throwable exception,
                                        final Consumer<String> messageConsumer) {
        if (shouldDisplayError().test(exception)) {
            final String message = isSecurityManagementException().test(exception) ?
                    getSecurityExceptionMessage().apply((SecurityManagementException) exception) :
                    getExceptionMessage().apply(exception);
            messageConsumer.accept(message);
        }
    }

    private Predicate<Throwable> shouldDisplayError() {
        return throwable -> !(throwable instanceof NoImplementationAvailableException);
    }

    private Predicate<Throwable> isSecurityManagementException() {
        return throwable -> throwable instanceof SecurityManagementException;
    }

    private Function<SecurityManagementException, String> getSecurityExceptionMessage() {
        return exception -> messageResolvers
                .getOrDefault(exception.getClass(),
                              SecurityManagementException::getMessage)
                .apply(exception);
    }

    private Function<Throwable, String> getExceptionMessage() {
        return exception -> exception.getCause() != null ?
                exception.getCause().getMessage() :
                exception.getMessage();
    }

    @SuppressWarnings("unchecked")
    private <E extends SecurityManagementException> void register(final Class<E> type,
                                                                  final Function<E, String> messageResolver) {
        messageResolvers.put(type,
                             (Function<SecurityManagementException, String>) messageResolver);
    }

    private static String getArgMessage(final String message,
                                        final String arg) {
        return message + ARG_SEPARATOR + arg;
    }
}
