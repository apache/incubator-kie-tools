/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.lienzo.toolbox.builder;

import org.kie.workbench.common.stunner.lienzo.toolbox.event.ToolboxButtonEventHandler;

public interface Button {

    Button setPadding( int padding );

    Button setIconSize( int iconSize );

    Button setClickHandler( ToolboxButtonEventHandler handler );

    Button setMouseDownHandler( ToolboxButtonEventHandler handler );

    Button setMouseEnterHandler( ToolboxButtonEventHandler handler );

    Button setMouseExitHandler( ToolboxButtonEventHandler handler );

    ButtonsOrRegister end();
}
