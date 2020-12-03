package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.preferences.client.PreferencesEntryPoint;

public class Type_factory__o_u_e_p_c_PreferencesEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<PreferencesEntryPoint> { public Type_factory__o_u_e_p_c_PreferencesEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PreferencesEntryPoint.class, "Type_factory__o_u_e_p_c_PreferencesEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { PreferencesEntryPoint.class, Object.class });
  }

  public PreferencesEntryPoint createInstance(final ContextManager contextManager) {
    final PreferencesEntryPoint instance = new PreferencesEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}