package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesEntryPoint;

public class Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerPreferencesEntryPoint> { public Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StunnerPreferencesEntryPoint.class, "Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { StunnerPreferencesEntryPoint.class, Object.class });
  }

  public StunnerPreferencesEntryPoint createInstance(final ContextManager contextManager) {
    final StunnerPreferencesEntryPoint instance = new StunnerPreferencesEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}