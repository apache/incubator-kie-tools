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

@JsType
public class ExpressionProps {

    public final String id;

    public final String name;

    public final String dataType;

    public final String logicType;

    public ExpressionProps(final String id, final String name, final String dataType, final String logicType) {
        this.id = id;
        this.name = name;
        this.dataType = dataType;
        this.logicType = logicType;
    }
}
