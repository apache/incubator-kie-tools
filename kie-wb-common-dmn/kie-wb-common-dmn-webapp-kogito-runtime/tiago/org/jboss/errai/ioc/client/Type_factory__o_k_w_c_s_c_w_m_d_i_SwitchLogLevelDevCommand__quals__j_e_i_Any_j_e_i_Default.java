package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.AbstractMenuDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.MenuDevCommand;
import org.kie.workbench.common.stunner.client.widgets.menu.dev.impl.SwitchLogLevelDevCommand;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;

public class Type_factory__o_k_w_c_s_c_w_m_d_i_SwitchLogLevelDevCommand__quals__j_e_i_Any_j_e_i_Default extends Factory<SwitchLogLevelDevCommand> { public Type_factory__o_k_w_c_s_c_w_m_d_i_SwitchLogLevelDevCommand__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SwitchLogLevelDevCommand.class, "Type_factory__o_k_w_c_s_c_w_m_d_i_SwitchLogLevelDevCommand__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SwitchLogLevelDevCommand.class, AbstractMenuDevCommand.class, Object.class, MenuDevCommand.class });
  }

  public SwitchLogLevelDevCommand createInstance(final ContextManager contextManager) {
    final SessionManager _sessionManager_0 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final SwitchLogLevelDevCommand instance = new SwitchLogLevelDevCommand(_sessionManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}