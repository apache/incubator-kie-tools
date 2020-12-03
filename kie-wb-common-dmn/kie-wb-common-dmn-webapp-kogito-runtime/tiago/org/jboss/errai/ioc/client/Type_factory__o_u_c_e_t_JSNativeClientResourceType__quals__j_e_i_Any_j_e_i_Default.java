package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.editor.type.JSNativeClientResourceType;

public class Type_factory__o_u_c_e_t_JSNativeClientResourceType__quals__j_e_i_Any_j_e_i_Default extends Factory<JSNativeClientResourceType> { public Type_factory__o_u_c_e_t_JSNativeClientResourceType__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(JSNativeClientResourceType.class, "Type_factory__o_u_c_e_t_JSNativeClientResourceType__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { JSNativeClientResourceType.class, Object.class });
  }

  public JSNativeClientResourceType createInstance(final ContextManager contextManager) {
    final JSNativeClientResourceType instance = new JSNativeClientResourceType();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}