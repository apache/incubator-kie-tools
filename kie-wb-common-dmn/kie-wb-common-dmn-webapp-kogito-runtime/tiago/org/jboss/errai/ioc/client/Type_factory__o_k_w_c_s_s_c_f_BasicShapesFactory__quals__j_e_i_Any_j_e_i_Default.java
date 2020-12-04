package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFunctionalFactory;
import org.kie.workbench.common.stunner.shapes.client.factory.BasicShapesFactory;
import org.kie.workbench.common.stunner.shapes.client.view.ShapeViewFactory;

public class Type_factory__o_k_w_c_s_s_c_f_BasicShapesFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<BasicShapesFactory> { public Type_factory__o_k_w_c_s_s_c_f_BasicShapesFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BasicShapesFactory.class, "Type_factory__o_k_w_c_s_s_c_f_BasicShapesFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BasicShapesFactory.class, Object.class, ShapeDefFactory.class });
  }

  public BasicShapesFactory createInstance(final ContextManager contextManager) {
    final ShapeViewFactory _shapeViewFactory_1 = (ShapeViewFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_s_c_v_ShapeViewFactory__quals__j_e_i_Any_j_e_i_Default");
    final ShapeDefFunctionalFactory _functionalFactory_0 = (ShapeDefFunctionalFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_f_ShapeDefFunctionalFactory__quals__j_e_i_Any_j_e_i_Default");
    final BasicShapesFactory instance = new BasicShapesFactory(_functionalFactory_0, _shapeViewFactory_1);
    registerDependentScopedReference(instance, _shapeViewFactory_1);
    registerDependentScopedReference(instance, _functionalFactory_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final BasicShapesFactory instance) {
    instance.init();
  }
}