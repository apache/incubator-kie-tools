package org.jboss.errai.ioc.client;

import org.guvnor.messageconsole.client.console.AlertsEntryPoint;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_g_m_c_c_AlertsEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<AlertsEntryPoint> { public Type_factory__o_g_m_c_c_AlertsEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AlertsEntryPoint.class, "Type_factory__o_g_m_c_c_AlertsEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { AlertsEntryPoint.class, Object.class });
  }

  public AlertsEntryPoint createInstance(final ContextManager contextManager) {
    final AlertsEntryPoint instance = new AlertsEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}