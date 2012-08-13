package org.drools.guvnor.client.widgets.tables;

import org.drools.guvnor.client.widgets.tables.TitledTextCell.TitledText;

import com.google.gwt.user.cellview.client.Column;

/**
 * A column containing TitleText cells
 * 
 *
 * @param <T>
 */
public abstract class TitledTextColumn<T> extends Column<T, TitledText> {

    /**
     * Construct a new TitledTextColumn.
     */
    public TitledTextColumn() {
        super( new TitledTextCell() );
    }
}
