package com.ait.lienzo.client.core.shape.wires;

import java.util.function.Consumer;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class DefaultSelectionListenerTest {

    @Mock
    private Consumer<WiresShape> onSelectShape;

    @Mock
    private Consumer<WiresShape> onDeselectShape;

    @Mock
    private Consumer<WiresConnector> onSelectConnector;

    @Mock
    private Consumer<WiresConnector> onDeselectConnector;

    @Mock
    private SelectionManager selectionManager;

    @Mock
    private WiresShape selectedShape;

    @Mock
    private WiresShape deselectedShape;

    @Mock
    private WiresConnector selectedConnector;

    @Mock
    private WiresConnector deselectedConnector;

    private DefaultSelectionListener tested;

    private SelectionManager.SelectedItems selectedItems;

    @Before
    public void setUp() {
        Layer layer = new Layer();
        selectedItems = new SelectionManager.SelectedItems(selectionManager, layer);
        selectedItems.add(selectedShape);
        selectedItems.add(selectedConnector);
        selectedItems.setSelectionGroup(true);
        selectedItems.getChanged().getRemovedShapes().add(deselectedShape);
        selectedItems.getChanged().getRemovedConnectors().add(deselectedConnector);
        tested = new DefaultSelectionListener(onSelectShape,
                                              onDeselectShape,
                                              onSelectConnector,
                                              onDeselectConnector);
    }

    @Test
    public void testOnItemsChanged() {
        tested.onChanged(selectedItems);
        verify(onSelectShape, times(1)).accept(eq(selectedShape));
        verify(onDeselectShape, times(1)).accept(eq(deselectedShape));
        verify(onSelectConnector, times(1)).accept(eq(selectedConnector));
        verify(onDeselectConnector, times(1)).accept(eq(deselectedConnector));
        verify(selectedShape, times(1)).listen(eq(false));
        verify(deselectedShape, times(1)).listen(eq(true));
        verify(selectedConnector, times(1)).listen(eq(false));
        verify(deselectedConnector, times(1)).listen(eq(true));
    }
}
