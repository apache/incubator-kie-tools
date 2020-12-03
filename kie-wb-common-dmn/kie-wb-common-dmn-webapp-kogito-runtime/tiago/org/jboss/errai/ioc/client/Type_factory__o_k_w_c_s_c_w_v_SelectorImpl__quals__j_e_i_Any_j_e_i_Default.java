package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.views.Selector;
import org.kie.workbench.common.stunner.client.widgets.views.SelectorImpl;
import org.kie.workbench.common.stunner.client.widgets.views.SelectorView;

public class Type_factory__o_k_w_c_s_c_w_v_SelectorImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<SelectorImpl> { public Type_factory__o_k_w_c_s_c_w_v_SelectorImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SelectorImpl.class, "Type_factory__o_k_w_c_s_c_w_v_SelectorImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SelectorImpl.class, Object.class, Selector.class });
  }

  public SelectorImpl createInstance(final ContextManager contextManager) {
    final SelectorView _view_0 = (SelectorView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_v_SelectorView__quals__j_e_i_Any_j_e_i_Default");
    final SelectorImpl instance = new SelectorImpl(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SelectorImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final SelectorImpl instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final SelectorImpl instance) {
    instance.init();
  }
}