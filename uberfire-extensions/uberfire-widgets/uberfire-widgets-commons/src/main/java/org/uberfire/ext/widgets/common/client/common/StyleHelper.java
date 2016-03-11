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

package org.uberfire.ext.widgets.common.client.common;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

/**
 * Borrow from GWTBootstrap3 (https://github.com/gwtbootstrap3/gwtbootstrap3)
 */
public final class StyleHelper {

    public static <E extends Style.HasCssName, F extends Enum<? extends Style.HasCssName>> void addUniqueEnumStyleName( final Element element,
                                                                                                                        final Class<F> enumClass,
                                                                                                                        final E style ) {
        removeEnumStyleNames( element, enumClass );
        addEnumStyleName( element, style );
    }

    public static <E extends Enum<? extends Style.HasCssName>> void removeEnumStyleNames( final Element element,
                                                                                          final Class<E> enumClass ) {

        for ( final Enum<? extends Style.HasCssName> constant : enumClass.getEnumConstants() ) {
            final String cssClass = ( (Style.HasCssName) constant ).getCssName();

            if ( cssClass != null && !cssClass.isEmpty() ) {
                element.removeClassName( cssClass );
            }
        }
    }

    public static <E extends Style.HasCssName> void addEnumStyleName( final Element element,
                                                                      final E style ) {

        if ( style != null && style.getCssName() != null && !style.getCssName().isEmpty() ) {
            element.addClassName( style.getCssName() );
        }
    }

    public static <E extends Style.HasCssName> void removeEnumStyleName( final Element element,
                                                                         final E style ) {

        if ( style != null && style.getCssName() != null && !style.getCssName().isEmpty() ) {
            element.removeClassName( style.getCssName() );
        }
    }

}
