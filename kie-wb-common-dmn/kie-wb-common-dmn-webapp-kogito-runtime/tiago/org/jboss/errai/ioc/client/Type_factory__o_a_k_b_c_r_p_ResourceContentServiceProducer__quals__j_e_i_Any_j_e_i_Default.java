package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.kogito.bridge.client.resource.producer.ResourceContentServiceProducer;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_a_k_b_c_r_p_ResourceContentServiceProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<ResourceContentServiceProducer> { public Type_factory__o_a_k_b_c_r_p_ResourceContentServiceProducer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ResourceContentServiceProducer.class, "Type_factory__o_a_k_b_c_r_p_ResourceContentServiceProducer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ResourceContentServiceProducer.class, Object.class });
  }

  public ResourceContentServiceProducer createInstance(final ContextManager contextManager) {
    final ResourceContentServiceProducer instance = new ResourceContentServiceProducer();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}