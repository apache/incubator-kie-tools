package org.jboss.errai.ioc.client;

import org.guvnor.common.services.project.client.ProjectEntryPoint;
import org.guvnor.common.services.project.client.preferences.ProjectScopedResolutionStrategySupplier;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.preferences.client.admin.page.AdminPage;
import org.uberfire.ext.preferences.client.admin.page.AdminPageImpl;

public class Type_factory__o_g_c_s_p_c_ProjectEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<ProjectEntryPoint> { public Type_factory__o_g_c_s_p_c_ProjectEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ProjectEntryPoint.class, "Type_factory__o_g_c_s_p_c_ProjectEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { ProjectEntryPoint.class, Object.class });
  }

  public ProjectEntryPoint createInstance(final ContextManager contextManager) {
    final AdminPage _adminPage_0 = (AdminPageImpl) contextManager.getInstance("Type_factory__o_u_e_p_c_a_p_AdminPageImpl__quals__j_e_i_Any_j_e_i_Default");
    final ProjectScopedResolutionStrategySupplier _projectScopedResolutionStrategySupplier_1 = (ProjectScopedResolutionStrategySupplier) contextManager.getInstance("Type_factory__o_g_c_s_p_c_p_ProjectScopedResolutionStrategySupplier__quals__j_e_i_Any_j_e_i_Default");
    final ProjectEntryPoint instance = new ProjectEntryPoint(_adminPage_0, _projectScopedResolutionStrategySupplier_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ProjectEntryPoint instance) {
    instance.startApp();
  }
}