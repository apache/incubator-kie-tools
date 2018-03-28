package org.uberfire.ext.widgets.common.client.dropdown;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SingleLiveSearchSelectionHandlerTest {

    private String SELECTED_ITEM_TEXT = "selectedItem";

    @Spy
    private SingleLiveSearchSelectionHandler handler = new SingleLiveSearchSelectionHandler();

    @Mock
    private Command callback;

    @Before
    public void init() {
        handler.setLiveSearchSelectionCallback(callback);
    }

    @Test
    public void testRegisterItemWithoutSelectedValue() {
        LiveSearchSelectorItem item = createItem("any");

        handler.registerItem(item);

        verify(item, never()).select();
        verify(item, times(1)).setSelectionCallback(any());

        checkHandlerSelectedValue(null);
    }

    @Test
    public void testRegisterIItemWithSelectedValue() {
        LiveSearchSelectorItem selectedItem = doTestSelectItem();

        LiveSearchSelectorItem secondItem = createItem(SELECTED_ITEM_TEXT);

        handler.registerItem(secondItem);

        verify(selectedItem, atLeast(1)).getKey();
        verify(secondItem).getKey();
        verify(secondItem).select();
    }

    @Test
    public void testSelectItem() {
        doTestSelectItem();
    }

    protected LiveSearchSelectorItem doTestSelectItem() {
        LiveSearchSelectorItem item = createItem(SELECTED_ITEM_TEXT);

        handler.registerItem(item);

        handler.selectItem(item);

        verify(item).select();

        checkHandlerSelectedValue(SELECTED_ITEM_TEXT);

        verify(callback).execute();

        return item;
    }

    @Test
    public void testSelectSecondItem() {
        LiveSearchSelectorItem firstItem = doTestSelectItem();

        String secondSelected = SELECTED_ITEM_TEXT + "2";

        LiveSearchSelectorItem secondItem = createItem(secondSelected);

        handler.registerItem(secondItem);

        handler.selectItem(secondItem);

        verify(secondItem).select();
        verify(firstItem).reset();

        checkHandlerSelectedValue(secondSelected);
    }

    @Test
    public void testSelectKeyWithoutSelectedValue() {

        LiveSearchSelectorItem firstItem = createItem("a");

        handler.registerItem(firstItem);

        LiveSearchSelectorItem secondItem = createItem("b");

        handler.registerItem(secondItem);

        LiveSearchSelectorItem thirdItem = createItem(SELECTED_ITEM_TEXT);

        handler.registerItem(thirdItem);

        handler.selectKey("a");

        verify(handler).selectItem(firstItem);
        verify(firstItem).select();

        checkHandlerSelectedValue("a");

        handler.selectKey("b");

        verify(handler).selectItem(secondItem);
        verify(secondItem).select();
        verify(firstItem).reset();

        checkHandlerSelectedValue("b");

        handler.selectKey(SELECTED_ITEM_TEXT);
        verify(handler).selectItem(thirdItem);
        verify(thirdItem).select();
        verify(secondItem).reset();

        checkHandlerSelectedValue(SELECTED_ITEM_TEXT);
    }

    @Test
    public void testClearSelection() {
        LiveSearchSelectorItem item = doTestSelectItem();

        handler.clearSelection();

        verify(item).reset();

        checkHandlerSelectedValue(null);
    }

    private LiveSearchSelectorItem createItem(String value) {
        LiveSearchSelectorItem item = mock(LiveSearchSelectorItem.class);

        when(item.getKey()).thenReturn(value);
        when(item.getValue()).thenReturn(value);

        return item;
    }

    private void checkHandlerSelectedValue(String value) {
        assertEquals(value, handler.getSelectedKey());
        assertEquals(value, handler.getSelectedValue());
        assertEquals(value, handler.getDropDownMenuHeader());
    }
}
