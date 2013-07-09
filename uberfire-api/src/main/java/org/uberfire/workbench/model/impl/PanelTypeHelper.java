package org.uberfire.workbench.model.impl;

import org.uberfire.workbench.model.PanelType;

import static org.uberfire.workbench.model.PanelType.*;

public final class PanelTypeHelper {

    public static boolean isRoot( final PanelType type ) {
        return type.toString().startsWith( "ROOT_" );
    }

    public static PanelType getDefaultChildType( final PanelType type ) {
        switch ( type ) {
            case ROOT_TAB:
            case MULTI_TAB:
                return MULTI_TAB;
            case ROOT_LIST:
            case MULTI_LIST:
                return MULTI_LIST;
            case ROOT_SIMPLE:
            case SIMPLE:
                return SIMPLE;
            case SIMPLE_DND:
                return SIMPLE_DND;
            case ROOT_STATIC:
                return STATIC;
            case STATIC:
                return null;
            default:
                throw new IllegalArgumentException( "Unhandled PanelType. Expect subsequent errors." );
        }
    }
}
