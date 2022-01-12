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

package org.uberfire.ext.widgets.common.client.dropdown;

import org.jboss.errai.common.client.api.IsElement;
import org.uberfire.mvp.Command;

public interface LiveSearchSelectorItem<TYPE> extends IsElement{

    void init(TYPE key, String value);

    TYPE getKey();

    String getValue();

    void reset();

    void setMultipleSelection(boolean enable);

    void onItemClick();

    void select();

    void setSelectionCallback(Command selectionCallback);
}
