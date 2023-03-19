/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dsl.model;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.dashbuilder.dataset.def.DataSetDef;

public class Dashboard {

    private List<Page> pages;
    private List<DataSetDef> dataSets;
    private Navigation navigation;
    private Path componentsPath;

    private Dashboard(List<Page> pages, List<DataSetDef> dataSets, Navigation navigation, Path componentsPath) {
        this.componentsPath = componentsPath;
        this.pages = pages;
        this.dataSets = dataSets;
        this.navigation = navigation;
    }

    public static Dashboard of(List<Page> pages, List<DataSetDef> dataSets, Navigation navigation, Path componentsPath) {
        return new Dashboard(pages, dataSets, navigation, componentsPath);
    }

    public List<Page> getPages() {
        return pages;
    }

    public List<DataSetDef> getDataSets() {
        return dataSets;
    }

    public Navigation getNavigation() {
        return navigation;
    }

    public Optional<Path> getComponentsPath() {
        return Optional.ofNullable(componentsPath);
    }

}