/*
 * 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.base;

import java.util.List;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotEmpty;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * Represents difference between two files. This is just a segment of the file,
 * not necessary the differences of the whole file.
 */
public class FileDiff {

    private List<String> linesA;
    private List<String> linesB;
    private String changeType;
    private String nameA;
    private String nameB;
    private int startA;
    private int endA;
    private int startB;
    private int endB;

    public FileDiff(final String nameA,
                    final String nameB,
                    final int startA,
                    final int endA,
                    final int startB,
                    final int endB,
                    final String changeType,
                    final List<String> linesA,
                    final List<String> linesB) {

        this.nameA = checkNotEmpty("nameA",
                                   nameA);
        this.nameB = checkNotEmpty("nameB",
                                   nameB);
        this.startA = startA;
        this.endA = endA;
        this.startB = startB;
        this.endB = endB;
        this.changeType = checkNotEmpty("nameA",
                                        changeType);
        this.linesA = checkNotNull("linesA",
                                   linesA);
        this.linesB = checkNotNull("linesB",
                                   linesB);
    }

    public List<String> getLinesA() {
        return linesA;
    }

    public List<String> getLinesB() {
        return linesB;
    }

    public String getChangeType() {
        return changeType;
    }

    public String getNameA() {
        return nameA;
    }

    public String getNameB() {
        return nameB;
    }

    public int getStartA() {
        return startA;
    }

    public int getEndA() {
        return endA;
    }

    public int getStartB() {
        return startB;
    }

    public int getEndB() {
        return endB;
    }

    @Override
    public String toString() {

        final String linesFromA = this.getLinesA().stream().reduce("",
                                                                   (acum, elem) -> acum += "-" + new String(elem.getBytes()) + "\n");
        final String linesFromB = this.getLinesB().stream().reduce("",
                                                                   (acum, elem) -> acum += "+" + new String(elem.getBytes()) + "\n");

        StringBuilder builder = new StringBuilder();
        builder.append("FileDiff { \n");
        builder.append(this.getChangeType());
        builder.append(" , \n");

        builder.append(this.getNameA());
        builder.append(" -> ");
        builder.append("( " + this.getStartA() + " , " + this.getEndA() + " )");
        builder.append("[ " + linesFromA + " ]");
        builder.append(" || ");
        builder.append(this.getNameB());
        builder.append(" -> ");
        builder.append("( " + this.getStartB() + " , " + this.getEndB() + " )");
        builder.append("[ " + linesFromB + " ]");
        builder.append("}");

        return builder.toString();
    }

    @Override
    public int hashCode() {
        int result = Integer.hashCode(startA);
        result = ~~result;
        result = 31 * result + (Integer.hashCode(endA));
        result = ~~result;
        result = 31 * result + (Integer.hashCode(startB));
        result = ~~result;
        result = 31 * result + (Integer.hashCode(endB));
        result = ~~result;
        result = 31 * result + (nameA.hashCode());
        result = ~~result;
        result = 31 * result + (nameB.hashCode());
        result = ~~result;
        result = 31 * result + (changeType.hashCode());
        result = ~~result;
        result = 31 * result + (linesA.hashCode());
        result = ~~result;
        result = 31 * result + (linesB.hashCode());
        result = ~~result;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof FileDiff) {
            FileDiff external = (FileDiff) obj;
            return this.startA == external.startA &&
                    this.endA == external.endA &&
                    this.startB == external.startB &&
                    this.endB == external.endB &&
                    this.changeType.equals(external.changeType) &&
                    this.nameA.equals(external.nameA) &&
                    this.nameB.equals(external.nameB) &&
                    this.linesA.equals(external.linesA) &&
                    this.linesB.equals(external.getLinesB());
        } else {
            return super.equals(obj);
        }
    }
}
