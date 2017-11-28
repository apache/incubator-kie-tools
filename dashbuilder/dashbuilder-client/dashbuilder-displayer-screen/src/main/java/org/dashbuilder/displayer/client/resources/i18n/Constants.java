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
package org.dashbuilder.displayer.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface Constants extends Messages {

    Constants INSTANCE = GWT.create(Constants.class);

    String menu_button_actions();

    String menu_edit();

    String menu_clone();

    String menu_export_csv();

    String menu_export_excel();

    String displayer_presenter_displayer_notfound();

    String displayer_presenter_export_large_dataset();

    String displayer_presenter_export_no_data();

}
