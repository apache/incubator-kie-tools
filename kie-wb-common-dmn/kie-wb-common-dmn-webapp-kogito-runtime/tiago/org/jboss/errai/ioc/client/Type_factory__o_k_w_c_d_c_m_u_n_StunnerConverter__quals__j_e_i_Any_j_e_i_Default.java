package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.StunnerConverter;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;

public class Type_factory__o_k_w_c_d_c_m_u_n_StunnerConverter__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerConverter> { public Type_factory__o_k_w_c_d_c_m_u_n_StunnerConverter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StunnerConverter.class, "Type_factory__o_k_w_c_d_c_m_u_n_StunnerConverter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { StunnerConverter.class, Object.class });
  }

  public StunnerConverter createInstance(final ContextManager contextManager) {
    final FactoryManager _factoryManager_0 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final StunnerConverter instance = new StunnerConverter(_factoryManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}