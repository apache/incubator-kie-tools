package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.factory.AbstractDMNDiagramFactory;
import org.kie.workbench.common.dmn.api.factory.DMNDiagramFactory;
import org.kie.workbench.common.dmn.api.factory.DMNDiagramFactoryImpl;
import org.kie.workbench.common.dmn.api.factory.DMNFactory;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.factory.impl.BindableDiagramFactory;

public class Type_factory__o_k_w_c_d_a_f_DMNDiagramFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDiagramFactoryImpl> { public Type_factory__o_k_w_c_d_a_f_DMNDiagramFactoryImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDiagramFactoryImpl.class, "Type_factory__o_k_w_c_d_a_f_DMNDiagramFactoryImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDiagramFactoryImpl.class, AbstractDMNDiagramFactory.class, BindableDiagramFactory.class, Object.class, DiagramFactory.class, org.kie.workbench.common.stunner.core.factory.Factory.class, DMNDiagramFactory.class, DMNFactory.class });
  }

  public DMNDiagramFactoryImpl createInstance(final ContextManager contextManager) {
    final DMNDiagramFactoryImpl instance = new DMNDiagramFactoryImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}