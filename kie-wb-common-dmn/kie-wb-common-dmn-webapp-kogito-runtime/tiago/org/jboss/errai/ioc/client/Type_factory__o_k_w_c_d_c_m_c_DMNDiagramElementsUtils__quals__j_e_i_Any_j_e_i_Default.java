package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.marshaller.common.DMNDiagramElementsUtils;

public class Type_factory__o_k_w_c_d_c_m_c_DMNDiagramElementsUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDiagramElementsUtils> { public Type_factory__o_k_w_c_d_c_m_c_DMNDiagramElementsUtils__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDiagramElementsUtils.class, "Type_factory__o_k_w_c_d_c_m_c_DMNDiagramElementsUtils__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDiagramElementsUtils.class, Object.class });
  }

  public DMNDiagramElementsUtils createInstance(final ContextManager contextManager) {
    final DMNDiagramElementsUtils instance = new DMNDiagramElementsUtils();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}