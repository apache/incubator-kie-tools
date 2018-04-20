package org.kie.workbench.common.screens.library.client.settings.util.sections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.screens.library.client.settings.SettingsPresenterTest.newMockedSection;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class SectionManagerTest {

    private final Promises promises = new SyncPromises();

    private SectionManager<ProjectScreenModel> sectionManager;

    @Before
    public void before() {
        sectionManager = spy(new SectionManager<>(mock(MenuItemsListPresenter.class), promises, new Elemental2DomUtil()));
    }

    @Test
    public void testResetDirtyIndicator() {
        final Map<Section<ProjectScreenModel>, Integer> hashes = new HashMap<>();
        final Section<ProjectScreenModel> section = newMockedSection();

        doReturn(42).when(section).currentHashCode();

        sectionManager.originalHashCodes = hashes;
        sectionManager.resetDirtyIndicator(section);

        assertEquals((Integer) 42, hashes.get(section));
        verify(sectionManager).updateDirtyIndicator(eq(section));
    }

    @Test
    public void testUpdateDirtyIndicatorNonexistentSection() {
        final Section<ProjectScreenModel> section = newMockedSection();
        sectionManager.originalHashCodes = new HashMap<>();

        sectionManager.updateDirtyIndicator(section);

        verify(section).setDirty(false);
    }

    @Test
    public void testUpdateDirtyIndicatorExistentDirtySection() {
        final Section<ProjectScreenModel> section = newMockedSection();
        doReturn(42).when(section).currentHashCode();
        sectionManager.sections = new ArrayList<>(Arrays.asList(section));

        sectionManager.originalHashCodes = new HashMap<>();
        sectionManager.originalHashCodes.put(section, 32);

        sectionManager.updateDirtyIndicator(section);

        verify(section).setDirty(true);
    }

    @Test
    public void testUpdateDirtyIndicatorExistentNotDirtySection() {
        final Section<ProjectScreenModel> section = newMockedSection();
        doReturn(42).when(section).currentHashCode();
        sectionManager.sections = new ArrayList<>(Arrays.asList(section));

        sectionManager.originalHashCodes = new HashMap<>();
        sectionManager.originalHashCodes.put(section, 42);

        sectionManager.updateDirtyIndicator(section);

        verify(section).setDirty(false);
    }
}