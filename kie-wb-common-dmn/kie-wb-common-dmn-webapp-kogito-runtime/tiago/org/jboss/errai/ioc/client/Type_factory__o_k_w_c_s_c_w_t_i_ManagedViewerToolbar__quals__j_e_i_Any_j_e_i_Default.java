package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedToolbarDelegate;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedViewerToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ViewerToolbar;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;

public class Type_factory__o_k_w_c_s_c_w_t_i_ManagedViewerToolbar__quals__j_e_i_Any_j_e_i_Default extends Factory<ManagedViewerToolbar> { public Type_factory__o_k_w_c_s_c_w_t_i_ManagedViewerToolbar__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ManagedViewerToolbar.class, "Type_factory__o_k_w_c_s_c_w_t_i_ManagedViewerToolbar__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ManagedViewerToolbar.class, ManagedToolbarDelegate.class, Object.class, Toolbar.class, ViewerToolbar.class });
  }

  public ManagedViewerToolbar createInstance(final ContextManager contextManager) {
    final ManagedToolbar<ViewerSession> _toolbar_0 = (ManagedToolbar) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_t_i_ManagedToolbar__quals__j_e_i_Any_j_e_i_Default");
    final ManagedViewerToolbar instance = new ManagedViewerToolbar(_toolbar_0);
    registerDependentScopedReference(instance, _toolbar_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ManagedViewerToolbar instance) {
    instance.init();
  }
}