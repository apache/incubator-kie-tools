package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeConnector;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;

public class Type_factory__o_k_w_c_d_c_m_u_n_NodeConnector__quals__j_e_i_Any_j_e_i_Default extends Factory<NodeConnector> { public Type_factory__o_k_w_c_d_c_m_u_n_NodeConnector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NodeConnector.class, "Type_factory__o_k_w_c_d_c_m_u_n_NodeConnector__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NodeConnector.class, Object.class });
  }

  public NodeConnector createInstance(final ContextManager contextManager) {
    final FactoryManager _factoryManager_0 = (ClientFactoryManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientFactoryManager__quals__j_e_i_Any_j_e_i_Default");
    final NodeConnector instance = new NodeConnector(_factoryManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}