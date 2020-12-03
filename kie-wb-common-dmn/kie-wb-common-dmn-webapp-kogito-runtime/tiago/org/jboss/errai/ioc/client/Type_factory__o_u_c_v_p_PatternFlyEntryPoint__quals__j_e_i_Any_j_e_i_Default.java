package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.views.pfly.PatternFlyEntryPoint;

public class Type_factory__o_u_c_v_p_PatternFlyEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<PatternFlyEntryPoint> { public Type_factory__o_u_c_v_p_PatternFlyEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PatternFlyEntryPoint.class, "Type_factory__o_u_c_v_p_PatternFlyEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { PatternFlyEntryPoint.class, Object.class });
  }

  public PatternFlyEntryPoint createInstance(final ContextManager contextManager) {
    final PatternFlyEntryPoint instance = new PatternFlyEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final PatternFlyEntryPoint instance) {
    instance.init();
  }
}