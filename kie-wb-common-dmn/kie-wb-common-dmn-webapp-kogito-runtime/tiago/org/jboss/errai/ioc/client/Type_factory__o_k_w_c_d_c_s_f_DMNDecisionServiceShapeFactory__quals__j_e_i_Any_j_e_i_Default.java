package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.resources.DMNDecisionServiceSVGViewFactory;
import org.kie.workbench.common.dmn.client.resources.DMNDecisionServiceSVGViewFactoryImpl;
import org.kie.workbench.common.dmn.client.shape.factory.DMNDecisionServiceShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFactory;

public class Type_factory__o_k_w_c_d_c_s_f_DMNDecisionServiceShapeFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNDecisionServiceShapeFactory> { public Type_factory__o_k_w_c_d_c_s_f_DMNDecisionServiceShapeFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNDecisionServiceShapeFactory.class, "Type_factory__o_k_w_c_d_c_s_f_DMNDecisionServiceShapeFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNDecisionServiceShapeFactory.class, Object.class, ShapeDefFactory.class });
  }

  public DMNDecisionServiceShapeFactory createInstance(final ContextManager contextManager) {
    final DMNDecisionServiceSVGViewFactory _dmnViewFactory_0 = (DMNDecisionServiceSVGViewFactoryImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_r_DMNDecisionServiceSVGViewFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    final DMNDecisionServiceShapeFactory instance = new DMNDecisionServiceShapeFactory(_dmnViewFactory_0);
    registerDependentScopedReference(instance, _dmnViewFactory_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}