/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.layout.editor.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface CssAllowedValueConstants extends ConstantsWithLookup {

    public static final CssAllowedValueConstants INSTANCE = GWT.create(CssAllowedValueConstants.class);

    String TEXT_ALIGN__LEFT();

    String TEXT_ALIGN__CENTER();

    String TEXT_ALIGN__RIGHT();

    String FONT_SIZE__XX_SMALL();

    String FONT_SIZE__X_SMALL();

    String FONT_SIZE__SMALL();

    String FONT_SIZE__MEDIUM();

    String FONT_SIZE__LARGE();

    String FONT_SIZE__X_LARGE();

    String FONT_SIZE__XX_LARGE();

    String FONT_WEIGHT__NORMAL();

    String FONT_WEIGHT__BOLD();

    String FONT_WEIGHT__BOLDER();

    String FONT_WEIGHT__LIGHTER();

    String TEXT_DECORATION__NONE();

    String TEXT_DECORATION__UNDERLINE();

    String TEXT_DECORATION__OVERLINE();

    String TEXT_DECORATION__LINE_THROUGH();

}
