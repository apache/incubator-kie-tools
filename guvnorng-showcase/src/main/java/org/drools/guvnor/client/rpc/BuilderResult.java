/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Returned by the builder.
 */
public class BuilderResult
        implements
        IsSerializable {

    private List<BuilderResultLine> lines = new ArrayList<BuilderResultLine>();

    public void addLines(List<BuilderResultLine> lines) {
        this.lines.addAll(lines);
    }

    public void addLine(BuilderResultLine builderResultLine) {
        this.lines.add(builderResultLine);
    }

    public List<BuilderResultLine> getLines() {
        return Collections.unmodifiableList(lines);
    }

    public boolean hasLines() {
        return !lines.isEmpty();
    }

    public static BuilderResult emptyResult() {
        return new BuilderResult();
    }
}
