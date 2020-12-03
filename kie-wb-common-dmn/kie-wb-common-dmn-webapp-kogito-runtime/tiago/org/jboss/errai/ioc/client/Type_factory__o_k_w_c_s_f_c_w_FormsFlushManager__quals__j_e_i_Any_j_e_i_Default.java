package org.jboss.errai.ioc.client;

import javax.inject.Singleton;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.forms.client.widgets.FormsFlushManager;

public class Type_factory__o_k_w_c_s_f_c_w_FormsFlushManager__quals__j_e_i_Any_j_e_i_Default extends Factory<FormsFlushManager> { public Type_factory__o_k_w_c_s_f_c_w_FormsFlushManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FormsFlushManager.class, "Type_factory__o_k_w_c_s_f_c_w_FormsFlushManager__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FormsFlushManager.class, Object.class });
  }

  public FormsFlushManager createInstance(final ContextManager contextManager) {
    final FormsFlushManager instance = new FormsFlushManager();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FormsFlushManager) instance, contextManager);
  }

  public void destroyInstanceHelper(final FormsFlushManager instance, final ContextManager contextManager) {
    instance.destroy();
  }
}