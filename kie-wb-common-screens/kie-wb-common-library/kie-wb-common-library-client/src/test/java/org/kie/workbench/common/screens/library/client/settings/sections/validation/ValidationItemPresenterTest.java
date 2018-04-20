package org.kie.workbench.common.screens.library.client.settings.sections.validation;

import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.MavenRepositorySource;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ValidationItemPresenterTest {

    private ValidationItemPresenter validationItemPresenter;

    @Mock
    public ValidationItemView view;

    @Before
    public void before() {
        validationItemPresenter = spy(new ValidationItemPresenter(view));
    }

    @Test
    public void testSetup() {

        final ModuleRepositories.ModuleRepository projectRepository = mock(ModuleRepositories.ModuleRepository.class);
        final ValidationPresenter parentPresenter = mock(ValidationPresenter.class);

        doReturn(new MavenRepositoryMetadata("test",
                                             "https://test.url",
                                             MavenRepositorySource.LOCAL))
                .when(projectRepository).getMetadata();

        validationItemPresenter.setup(projectRepository,
                                      parentPresenter);

        verify(view).init(eq(validationItemPresenter));
        verify(view).setInclude(anyBoolean());
        verify(view).setId(any());
        verify(view).setUrl(any());
        verify(view).setSource(any());
    }

    @Test
    public void testSetInclude() {

        final ModuleRepositories.ModuleRepository projectRepository = mock(ModuleRepositories.ModuleRepository.class);
        final ValidationPresenter parentPresenter = mock(ValidationPresenter.class);

        validationItemPresenter.projectRepository = projectRepository;
        validationItemPresenter.parentPresenter = parentPresenter;

        validationItemPresenter.setInclude(true);

        verify(projectRepository).setIncluded(eq(true));
        verify(parentPresenter).fireChangeEvent();
    }
}