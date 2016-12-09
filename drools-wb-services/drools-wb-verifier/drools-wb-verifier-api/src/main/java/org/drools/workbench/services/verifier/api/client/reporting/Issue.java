/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.services.verifier.api.client.reporting;

import java.util.HashSet;
import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class Issue {

    public static final Issue EMPTY = new Issue( );

    private final Severity severity;
    private final Set<Integer> rowNumbers;
    private ExplanationType explanationType;
    private String debugMessage;

    private Issue() {
        severity=null;
        rowNumbers=new HashSet<>();
    }

    public Issue( @MapsTo("severity") final Severity severity,
                  @MapsTo("explanationType") final ExplanationType explanationType,
                  @MapsTo("rowNumbers") final Set<Integer> rowNumbers ) {
        this.severity = severity;
        this.explanationType = explanationType;
        this.rowNumbers = rowNumbers;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Set<Integer> getRowNumbers() {
        return rowNumbers;
    }

    public ExplanationType getExplanationType() {
        return explanationType;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setDebugMessage( final String debugMessage ) {
        this.debugMessage = debugMessage;
    }
}
