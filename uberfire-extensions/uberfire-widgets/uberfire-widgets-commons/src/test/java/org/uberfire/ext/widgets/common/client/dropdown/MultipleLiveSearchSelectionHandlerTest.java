package org.uberfire.ext.widgets.common.client.dropdown;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class MultipleLiveSearchSelectionHandlerTest {

    private String SELECTED_ITEM_TEXT = "selectedItem";

    @GwtMock
    private CommonConstants constants;

    @Mock
    private Command callback;

    @Spy
    private MultipleLiveSearchSelectionHandler<String> handler = new MultipleLiveSearchSelectionHandler<>();

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
        verify(item, times(1)).setMultipleSelection(true);

        checkHandlerSelectedValue(null);

        assertEquals(null, handler.getDropDownMenuHeader());
    }

    @Test
    public void testRegisterIItemWithSelectedValue() {
        LiveSearchSelectorItem selectedItem = doTestSelectItem();

        LiveSearchSelectorItem secondItem = createItem(SELECTED_ITEM_TEXT);

        handler.registerItem(secondItem);

        verify(selectedItem, atLeast(1)).getKey();
        verify(secondItem, times(2)).getKey();
        verify(secondItem).select();

        checkHandlerSelectedValue(SELECTED_ITEM_TEXT);

        assertEquals(SELECTED_ITEM_TEXT, handler.getDropDownMenuHeader());
    }

    @Test
    public void testSelectItem() {
        doTestSelectItem();

        assertEquals(1, handler.getSelectedValues().size());

        assertEquals(SELECTED_ITEM_TEXT, handler.getDropDownMenuHeader());
    }

    @Test
    public void testSelectMultipleItems() {

        // Setting max title elements to 3
        handler.setMaxDropDownTextItems(3);

        // Register Items
        LiveSearchSelectorItem firstItem = createItem("a");
        handler.registerItem(firstItem);
        LiveSearchSelectorItem secondItem = createItem("b");
        handler.registerItem(secondItem);
        LiveSearchSelectorItem thirdItem = createItem("c");
        handler.registerItem(thirdItem);
        LiveSearchSelectorItem fourthItem = createItem("d");
        handler.registerItem(fourthItem);

        // Selecting values

        // Selecting "a"
        handler.selectKey("a");
        verify(handler, times(1)).selectItem(firstItem);
        verify(firstItem).select();
        verify(callback).execute();
        checkHandlerSelectedValue("a");
        assertEquals(1, handler.getSelectedValues().size());
        assertEquals("a", handler.getDropDownMenuHeader());

        // Selecting "a" & "b"
        handler.selectKey("b");
        verify(handler, times(1)).selectItem(secondItem);
        verify(secondItem).select();
        verify(callback, times(2)).execute();
        checkHandlerSelectedValue("b");
        assertEquals(2, handler.getSelectedValues().size());
        assertEquals("a & b", handler.getDropDownMenuHeader());

        // Selecting "a", "b" & "c"
        handler.selectKey("c");
        verify(handler, times(1)).selectItem(thirdItem);
        verify(thirdItem).select();
        verify(callback, times(3)).execute();
        checkHandlerSelectedValue("c");
        assertEquals(3, handler.getSelectedValues().size());
        assertEquals("a, b & c", handler.getDropDownMenuHeader());

        // Selectiong "a", "b", "c" & "d"
        handler.selectKey("d");
        verify(handler, times(1)).selectItem(fourthItem);
        verify(fourthItem).select();
        verify(callback, times(4)).execute();
        checkHandlerSelectedValue("d");
        assertEquals(4, handler.getSelectedValues().size());
        assertEquals("liveSearchElementsSelected(4)", handler.getDropDownMenuHeader());

        // Deselecting values

        // Deselect "a"
        handler.selectKey("a");
        verify(handler, times(2)).selectItem(firstItem);
        verify(firstItem).reset();
        verify(callback, times(5)).execute();
        assertEquals(3, handler.getSelectedValues().size());
        assertEquals("b, c & d", handler.getDropDownMenuHeader());

        // Deselect "b"
        handler.selectKey("b");
        verify(handler, times(2)).selectItem(secondItem);
        verify(secondItem).reset();
        verify(callback, times(6)).execute();
        assertEquals(2, handler.getSelectedValues().size());
        assertEquals("c & d", handler.getDropDownMenuHeader());

        // Deselect "c"
        handler.selectKey("c");
        verify(handler, times(2)).selectItem(thirdItem);
        verify(thirdItem).reset();
        verify(callback, times(7)).execute();
        assertEquals(1, handler.getSelectedValues().size());
        assertEquals("d", handler.getDropDownMenuHeader());

        // Deselect "d"
        handler.selectKey("d");
        verify(handler, times(2)).selectItem(fourthItem);
        verify(fourthItem).reset();
        verify(callback, times(8)).execute();
        assertEquals(0, handler.getSelectedValues().size());
        assertEquals(null, handler.getDropDownMenuHeader());
    }

    @Test
    public void testClearSelection() {
        // Register Items
        LiveSearchSelectorItem firstItem = createItem("a");
        handler.registerItem(firstItem);
        LiveSearchSelectorItem secondItem = createItem("b");
        handler.registerItem(secondItem);
        LiveSearchSelectorItem thirdItem = createItem("c");
        handler.registerItem(thirdItem);
        LiveSearchSelectorItem fourthItem = createItem("d");
        handler.registerItem(fourthItem);

        handler.selectKey("a");
        handler.selectKey("b");
        handler.selectKey("c");
        handler.selectKey("d");

        assertEquals(4, handler.getSelectedValues().size());
        assertEquals("a, b, c & d", handler.getDropDownMenuHeader());

        handler.clearSelection();

        assertEquals(0, handler.getSelectedValues().size());
        assertEquals(null, handler.getDropDownMenuHeader());
    }

    protected LiveSearchSelectorItem doTestSelectItem() {
        LiveSearchSelectorItem item = createItem(SELECTED_ITEM_TEXT);

        handler.registerItem(item);

        handler.selectItem(item);

        verify(item).select();

        checkHandlerSelectedValue(SELECTED_ITEM_TEXT);

        assertEquals(SELECTED_ITEM_TEXT, handler.getDropDownMenuHeader());

        verify(callback).execute();

        return item;
    }

    private LiveSearchSelectorItem createItem(String value) {
        LiveSearchSelectorItem item = mock(LiveSearchSelectorItem.class);

        when(item.getKey()).thenReturn(value);
        when(item.getValue()).thenReturn(value);

        return item;
    }

    private void checkHandlerSelectedValue(String value) {
        if (value != null) {
            assertTrue(handler.getSelectedValues().contains(value));
        } else {
            assertTrue(handler.getSelectedValues().isEmpty());
        }
    }
}
