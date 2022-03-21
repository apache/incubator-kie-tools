/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.parameters;

import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;

public interface ParameterView extends org.jboss.errai.ui.client.local.api.IsElement {

    void setName(final String text);

    void setTypeRef(final HasTypeRef hasTypeRef);

    void addRemoveClickHandler(final Command command);

    void addParameterNameChangeHandler(final ParameterizedCommand<String> command);

    void addParameterTypeRefChangeHandler(final ParameterizedCommand<QName> command);

    void focus();
}
