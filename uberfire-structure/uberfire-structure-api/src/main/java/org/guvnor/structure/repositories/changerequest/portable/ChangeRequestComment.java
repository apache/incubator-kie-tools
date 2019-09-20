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

package org.guvnor.structure.repositories.changerequest.portable;

import java.util.Date;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class ChangeRequestComment {

    private Long id;
    private String authorId;
    private Date createdDate;
    private String text;

    public ChangeRequestComment(@MapsTo("id") final Long id,
                                @MapsTo("authorId") final String authorId,
                                @MapsTo("createdDate") final Date createdDate,
                                @MapsTo("text") final String text) {
        this.id = checkNotNull("id", id);
        this.authorId = checkNotEmpty("authorId", authorId);
        this.createdDate = checkNotNull("createdDate", createdDate);
        this.text = checkNotEmpty("text", text);
    }

    public Long getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChangeRequestComment that = (ChangeRequestComment) o;
        return id.equals(that.id) &&
                authorId.equals(that.authorId) &&
                createdDate.equals(that.createdDate) &&
                text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                            authorId,
                            createdDate,
                            text);
    }
}