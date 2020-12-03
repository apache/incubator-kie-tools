package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.guvnor.common.services.project.client.POMEditorPanel;
import org.guvnor.common.services.project.client.POMEditorPanelView;
import org.guvnor.common.services.project.client.POMEditorPanelView.Presenter;
import org.guvnor.common.services.project.client.POMEditorPanelViewImpl;
import org.guvnor.common.services.project.client.preferences.ProjectScopedResolutionStrategySupplier;
import org.guvnor.common.services.project.preferences.GAVPreferences;
import org.guvnor.common.services.project.preferences.GAVPreferencesBeanGeneratedImpl;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

public class Type_factory__o_g_c_s_p_c_POMEditorPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<POMEditorPanel> { public Type_factory__o_g_c_s_p_c_POMEditorPanel__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(POMEditorPanel.class, "Type_factory__o_g_c_s_p_c_POMEditorPanel__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { POMEditorPanel.class, Object.class, Presenter.class, IsWidget.class });
  }

  public POMEditorPanel createInstance(final ContextManager contextManager) {
    final GAVPreferences _gavPreferences_2 = (GAVPreferencesBeanGeneratedImpl) contextManager.getInstance("Type_factory__o_g_c_s_p_p_GAVPreferencesBeanGeneratedImpl__quals__j_e_i_Any_j_e_i_Default");
    final SyncBeanManager _iocManager_1 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final POMEditorPanelView _view_0 = (POMEditorPanelViewImpl) contextManager.getInstance("Type_factory__o_g_c_s_p_c_POMEditorPanelViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final ProjectScopedResolutionStrategySupplier _projectScopedResolutionStrategySupplier_3 = (ProjectScopedResolutionStrategySupplier) contextManager.getInstance("Type_factory__o_g_c_s_p_c_p_ProjectScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default");
    final POMEditorPanel instance = new POMEditorPanel(_view_0, _iocManager_1, _gavPreferences_2, _projectScopedResolutionStrategySupplier_3);
    registerDependentScopedReference(instance, _gavPreferences_2);
    registerDependentScopedReference(instance, _iocManager_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}