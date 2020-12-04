package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.definition.DMNDefinition;
import org.kie.workbench.common.dmn.client.shape.factory.DMNConnectorShapeFactory;
import org.kie.workbench.common.dmn.client.shape.factory.DMNDecisionServiceShapeFactory;
import org.kie.workbench.common.dmn.client.shape.factory.DMNShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.DelegateShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;

public class Type_factory__o_k_w_c_d_c_s_f_DMNShapeFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNShapeFactory> { public Type_factory__o_k_w_c_d_c_s_f_DMNShapeFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNShapeFactory.class, "Type_factory__o_k_w_c_d_c_s_f_DMNShapeFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNShapeFactory.class, Object.class, ShapeFactory.class });
  }

  public DMNShapeFactory createInstance(final ContextManager contextManager) {
    final SVGShapeFactory _svgShapeFactory_0 = (SVGShapeFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_s_c_s_f_SVGShapeFactory__quals__j_e_i_Any_j_e_i_Default");
    final DMNConnectorShapeFactory _dmnConnectorShapeFactory_1 = (DMNConnectorShapeFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_s_f_DMNConnectorShapeFactory__quals__j_e_i_Any_j_e_i_Default");
    final DelegateShapeFactory<DMNDefinition, Shape> _delegateShapeFactory_3 = (DelegateShapeFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_f_DelegateShapeFactory__quals__j_e_i_Any_j_e_i_Default");
    final DMNDecisionServiceShapeFactory _dmnDecisionServiceShapeFactory_2 = (DMNDecisionServiceShapeFactory) contextManager.getInstance("Type_factory__o_k_w_c_d_c_s_f_DMNDecisionServiceShapeFactory__quals__j_e_i_Any_j_e_i_Default");
    final DMNShapeFactory instance = new DMNShapeFactory(_svgShapeFactory_0, _dmnConnectorShapeFactory_1, _dmnDecisionServiceShapeFactory_2, _delegateShapeFactory_3);
    registerDependentScopedReference(instance, _svgShapeFactory_0);
    registerDependentScopedReference(instance, _delegateShapeFactory_3);
    registerDependentScopedReference(instance, _dmnDecisionServiceShapeFactory_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DMNShapeFactory instance) {
    instance.init();
  }
}