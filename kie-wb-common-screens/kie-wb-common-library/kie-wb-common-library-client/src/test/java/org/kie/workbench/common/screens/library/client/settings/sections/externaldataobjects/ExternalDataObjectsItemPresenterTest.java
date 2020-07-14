package org.kie.workbench.common.screens.library.client.settings.sections.externaldataobjects;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.imports.Import;
import org.kie.workbench.common.screens.library.client.settings.sections.externaldataobjects.ExternalDataObjectsPresenter.ImportsListPresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ExternalDataObjectsItemPresenterTest {

    private ExternalDataObjectsItemPresenter externalDataObjectsItemPresenter;

    @Mock
    private ExternalDataObjectsItemPresenter.View view;

    @Before
    public void before() {
        externalDataObjectsItemPresenter = spy(new ExternalDataObjectsItemPresenter(view));
    }

    @Test
    public void testSetup() {
        testSetupOfType("type");
        testSetupOfRemoveButton(true);
    }

    @Test
    public void testSetupWhenBuiltInJavaLangTypeIsImported() {
        testSetupOfType("java.lang.Number");
        testSetupOfRemoveButton(false);
    }

    @Test
    public void testSetupWhenBuiltInJavaUtilTypeIsImported() {
        testSetupOfType("java.util.List");
        testSetupOfRemoveButton(false);
    }

    @Test
    public void testTypeChange() {
        testTypeChange("NewType");
        testSetupOfRemoveButton(true);
    }

    @Test
    public void testTypeChangeWhenBuiltInJavaLangTypeIsImported() {
        testTypeChange("java.lang.Number");
        testSetupOfRemoveButton(false);
    }

    @Test
    public void testTypeChangeWhenBuiltInJavaUtilTypeIsImported() {
        testTypeChange("java.util.List");
        testSetupOfRemoveButton(false);
    }

    @Test
    public void testRemove() {
        final ExternalDataObjectsPresenter parentPresenter = mock(ExternalDataObjectsPresenter.class);
        final ImportsListPresenter listPresenter = mock(ImportsListPresenter.class);

        externalDataObjectsItemPresenter.parentPresenter = parentPresenter;
        externalDataObjectsItemPresenter.setListPresenter(listPresenter);

        externalDataObjectsItemPresenter.remove();

        verify(listPresenter).remove(eq(externalDataObjectsItemPresenter));
        verify(parentPresenter).fireChangeEvent();
    }

    private void testSetupOfType(final String type) {
        final Import import_ = new Import(type);
        externalDataObjectsItemPresenter.setup(import_, mock(ExternalDataObjectsPresenter.class));

        verify(view).init(eq(externalDataObjectsItemPresenter));
        verify(view).setTypeName(eq(type));
        assertEquals(import_, externalDataObjectsItemPresenter.getObject());
    }

    private void testTypeChange(final String newType) {
        final Import import_ = new Import("com.sample.OriginalType");
        externalDataObjectsItemPresenter.setup(import_, mock(ExternalDataObjectsPresenter.class));
        reset(view);

        externalDataObjectsItemPresenter.onTypeNameChange(newType);

        assertEquals(newType, externalDataObjectsItemPresenter.getObject().getType());
    }

    private void testSetupOfRemoveButton(final boolean visible) {
        if (visible) {
            verify(view).showRemoveButton();
            verify(view, never()).hideRemoveButton();
        } else {

            verify(view, never()).showRemoveButton();
            verify(view).hideRemoveButton();
        }
    }
}