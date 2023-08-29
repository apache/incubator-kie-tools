/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.forms.adf.service.definitions.layout;

import org.kie.workbench.common.forms.adf.service.definitions.elements.FormElement;

/**
 * Defines how a {@link FormElement} is going to be added to the Form Layout.
 */
public class LayoutSettings {

    private boolean wrap = false;
    private int horizontalSpan = 1;
    private int verticalSpan = 1;
    private String afterElement = null;

    public LayoutSettings() {
    }

    public boolean isWrap() {
        return wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public int getHorizontalSpan() {
        return horizontalSpan;
    }

    public void setHorizontalSpan(int horizontalSpan) {
        this.horizontalSpan = horizontalSpan;
    }

    public int getVerticalSpan() {
        return verticalSpan;
    }

    public void setVerticalSpan(int verticalSpan) {
        this.verticalSpan = verticalSpan;
    }

    public String getAfterElement() {
        return afterElement;
    }

    public void setAfterElement(String afterElement) {
        this.afterElement = afterElement;
    }
}
