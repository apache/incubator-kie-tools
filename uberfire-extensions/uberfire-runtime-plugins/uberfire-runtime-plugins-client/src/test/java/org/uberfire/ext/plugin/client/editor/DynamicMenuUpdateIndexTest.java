package org.uberfire.ext.plugin.client.editor;

import org.junit.Test;
import org.uberfire.ext.plugin.model.DynamicMenuItem;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class DynamicMenuUpdateIndexTest {

    private DynamicMenuEditorPresenter presenter;
    private DynamicMenuItem firstMenuItem;
    private DynamicMenuItem secondMenuItem;

    @Test
    public void validateItemIndexes() {
        presenter = createDynamicMenuEditorPresenter( mock( DynamicMenuEditorPresenter.View.class ) );

        firstMenuItem = new DynamicMenuItem( "firstId", "firstLabel" );
        secondMenuItem = new DynamicMenuItem( "secondId", "secondLabel" );

        presenter.addMenuItem( firstMenuItem );
        presenter.addMenuItem( secondMenuItem );

        presenter.updateIndex( firstMenuItem, 0, DynamicMenuEditorPresenter.UpdateIndexOperation.UP );
        checkMenuItemIndexes( 0, 1 );

        presenter.updateIndex( secondMenuItem, 1, DynamicMenuEditorPresenter.UpdateIndexOperation.DOWN );
        checkMenuItemIndexes( 0, 1 );

        presenter.updateIndex( firstMenuItem, 0, DynamicMenuEditorPresenter.UpdateIndexOperation.DOWN );
        checkMenuItemIndexes( 1, 0 );

        presenter.updateIndex( firstMenuItem, 1, DynamicMenuEditorPresenter.UpdateIndexOperation.UP );
        checkMenuItemIndexes( 0, 1) ;
    }

    private DynamicMenuEditorPresenter createDynamicMenuEditorPresenter(DynamicMenuEditorPresenter.View view) {

        return new DynamicMenuEditorPresenter( view ) {

            private List<DynamicMenuItem> dynamicMenuItems = new ArrayList<DynamicMenuItem>();

            @Override
            public List<DynamicMenuItem> getDynamicMenuItems() {
                return dynamicMenuItems;
            }

            @Override
            public void addMenuItem( final DynamicMenuItem menuItem ) {
                dynamicMenuItems.add( menuItem );
            }
        };
    }

    private void checkMenuItemIndexes( int firstMenuItemIndex, int secondMenuItemIndex ) {
        assertEquals( firstMenuItemIndex, presenter.getDynamicMenuItems().indexOf( firstMenuItem ) );
        assertEquals( secondMenuItemIndex, presenter.getDynamicMenuItems().indexOf( secondMenuItem ) );
    }
}
