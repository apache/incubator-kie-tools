/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props;

import jsinterop.annotations.JsType;
import org.kie.dmn.model.api.FunctionKind;

@JsType
public class JavaFunctionProps extends FunctionProps {

    public final String className;
    public final String methodName;
    public final String classFieldId;
    public final String methodFieldId;

    public JavaFunctionProps(final String id, final String name, final String dataType, final EntryInfo[] formalParameters, final Double parametersWidth, final String className, final String methodName, final String classFieldId, final String methodFieldId) {
        super(id, name, dataType, formalParameters, parametersWidth, FunctionKind.JAVA.value());
        this.className = className;
        this.methodName = methodName;
        this.classFieldId = classFieldId;
        this.methodFieldId = methodFieldId;
    }
}
