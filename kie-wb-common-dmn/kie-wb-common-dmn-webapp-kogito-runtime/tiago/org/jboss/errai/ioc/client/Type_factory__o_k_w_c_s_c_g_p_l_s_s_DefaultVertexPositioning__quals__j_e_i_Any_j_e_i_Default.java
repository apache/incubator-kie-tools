package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.DefaultVertexPositioning;
import org.kie.workbench.common.stunner.core.graph.processing.layout.sugiyama.step04.VertexPositioning;

public class Type_factory__o_k_w_c_s_c_g_p_l_s_s_DefaultVertexPositioning__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultVertexPositioning> { public Type_factory__o_k_w_c_s_c_g_p_l_s_s_DefaultVertexPositioning__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultVertexPositioning.class, "Type_factory__o_k_w_c_s_c_g_p_l_s_s_DefaultVertexPositioning__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultVertexPositioning.class, Object.class, VertexPositioning.class });
  }

  public DefaultVertexPositioning createInstance(final ContextManager contextManager) {
    final DefaultVertexPositioning instance = new DefaultVertexPositioning();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}