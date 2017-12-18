package org.dashbuilder.dataset.editor.client.screens;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.category.Others;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataSetDefTypeTest {

    private DataSetDefType tested;

    @Before
    public void setup() {
        tested = new DataSetDefType(new Others());
    }

    @Test
    public void testIt() {
        assertEquals("dataset", tested.getShortName());
        assertEquals("Data set", tested.getDescription());
        assertEquals(null, tested.getIcon());
        assertEquals("", tested.getPrefix());
        assertEquals("dset", tested.getSuffix());
        assertEquals(0, tested.getPriority());
        assertEquals("*.dset", tested.getSimpleWildcardPattern());
        final Path path = mock(Path.class);
        when(path.getFileName()).thenReturn("fff.dset");
        assertEquals(true, tested.accept(path));
        final Path path2 = mock(Path.class);
        when(path2.getFileName()).thenReturn("fff.mock");
        assertEquals(false, tested.accept(path2));
    }
}
