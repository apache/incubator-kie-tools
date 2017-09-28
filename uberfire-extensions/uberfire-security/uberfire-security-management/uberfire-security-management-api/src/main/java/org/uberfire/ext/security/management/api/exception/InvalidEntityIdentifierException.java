/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.uberfire.ext.security.management.api.exception;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * <p>The entity's identifier is invalid.</p>
 * @since 2.0.0
 */
@Portable
public class InvalidEntityIdentifierException extends EntityNotFoundException {

    private final String symbolsAccepted;

    public InvalidEntityIdentifierException(@MapsTo("identifier") String identifier,
                                            @MapsTo("symbolsAccepted") String symbolsAccepted) {
        super(identifier);
        this.symbolsAccepted = symbolsAccepted;
    }

    public String getSymbolsAccepted() {
        return symbolsAccepted;
    }

    @Override
    public String getMessage() {
        return "Invalid entity identifier. " +
                "[identifier=" + getIdentifier() +
                ", symbolsAccepted=" + symbolsAccepted + "]";
    }
}
