package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.views.session.EmptyStateView;

public class Type_factory__o_k_w_c_s_c_w_v_s_EmptyStateView__quals__j_e_i_Any_j_e_i_Default extends Factory<EmptyStateView> { public Type_factory__o_k_w_c_s_c_w_v_s_EmptyStateView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(EmptyStateView.class, "Type_factory__o_k_w_c_s_c_w_v_s_EmptyStateView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { EmptyStateView.class, Object.class });
  }

  public EmptyStateView createInstance(final ContextManager contextManager) {
    final EmptyStateView instance = new EmptyStateView();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((EmptyStateView) instance, contextManager);
  }

  public void destroyInstanceHelper(final EmptyStateView instance, final ContextManager contextManager) {
    instance.destroy();
  }
}