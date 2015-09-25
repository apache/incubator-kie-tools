package org.kie.workbench.common.widgets.metadata.client.widget;

import static org.junit.Assert.assertEquals;

import org.gwtbootstrap3.client.ui.FormControlStatic;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.metadata.client.resources.i18n.MetadataConstants;
import org.mockito.InjectMocks;
import org.uberfire.backend.vfs.impl.LockInfo;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class MetadataWidgetTest {
    
    @InjectMocks
    private MetadataWidget metadataWidget;
    
    @GwtMock
    private FormControlStatic lockedBy;
    
    @Before
    public void setup() {
        metadataWidget.setCurrentUser( "Christian" );
    }
    
    @Test
    public void lockStatusTextNotLocked() {
        final String expected = MetadataConstants.INSTANCE.UnlockedHint();
        final String lockStatusText = metadataWidget.getLockStatusText(new LockInfo(false, "", null));
        assertEquals("Lock status text incorrect", expected, lockStatusText);
    }
    
    @Test
    public void lockStatusTextLockedByOtherUser() {
        final String lockedBy = "Michael";
        final String expected = MetadataConstants.INSTANCE.LockedByHint() + " " + lockedBy;
        final String lockStatusText = metadataWidget.getLockStatusText(new LockInfo(true, lockedBy, null));
        assertEquals("Lock status text incorrect", expected, lockStatusText);
    }
    
    @Test
    public void lockStatusTextLockedByCurrentUser() {
        final String lockedBy = "Christian";
        final String expected = MetadataConstants.INSTANCE.LockedByHintOwned();
        final String lockStatusText = metadataWidget.getLockStatusText(new LockInfo(true, lockedBy, null));
        assertEquals("Lock status text incorrect", expected, lockStatusText);
    }

}
