package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.resources.DMNDecisionServiceSVGViewFactory;
import org.kie.workbench.common.dmn.client.resources.DMNDecisionServiceSVGViewFactoryImpl;
import org.kie.workbench.common.stunner.svg.client.shape.view.factory.AbstractSVGViewFactory;

public class Type_factory__o_k_w_c_d_c_r_DMNDecisionServiceSVGViewFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDecisionServiceSVGViewFactoryImpl> { public Type_factory__o_k_w_c_d_c_r_DMNDecisionServiceSVGViewFactoryImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDecisionServiceSVGViewFactoryImpl.class, "Type_factory__o_k_w_c_d_c_r_DMNDecisionServiceSVGViewFactoryImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDecisionServiceSVGViewFactoryImpl.class, AbstractSVGViewFactory.class, Object.class, DMNDecisionServiceSVGViewFactory.class });
  }

  public DMNDecisionServiceSVGViewFactoryImpl createInstance(final ContextManager contextManager) {
    final DMNDecisionServiceSVGViewFactoryImpl instance = new DMNDecisionServiceSVGViewFactoryImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}