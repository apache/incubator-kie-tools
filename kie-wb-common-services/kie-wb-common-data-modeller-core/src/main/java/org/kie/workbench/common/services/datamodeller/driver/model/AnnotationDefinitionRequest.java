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

public class AnnotationDefinitionRequest extends DriverRequest {

    private String className;

    public AnnotationDefinitionRequest() {
    }

    public AnnotationDefinitionRequest( String className ) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        AnnotationDefinitionRequest request = ( AnnotationDefinitionRequest ) o;

        return !( className != null ? !className.equals( request.className ) : request.className != null );

    }

    @Override public int hashCode() {
        return className != null ? className.hashCode() : 0;
    }
}
