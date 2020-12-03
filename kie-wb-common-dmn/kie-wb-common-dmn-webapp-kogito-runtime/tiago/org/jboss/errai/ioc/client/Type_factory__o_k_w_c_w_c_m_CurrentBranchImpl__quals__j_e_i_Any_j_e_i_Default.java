package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.guvnor.common.services.project.client.context.WorkspaceProjectContext;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.menu.CurrentBranchImpl;
import org.uberfire.ext.editor.commons.version.CurrentBranch;

public class Type_factory__o_k_w_c_w_c_m_CurrentBranchImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CurrentBranchImpl> { public Type_factory__o_k_w_c_w_c_m_CurrentBranchImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CurrentBranchImpl.class, "Type_factory__o_k_w_c_w_c_m_CurrentBranchImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CurrentBranchImpl.class, Object.class, CurrentBranch.class });
  }

  public CurrentBranchImpl createInstance(final ContextManager contextManager) {
    final CurrentBranchImpl instance = new CurrentBranchImpl();
    setIncompleteInstance(instance);
    final WorkspaceProjectContext CurrentBranchImpl_projectContext = (WorkspaceProjectContext) contextManager.getInstance("Type_factory__o_g_c_s_p_c_c_WorkspaceProjectContext__quals__j_e_i_Any_j_e_i_Default");
    CurrentBranchImpl_WorkspaceProjectContext_projectContext(instance, CurrentBranchImpl_projectContext);
    setIncompleteInstance(null);
    return instance;
  }

  native static WorkspaceProjectContext CurrentBranchImpl_WorkspaceProjectContext_projectContext(CurrentBranchImpl instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.menu.CurrentBranchImpl::projectContext;
  }-*/;

  native static void CurrentBranchImpl_WorkspaceProjectContext_projectContext(CurrentBranchImpl instance, WorkspaceProjectContext value) /*-{
    instance.@org.kie.workbench.common.widgets.client.menu.CurrentBranchImpl::projectContext = value;
  }-*/;
}