package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.kogito.bridge.client.guided.tour.service.GuidedTourServiceProducer;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_a_k_b_c_g_t_s_GuidedTourServiceProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourServiceProducer> { public Type_factory__o_a_k_b_c_g_t_s_GuidedTourServiceProducer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GuidedTourServiceProducer.class, "Type_factory__o_a_k_b_c_g_t_s_GuidedTourServiceProducer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GuidedTourServiceProducer.class, Object.class });
  }

  public GuidedTourServiceProducer createInstance(final ContextManager contextManager) {
    final GuidedTourServiceProducer instance = new GuidedTourServiceProducer();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}