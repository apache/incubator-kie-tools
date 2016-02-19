package org.kie.workbench.common.screens.server.management.client.util;

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
