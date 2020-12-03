package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerTextPreferences;

public class Type_factory__o_k_w_c_s_c_c_p_StunnerTextPreferences__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerTextPreferences> { public Type_factory__o_k_w_c_s_c_c_p_StunnerTextPreferences__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StunnerTextPreferences.class, "Type_factory__o_k_w_c_s_c_c_p_StunnerTextPreferences__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { StunnerTextPreferences.class, Object.class });
  }

  public StunnerTextPreferences createInstance(final ContextManager contextManager) {
    final StunnerTextPreferences instance = new StunnerTextPreferences();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}