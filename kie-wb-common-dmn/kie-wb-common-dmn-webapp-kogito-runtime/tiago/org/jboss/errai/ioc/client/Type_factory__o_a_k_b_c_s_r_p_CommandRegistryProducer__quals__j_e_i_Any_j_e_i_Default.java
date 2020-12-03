package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.kogito.bridge.client.stateControl.registry.producer.CommandRegistryProducer;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_a_k_b_c_s_r_p_CommandRegistryProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<CommandRegistryProducer> { public Type_factory__o_a_k_b_c_s_r_p_CommandRegistryProducer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CommandRegistryProducer.class, "Type_factory__o_a_k_b_c_s_r_p_CommandRegistryProducer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CommandRegistryProducer.class, Object.class });
  }

  public CommandRegistryProducer createInstance(final ContextManager contextManager) {
    final CommandRegistryProducer instance = new CommandRegistryProducer();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}