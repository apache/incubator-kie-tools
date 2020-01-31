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

package org.kie.workbench.common.screens.archetype.mgmt.shared.model;

import java.util.Date;
import java.util.Objects;

import org.guvnor.common.services.project.model.GAV;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class Archetype {

    private static final String DEFAULT_MESSAGE = "N/A";

    private final String alias;
    private final GAV gav;
    private final Date createdDate;
    private final ArchetypeStatus status;
    private final String message;
    private final Boolean internal;

    public Archetype(final String alias,
                     final GAV gav,
                     final Date createdDate,
                     final ArchetypeStatus status) {
        this(alias,
             gav,
             createdDate,
             status,
             DEFAULT_MESSAGE);
    }

    public Archetype(final String alias,
                     final GAV gav,
                     final Date createdDate,
                     final ArchetypeStatus status,
                     final Boolean internal) {
        this(alias,
             gav,
             createdDate,
             status,
             DEFAULT_MESSAGE,
             internal);
    }

    public Archetype(final String alias,
                     final GAV gav,
                     final Date createdDate,
                     final ArchetypeStatus status,
                     final String message) {
        this(alias,
             gav,
             createdDate,
             status,
             message,
             false);
    }

    public Archetype(@MapsTo("alias") final String alias,
                     @MapsTo("gav") final GAV gav,
                     @MapsTo("createdDate") final Date createdDate,
                     @MapsTo("status") final ArchetypeStatus status,
                     @MapsTo("message") final String message,
                     @MapsTo("internal") final Boolean internal) {
        this.alias = checkNotEmpty("alias", alias);
        this.gav = checkNotNull("gav", gav);
        this.createdDate = checkNotNull("createdDate", createdDate);
        this.status = checkNotNull("status", status);
        this.message = message == null || message.isEmpty() ? DEFAULT_MESSAGE : message;
        this.internal = internal != null && internal;
    }

    public String getAlias() {
        return alias;
    }

    public GAV getGav() {
        return gav;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public ArchetypeStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Boolean isInternal() {
        return internal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Archetype archetype = (Archetype) o;
        return alias.equals(archetype.alias) &&
                gav.equals(archetype.gav) &&
                createdDate.equals(archetype.createdDate) &&
                status == archetype.status &&
                message.equals(archetype.message) &&
                internal.equals(archetype.internal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias, gav, createdDate, status, message, internal);
    }
}
