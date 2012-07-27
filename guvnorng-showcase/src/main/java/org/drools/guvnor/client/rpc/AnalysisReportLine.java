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

import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This is a single line of an analysis report.
 */
public class AnalysisReportLine
    implements
    IsSerializable {

    public String              description;
    public String              reason;
    public Integer                patternOrderNumber;
    public Cause[]             causes;
    public Map<String, String> impactedRules;

    public AnalysisReportLine() {
    }

    public AnalysisReportLine(String description,
                              String reason,
                              Cause[] causes) {
        this.description = description;
        this.reason = reason;
        this.causes = causes;
    }

}
