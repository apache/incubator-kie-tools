package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.client.stateControl.registry.Registry;
import org.appformer.kogito.bridge.client.stateControl.registry.producer.CommandRegistryProducer;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Producer_factory__o_a_c_s_r_Registry__quals__j_e_i_Any_j_e_i_Default extends Factory<Registry> { public Producer_factory__o_a_c_s_r_Registry__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Registry.class, "Producer_factory__o_a_c_s_r_Registry__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Registry.class });
  }

  public Registry createInstance(final ContextManager contextManager) {
    CommandRegistryProducer producerInstance = contextManager.getInstance("Type_factory__o_a_k_b_c_s_r_p_CommandRegistryProducer__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final Registry instance = producerInstance.lookup();
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    registerDependentScopedReference(instance, producerInstance);
    return instance;
  }
}