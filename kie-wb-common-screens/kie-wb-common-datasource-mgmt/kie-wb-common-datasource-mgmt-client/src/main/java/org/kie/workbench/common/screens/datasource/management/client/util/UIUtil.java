/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.client.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.uberfire.ext.widgets.common.client.common.StyleHelper;
import org.uberfire.ext.widgets.common.client.common.popups.YesNoCancelPopup;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

public class UIUtil {

    public static void setGroupOnError( final Element formGroup, final boolean onError ) {
        StyleHelper.addUniqueEnumStyleName( formGroup, ValidationState.class,
                onError ? ValidationState.ERROR : ValidationState.NONE );
    }

    public static void setSpanMessage( final Element span, final String text ) {
        span.getStyle().setVisibility( Style.Visibility.VISIBLE );
        span.setInnerHTML( text );
    }

    public static void clearSpanMessage( final Element span ) {
        span.getStyle().setVisibility( Style.Visibility.HIDDEN );
        span.setInnerHTML( "" );
    }
}
