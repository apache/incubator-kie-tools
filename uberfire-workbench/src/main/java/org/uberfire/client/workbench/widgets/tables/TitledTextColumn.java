package org.uberfire.client.workbench.widgets.tables;

import com.google.gwt.user.cellview.client.Column;

/**
 * A column containing TitleText cells
 * @param <T>
 */
public abstract class TitledTextColumn<T> extends Column<T, TitledTextCell.TitledText> {

    /**
     * Construct a new TitledTextColumn.
     */
    public TitledTextColumn() {
        super( new TitledTextCell() );
    }
}
