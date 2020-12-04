package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFunctionalFactory;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;

public class Type_factory__o_k_w_c_s_s_c_s_f_SVGShapeFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<SVGShapeFactory> { public Type_factory__o_k_w_c_s_s_c_s_f_SVGShapeFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SVGShapeFactory.class, "Type_factory__o_k_w_c_s_s_c_s_f_SVGShapeFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SVGShapeFactory.class, Object.class, ShapeDefFactory.class });
  }

  public SVGShapeFactory createInstance(final ContextManager contextManager) {
    final ShapeDefFunctionalFactory _functionalFactory_1 = (ShapeDefFunctionalFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_s_f_ShapeDefFunctionalFactory__quals__j_e_i_Any_j_e_i_Default");
    final SyncBeanManager _beanManager_0 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final SVGShapeFactory instance = new SVGShapeFactory(_beanManager_0, _functionalFactory_1);
    registerDependentScopedReference(instance, _functionalFactory_1);
    registerDependentScopedReference(instance, _beanManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final SVGShapeFactory instance) {
    instance.init();
  }
}