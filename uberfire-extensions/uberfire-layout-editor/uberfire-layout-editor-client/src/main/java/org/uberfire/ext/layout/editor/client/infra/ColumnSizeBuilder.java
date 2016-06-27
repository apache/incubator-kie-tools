package org.uberfire.ext.layout.editor.client.infra;

import org.gwtbootstrap3.client.ui.constants.ColumnSize;

public class ColumnSizeBuilder {

    public static String buildColumnSize( final int value ) {
        switch ( value ) {
            case 1:
                return buildSize( ColumnSize.MD_1 );
            case 2:
                return buildSize( ColumnSize.MD_2 );
            case 3:
                return buildSize( ColumnSize.MD_3 );
            case 4:
                return buildSize( ColumnSize.MD_4 );
            case 5:
                return buildSize( ColumnSize.MD_5 );
            case 6:
                return buildSize( ColumnSize.MD_6 );
            case 7:
                return buildSize( ColumnSize.MD_7 );
            case 8:
                return buildSize( ColumnSize.MD_8 );
            case 9:
                return buildSize( ColumnSize.MD_9 );
            case 10:
                return buildSize( ColumnSize.MD_10 );
            case 11:
                return buildSize( ColumnSize.MD_11 );
            case 12:
                return buildSize( ColumnSize.MD_12 );
            default:
                return buildSize( ColumnSize.MD_12 );
        }
    }

    private static String buildSize( ColumnSize mdSize ) {
        String size = mdSize + " " + ColumnSize.XS_12;
        if ( mdSize == ColumnSize.MD_4 ) {
            size += " " + ColumnSize.SM_6;
        } else if ( mdSize == ColumnSize.MD_5 ) {
            size += " " + ColumnSize.SM_6;
        } else if ( mdSize == ColumnSize.MD_6 ) {
            size += " " + ColumnSize.SM_6;
        } else {
            size += " " + ColumnSize.SM_12;
        }
        return size;
    }
}
