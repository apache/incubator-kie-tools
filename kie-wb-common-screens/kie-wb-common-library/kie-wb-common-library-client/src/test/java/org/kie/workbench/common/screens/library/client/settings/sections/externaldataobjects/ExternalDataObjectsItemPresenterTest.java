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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        final Import import_ = new Import("type");
        externalDataObjectsItemPresenter.setup(import_, mock(ExternalDataObjectsPresenter.class));

        verify(view).init(eq(externalDataObjectsItemPresenter));
        verify(view).setTypeName(eq("type"));
        assertEquals(import_, externalDataObjectsItemPresenter.getObject());
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
}