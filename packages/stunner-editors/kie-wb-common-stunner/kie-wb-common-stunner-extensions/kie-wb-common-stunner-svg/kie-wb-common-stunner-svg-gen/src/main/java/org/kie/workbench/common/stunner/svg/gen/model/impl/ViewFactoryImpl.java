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

package org.kie.workbench.common.stunner.svg.gen.model.impl;

import java.util.LinkedList;
import java.util.List;

import org.kie.workbench.common.stunner.svg.gen.model.StyleSheetDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewDefinition;
import org.kie.workbench.common.stunner.svg.gen.model.ViewFactory;

public class ViewFactoryImpl implements ViewFactory {

    private final String name;
    private final String pkg;
    private final String typoF;
    private final String viewBuilderType;
    private StyleSheetDefinition styleSheetDefinition;
    private final List<ViewDefinition<?>> viewDefinitions = new LinkedList<>();

    public ViewFactoryImpl(final String name,
                           final String pkg,
                           final String typoF,
                           final String viewBuilderType) {
        this.name = name;
        this.pkg = pkg;
        this.typoF = typoF;
        this.viewBuilderType = viewBuilderType;
    }

    @Override
    public String getSimpleName() {
        return name;
    }

    @Override
    public String getPackage() {
        return pkg;
    }

    @Override
    public String getImplementedType() {
        return typoF;
    }

    @Override
    public String getViewBuilderType() {
        return viewBuilderType;
    }

    @Override
    public StyleSheetDefinition getStyleSheetDefinition() {
        return styleSheetDefinition;
    }

    public void setStyleSheetDefinition(final StyleSheetDefinition styleSheetDefinition) {
        this.styleSheetDefinition = styleSheetDefinition;
    }

    @Override
    public List<ViewDefinition<?>> getViewDefinitions() {
        return viewDefinitions;
    }
}
