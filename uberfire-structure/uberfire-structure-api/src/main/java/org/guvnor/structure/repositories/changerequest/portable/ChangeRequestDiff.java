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

import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.vfs.Path;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@Portable
public class ChangeRequestDiff {

    private Path oldFilePath;
    private Path newFilePath;
    private ChangeType changeType;
    private Integer addedLinesCount;
    private Integer deletedLinesCount;
    private String diffText;
    private Boolean conflict;

    public ChangeRequestDiff(@MapsTo("oldFilePath") final Path oldFilePath,
                             @MapsTo("newFilePath") final Path newFilePath,
                             @MapsTo("changeType") final ChangeType changeType,
                             @MapsTo("addedLinesCount") final Integer addedLinesCount,
                             @MapsTo("deletedLinesCount") final Integer deletedLinesCount,
                             @MapsTo("diffText") final String diffText,
                             @MapsTo("conflict") final Boolean conflict) {
        this.oldFilePath = checkNotNull("oldFilePath", oldFilePath);
        this.newFilePath = checkNotNull("newFilePath", newFilePath);
        this.changeType = checkNotNull("changeType", changeType);
        this.addedLinesCount = checkNotNull("addedLinesCount", addedLinesCount);
        this.deletedLinesCount = checkNotNull("deletedLinesCount", deletedLinesCount);
        this.diffText = checkNotEmpty("diffText", diffText);
        this.conflict = checkNotNull("conflict", conflict);
    }

    public Path getOldFilePath() {
        return oldFilePath;
    }

    public Path getNewFilePath() {
        return newFilePath;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public Integer getAddedLinesCount() {
        return addedLinesCount;
    }

    public Integer getDeletedLinesCount() {
        return deletedLinesCount;
    }

    public String getDiffText() {
        return diffText;
    }

    public Boolean isConflict() {
        return conflict;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChangeRequestDiff that = (ChangeRequestDiff) o;
        return oldFilePath.equals(that.oldFilePath) &&
                newFilePath.equals(that.newFilePath) &&
                changeType == that.changeType &&
                addedLinesCount.equals(that.addedLinesCount) &&
                deletedLinesCount.equals(that.deletedLinesCount) &&
                diffText.equals(that.diffText) &&
                conflict == that.conflict;
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldFilePath,
                            newFilePath,
                            changeType,
                            addedLinesCount,
                            deletedLinesCount,
                            diffText,
                            conflict);
    }
}
