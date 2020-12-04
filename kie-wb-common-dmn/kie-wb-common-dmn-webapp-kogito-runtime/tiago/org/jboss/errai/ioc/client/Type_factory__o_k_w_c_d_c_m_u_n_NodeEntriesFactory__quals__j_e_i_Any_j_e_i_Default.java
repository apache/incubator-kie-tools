package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsClientHelper;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeConnector;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntriesFactory;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.StunnerConverter;

public class Type_factory__o_k_w_c_d_c_m_u_n_NodeEntriesFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<NodeEntriesFactory> { public Type_factory__o_k_w_c_d_c_m_u_n_NodeEntriesFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NodeEntriesFactory.class, "Type_factory__o_k_w_c_d_c_m_u_n_NodeEntriesFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NodeEntriesFactory.class, Object.class });
  }

  public NodeEntriesFactory createInstance(final ContextManager contextManager) {
    final StunnerConverter _nodeFactory_0 = (StunnerConverter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_m_u_n_StunnerConverter__quals__j_e_i_Any_j_e_i_Default");
    final DMNMarshallerImportsClientHelper _dmnMarshallerImportsHelper_2 = (DMNMarshallerImportsClientHelper) contextManager.getInstance("Type_factory__o_k_w_c_d_c_m_i_DMNMarshallerImportsClientHelper__quals__j_e_i_Any_j_e_i_Default");
    final NodeConnector _nodeConnector_1 = (NodeConnector) contextManager.getInstance("Type_factory__o_k_w_c_d_c_m_u_n_NodeConnector__quals__j_e_i_Any_j_e_i_Default");
    final NodeEntriesFactory instance = new NodeEntriesFactory(_nodeFactory_0, _nodeConnector_1, _dmnMarshallerImportsHelper_2);
    registerDependentScopedReference(instance, _nodeFactory_0);
    registerDependentScopedReference(instance, _dmnMarshallerImportsHelper_2);
    registerDependentScopedReference(instance, _nodeConnector_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}