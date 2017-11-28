/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.common.client;

import java.lang.String;

import com.google.gwt.user.client.ui.HTML;

public class SpacerWidget extends HTML {

    public static SpacerWidget SINGLE = new SpacerWidget( "&nbsp;" );
    public static SpacerWidget DOUBLE = new SpacerWidget( "&nbsp;&nbsp;" );

    private SpacerWidget( String spaces ) {
        super( spaces );
    }
}
