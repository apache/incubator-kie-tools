package org.uberfire.ext.layout.editor.client.infra;

import org.jboss.errai.common.client.dom.HTMLElement;

public class CSSClassNameHelper {

    public static void addClassName( HTMLElement element, String cssClass ) {
        if ( !hasClassName( element, cssClass ) ) {
            String className = element.getClassName();
            if ( !className.isEmpty() ) {
                className += " ";
            }
            className += cssClass;
            element.setClassName( className );
        }
    }

    public static void removeClassName( HTMLElement element, String cssClass ) {
        if ( hasClassName( element, cssClass ) ) {
            String[] classes = element.getClassName().split( "\\s+" );
            String newClass = "";
            for ( String aClass : classes ) {
                if ( !aClass.equals( cssClass ) ) {
                    if ( !newClass.isEmpty() ) {
                        newClass += " ";
                    }
                    newClass += aClass;
                }
            }
            element.setClassName( newClass );
        }
    }

    public static boolean hasClassName( HTMLElement element, String cssClass ) {
        String[] classes = element.getClassName().split( "\\s+" );
        for ( String aClass : classes ) {
            if ( aClass.equals( cssClass ) ) {
                return true;
            }
        }
        return false;
    }

}
