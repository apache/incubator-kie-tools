/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.jsinterop;

import java.util.Arrays;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListView;

@JsType(namespace = JsPackage.GLOBAL, name = "ImportJavaClassesAPIWrapper")
public class ImportJavaClassesService {

    private static DataTypeListView dataTypeListView;

    public static void registerBroadcastForImportJavaClasses(final DataTypeListView dataTypeListView) {
        ImportJavaClassesService.dataTypeListView = dataTypeListView;
    }

    @JsMethod
    public static void importJavaClasses(JavaClass[] javaClasses) {
        dataTypeListView.importJavaClasses(Arrays.asList(javaClasses));
    }

}
