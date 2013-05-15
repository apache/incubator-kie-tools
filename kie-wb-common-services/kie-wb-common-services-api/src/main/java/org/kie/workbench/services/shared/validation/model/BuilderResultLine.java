/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.services.shared.validation.model;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Returned by the builder.
 */
@Portable
public class BuilderResultLine {

    private String resourceId;
    private String resourceName;
    private String resourceFormat;
    private String message;

    public BuilderResultLine() {

    }

    public String getResourceId() {
        return resourceId;
    }

    public BuilderResultLine setResourceId( String resourceId ) {
        this.resourceId = resourceId;
        return this;
    }

    public String getResourceName() {
        return resourceName;
    }

    public BuilderResultLine setResourceName( String resourceName ) {
        this.resourceName = resourceName;
        return this;
    }

    public String getResourceFormat() {
        return resourceFormat;
    }

    public BuilderResultLine setResourceFormat( String resourceFormat ) {
        this.resourceFormat = resourceFormat;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public BuilderResultLine setMessage( String message ) {
        this.message = message;
        return this;
    }
}
