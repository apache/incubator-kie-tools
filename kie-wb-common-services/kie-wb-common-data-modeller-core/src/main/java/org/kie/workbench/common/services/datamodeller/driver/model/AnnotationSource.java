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

package org.kie.workbench.common.services.datamodeller.driver.model;

import java.util.HashMap;
import java.util.Map;

public class AnnotationSource {

    private String source;

    private Map<String, String> valuePairSources = new HashMap<String, String>(  );

    public AnnotationSource() {
    }

    public AnnotationSource withSource( String source ) {
        this.source = source;
        return this;
    }

    public AnnotationSource withValuePairSource( String valuePair, String source ) {
        valuePairSources.put( valuePair, source );
        return this;
    }

    public String getSource() {
        return source;
    }

    public String getValuePairSource( String valuePair ) {
        return valuePairSources.get( valuePair );
    }
}
