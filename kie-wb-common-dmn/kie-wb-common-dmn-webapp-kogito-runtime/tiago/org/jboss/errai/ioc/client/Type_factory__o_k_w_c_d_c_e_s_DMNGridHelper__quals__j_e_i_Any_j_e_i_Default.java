package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.editors.search.DMNGridHelper;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;

public class Type_factory__o_k_w_c_d_c_e_s_DMNGridHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNGridHelper> { public Type_factory__o_k_w_c_d_c_e_s_DMNGridHelper__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNGridHelper.class, "Type_factory__o_k_w_c_d_c_e_s_DMNGridHelper__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNGridHelper.class, Object.class });
  }

  public DMNGridHelper createInstance(final ContextManager contextManager) {
    final SessionManager _sessionManager_0 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final DMNGridHelper instance = new DMNGridHelper(_sessionManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}