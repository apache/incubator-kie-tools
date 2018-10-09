package org.kie.workbench.common.screens.library.client.settings.sections.knowledgebases;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.settings.SettingsSectionChange;
import org.kie.workbench.common.screens.library.client.settings.util.sections.MenuItem;
import org.kie.workbench.common.screens.library.client.settings.util.modal.single.AddSingleValueModal;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KModuleModel;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KnowledgeBasesPresenterTest {

    private KnowledgeBasesPresenter knowledgeBasesPresenter;

    @Mock
    private Event<SettingsSectionChange<ProjectScreenModel>> settingsSectionChangeEvent;

    @Mock
    private KnowledgeBasesPresenter.View view;

    @Mock
    private MenuItem<ProjectScreenModel> menuItem;

    @Mock
    private KnowledgeBasesPresenter.KnowledgeBaseListPresenter knowledgeBaseListPresenter;

    private final Promises promises = new SyncPromises();

    @Before
    public void before() {
        knowledgeBasesPresenter = spy(new KnowledgeBasesPresenter(view,
                                                                  settingsSectionChangeEvent,
                                                                  promises,
                                                                  menuItem,
                                                                  knowledgeBaseListPresenter));
    }

    @Test
    public void testSetup() {
        final ProjectScreenModel model = mock(ProjectScreenModel.class);
        when(model.getKModule()).thenReturn(spy(new KModuleModel()));
        when(model.getKModule().getKBases()).thenReturn(emptyMap());

        knowledgeBasesPresenter.setup(model).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(view).init(eq(knowledgeBasesPresenter));
        verify(knowledgeBaseListPresenter).setup(any(), any(), any());
    }

    @Test
    public void testSave() {
        final KModuleModel kModuleModel = spy(new KModuleModel());
        knowledgeBasesPresenter.kModuleModel = kModuleModel;

        final Map<String, KBaseModel> kBasesMap = spy(new HashMap<>());
        doReturn(kBasesMap).when(kModuleModel).getKBases();

        knowledgeBasesPresenter.save("Test comment", null).catch_(i -> {
            Assert.fail("Promise should've been resolved!");
            return promises.resolve();
        });

        verify(kBasesMap).clear();
        verify(kBasesMap).putAll(any());
    }

    @Test
    public void testAddKnowledgeBase() {
        knowledgeBasesPresenter.addKnowledgeBase();
        final KBaseModel kBaseModel = new KBaseModel();
        kBaseModel.setName("");
        kBaseModel.setDefault(knowledgeBaseListPresenter.getObjectsList().isEmpty());
        verify(knowledgeBaseListPresenter).add(kBaseModel);
        verify(knowledgeBasesPresenter).fireChangeEvent();
    }

    @Test
    public void testNewKBaseModelEmptyMap() {
        doReturn(emptyList()).when(knowledgeBaseListPresenter).getObjectsList();

        final KBaseModel kBaseModel = knowledgeBasesPresenter.newKBaseModel("Name");

        Assert.assertEquals("Name", kBaseModel.getName());
        Assert.assertEquals(true, kBaseModel.isDefault());
    }

    @Test
    public void testNewKBaseModelNonEmptyMap() {
        doReturn(singletonList(new KBaseModel())).when(knowledgeBaseListPresenter).getObjectsList();

        final KBaseModel kBaseModel = knowledgeBasesPresenter.newKBaseModel("Name");

        Assert.assertEquals("Name", kBaseModel.getName());
        Assert.assertEquals(false, kBaseModel.isDefault());
    }
}