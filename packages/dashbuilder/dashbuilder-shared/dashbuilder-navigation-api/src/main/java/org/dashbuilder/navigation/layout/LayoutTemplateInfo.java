/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.navigation.layout;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

/**
 * Class containing a perspective's layout template plus additional information like if any of its components
 * is linked to another perspective causing an inifinite loop. For example, imagine a perspective A containing a tab list
 * component with a tab linked to perspective B. The issue might occur if perspective B contains
 * a component linked to perspective A.
 */
@Portable
public class LayoutTemplateInfo {

    LayoutTemplate layoutTemplate;
    boolean hasNavigationComponents;
    LayoutRecursionIssue recursionIssue;

    public LayoutTemplateInfo() {
    }

    public LayoutTemplateInfo(LayoutTemplate layoutTemplate, boolean hasNavigationComponents, LayoutRecursionIssue recursionIssue) {
        this.layoutTemplate = layoutTemplate;
        this.hasNavigationComponents = hasNavigationComponents;
        this.recursionIssue = recursionIssue;
    }

    public LayoutTemplate getLayoutTemplate() {
        return layoutTemplate;
    }

    public LayoutRecursionIssue getRecursionIssue() {
        return recursionIssue;
    }

    public boolean hasNavigationComponents() {
        return hasNavigationComponents;
    }
}
