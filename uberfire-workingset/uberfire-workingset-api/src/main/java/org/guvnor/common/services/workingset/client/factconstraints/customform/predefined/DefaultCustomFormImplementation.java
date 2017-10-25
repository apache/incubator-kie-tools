/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.workingset.client.factconstraints.customform.predefined;

import org.guvnor.common.services.workingset.client.factconstraints.customform.CustomFormConfiguration;

public class DefaultCustomFormImplementation
        implements CustomFormConfiguration {

    private int width = 200;
    private int height = 200;
    private String factType;
    private String fieldName;
    private String url;

    public String getFactType() {
        return this.factType;
    }

    public void setFactType(String factType) {
        this.factType = factType;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getCustomFormURL() {
        return this.url;
    }

    public void setCustomFormURL(String url) {
        this.url = url;
    }

    public int getCustomFormHeight() {
        return height;
    }

    public void setCustomFormHeight(int height) {
        this.height = height;
    }

    public int getCustomFormWidth() {
        return width;
    }

    public void setCustomFormWidth(int width) {
        this.width = width;
    }
}
