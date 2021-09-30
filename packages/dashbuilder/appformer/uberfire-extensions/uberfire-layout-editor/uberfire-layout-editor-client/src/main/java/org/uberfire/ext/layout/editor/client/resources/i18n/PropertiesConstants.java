/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.layout.editor.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface PropertiesConstants
        extends
        Messages {

    public static final PropertiesConstants INSTANCE = GWT.create(PropertiesConstants.class);

    String elementLabel();

    String propertiesLabel();

    String clearAll();

    String layoutElementPosition(String parentDisplay, String name, String index);

    String layoutElementName(String name, String index);

    String layoutElementTypePage();

    String layoutElementTypeRow();

    String layoutElementTypeColumn();

    String layoutElementTypeComponent();

    String panel();

    String text();

    String width();

    String height();

    String background_color();

    String margin();

    String margin_top();

    String margin_bottom();

    String margin_left();

    String margin_right();

    String padding();

    String padding_top();

    String padding_bottom();

    String padding_left();

    String padding_right();

    String text_align();

    String text_decoration();

    String color();

    String font_size();

    String font_weight();

    String length_help();

    String float_property();

}