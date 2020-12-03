package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.toolbar.Toolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.EditorToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedEditorToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedToolbar;
import org.kie.workbench.common.stunner.client.widgets.toolbar.impl.ManagedToolbarDelegate;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;

public class Type_factory__o_k_w_c_s_c_w_t_i_ManagedEditorToolbar__quals__j_e_i_Any_j_e_i_Default extends Factory<ManagedEditorToolbar> { public Type_factory__o_k_w_c_s_c_w_t_i_ManagedEditorToolbar__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ManagedEditorToolbar.class, "Type_factory__o_k_w_c_s_c_w_t_i_ManagedEditorToolbar__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ManagedEditorToolbar.class, ManagedToolbarDelegate.class, Object.class, Toolbar.class, EditorToolbar.class });
  }

  public ManagedEditorToolbar createInstance(final ContextManager contextManager) {
    final ManagedToolbar<EditorSession> _toolbar_0 = (ManagedToolbar) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_t_i_ManagedToolbar__quals__j_e_i_Any_j_e_i_Default");
    final ManagedEditorToolbar instance = new ManagedEditorToolbar(_toolbar_0);
    registerDependentScopedReference(instance, _toolbar_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ManagedEditorToolbar instance) {
    instance.init();
  }
}