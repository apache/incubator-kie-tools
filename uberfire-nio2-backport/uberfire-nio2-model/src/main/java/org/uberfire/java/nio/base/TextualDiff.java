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

package org.uberfire.java.nio.base;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;

public class TextualDiff {

    private String oldFilePath;
    private String newFilePath;
    private String changeType;
    private int linesAdded;
    private int linesDeleted;
    private String diffText;

    public TextualDiff(final String oldFilePath,
                       final String newFilePath,
                       final String changeType,
                       final int linesAdded,
                       final int linesDeleted,
                       final String diffText) {
        this.oldFilePath = checkNotEmpty("oldFilePath",
                                         oldFilePath);
        this.newFilePath = checkNotEmpty("newFilePath",
                                         newFilePath);
        this.changeType = checkNotEmpty("changeType",
                                        changeType);

        this.linesAdded = linesAdded;
        this.linesDeleted = linesDeleted;

        this.diffText = checkNotEmpty("diffText",
                                      diffText);
    }

    public String getOldFilePath() {
        return oldFilePath;
    }

    public String getNewFilePath() {
        return newFilePath;
    }

    public String getChangeType() {
        return changeType;
    }

    public int getLinesAdded() {
        return linesAdded;
    }

    public int getLinesDeleted() {
        return linesDeleted;
    }

    public String getDiffText() {
        return diffText;
    }
}
