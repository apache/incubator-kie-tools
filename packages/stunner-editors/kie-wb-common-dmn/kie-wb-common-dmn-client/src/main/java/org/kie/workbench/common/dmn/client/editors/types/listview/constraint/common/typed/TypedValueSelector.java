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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed;

import java.util.function.Consumer;

import com.google.gwt.event.dom.client.BlurEvent;
import elemental2.dom.Element;
import elemental2.dom.Event;

public interface TypedValueSelector {

    String getValue();

    void setValue(final String value);

    void setPlaceholder(final String placeholder);

    Element getElement();

    void setOnInputChangeCallback(final Consumer<Event> onValueChanged);

    void setOnInputBlurCallback(final Consumer<BlurEvent> onValueInputBlur);

    void select();

    String toDisplay(final String rawValue);
}
