package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.packages;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.KnowledgeBaseItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases.item.KnowledgeBaseItemPresenter.PackageListPresenter;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PackageItemPresenterTest {

    private PackageItemPresenter packageItemPresenter;

    @Mock
    private PackageItemPresenter.View view;

    @Before
    public void before() {
        packageItemPresenter = spy(new PackageItemPresenter(view));
    }

    @Test
    public void testSetup() {
        packageItemPresenter.setup("Name", mock(KnowledgeBaseItemPresenter.class));
        verify(view).init(eq(packageItemPresenter));
        verify(view).setName(eq("Name"));
    }

    @Test
    public void testRemove() {
        final KnowledgeBaseItemPresenter parentPresenter = mock(KnowledgeBaseItemPresenter.class);
        final PackageListPresenter listPresenter = mock(PackageListPresenter.class);

        packageItemPresenter.parentPresenter = parentPresenter;
        packageItemPresenter.setListPresenter(listPresenter);

        packageItemPresenter.remove();

        verify(listPresenter).remove(eq(packageItemPresenter));
        verify(parentPresenter).fireChangeEvent();
    }
}