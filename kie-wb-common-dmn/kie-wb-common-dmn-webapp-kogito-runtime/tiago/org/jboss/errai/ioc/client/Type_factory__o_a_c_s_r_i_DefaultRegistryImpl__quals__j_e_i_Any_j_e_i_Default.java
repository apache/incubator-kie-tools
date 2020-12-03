package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.client.stateControl.registry.DefaultRegistry;
import org.appformer.client.stateControl.registry.Registry;
import org.appformer.client.stateControl.registry.impl.DefaultRegistryImpl;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_a_c_s_r_i_DefaultRegistryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultRegistryImpl> { public Type_factory__o_a_c_s_r_i_DefaultRegistryImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultRegistryImpl.class, "Type_factory__o_a_c_s_r_i_DefaultRegistryImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultRegistryImpl.class, Object.class, DefaultRegistry.class, Registry.class });
  }

  public DefaultRegistryImpl createInstance(final ContextManager contextManager) {
    final DefaultRegistryImpl instance = new DefaultRegistryImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}