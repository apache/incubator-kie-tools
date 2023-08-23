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

package org.kie.workbench.common.dmn.client.editors.documentation.common;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class DMNDocumentationDataType {

    private String name;

    private String type;

    private String listLabel;

    private String constraint;

    private int level;

    private boolean isTopLevel;

    @JsOverlay
    public static DMNDocumentationDataType create(final String name,
                                                  final String type,
                                                  final String listLabel,
                                                  final String constraint,
                                                  final int level) {

        final DMNDocumentationDataType dataType = new DMNDocumentationDataType();

        dataType.name = name;
        dataType.type = type;
        dataType.level = level;
        dataType.listLabel = listLabel;
        dataType.constraint = constraint;
        dataType.isTopLevel = level == 0;

        return dataType;
    }

    @JsOverlay
    public final String getName() {
        return name;
    }

    @JsOverlay
    public final String getType() {
        return type;
    }

    @JsOverlay
    public final String getListLabel() {
        return listLabel;
    }

    @JsOverlay
    public final String getConstraint() {
        return constraint;
    }

    @JsOverlay
    public final int getLevel() {
        return level;
    }

    @JsOverlay
    public final boolean isTopLevel() {
        return isTopLevel;
    }
}
