package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.graph.DMNDiagramUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSessionState;

public class Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSessionState__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDiagramsSessionState> { public Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSessionState__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDiagramsSessionState.class, "Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSessionState__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDiagramsSessionState.class, Object.class });
  }

  public DMNDiagramsSessionState createInstance(final ContextManager contextManager) {
    final DMNDiagramUtils _dmnDiagramUtils_0 = (DMNDiagramUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_a_g_DMNDiagramUtils__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramsSessionState instance = new DMNDiagramsSessionState(_dmnDiagramUtils_0);
    registerDependentScopedReference(instance, _dmnDiagramUtils_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}