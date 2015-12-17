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

public class AnnotationSourceResponse extends DriverResponse {

    private Map<String, AnnotationSource> annotationSources = new HashMap<String, AnnotationSource>(  );

    public AnnotationSourceResponse() {
    }

    public AnnotationSourceResponse withAnnotationSource( String annotation, AnnotationSource annotationSource ) {
        annotationSources.put( annotation, annotationSource );
        return this;
    }

    public AnnotationSource getAnnotationSource( String annotation ) {
        return annotationSources.get( annotation );
    }

    public Map<String, AnnotationSource> getAnnotationSources() {
        return annotationSources;
    }
}
