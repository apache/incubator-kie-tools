/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.client.editor.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface Constants extends Messages {

    public static final Constants INSTANCE = GWT.create( Constants.class );

    String displayer_perspective_editor_title();

    String DisplayerComponent();

    String drag_group_name_reporting();

    String drag_component_name_barchart();

    String drag_component_name_piechart();

    String drag_component_name_areachart();

    String drag_component_name_linechart();

    String drag_component_name_bubblechart();

    String drag_component_name_meterchart();

    String drag_component_name_mapchart();

    String drag_component_name_metric();

    String drag_component_name_table();

    String drag_component_name_filter();

    String externalComponents();
}
