/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.svg.gen;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.processing.Messager;

public class SVGGeneratorRequest {

    private final String name;
    private final String pkg;
    private final String typeOf;
    private final String cssPath;
    private final String viewBuilderType;
    private final Map<String, String> viewSources = new LinkedHashMap<>();
    private final Messager messager;

    public SVGGeneratorRequest(final String name,
                               final String pkg,
                               final String typeOf,
                               final String cssPath,
                               final String viewBuilderType,
                               final Messager messager) {
        this.name = name;
        this.pkg = pkg;
        this.typeOf = typeOf;
        this.cssPath = cssPath;
        this.viewBuilderType = viewBuilderType;
        this.messager = messager;
    }

    public String getName() {
        return name;
    }

    public String getPkg() {
        return pkg;
    }

    public String getImplementedType() {
        return typeOf;
    }

    public String getCssPath() {
        return cssPath;
    }

    public String getViewBuilderType() {
        return viewBuilderType;
    }

    public String put(final String methodName,
                      String source) {
        return viewSources.put(methodName,
                               source);
    }

    public Map<String, String> getViewSources() {
        return viewSources;
    }

    public Messager getMessager() {
        return messager;
    }
}
