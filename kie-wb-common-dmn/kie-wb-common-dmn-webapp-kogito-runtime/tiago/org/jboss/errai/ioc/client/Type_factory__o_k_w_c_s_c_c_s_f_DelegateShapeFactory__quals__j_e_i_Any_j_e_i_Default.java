package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.shape.factory.AbstractShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.DelegateShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;

public class Type_factory__o_k_w_c_s_c_c_s_f_DelegateShapeFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<DelegateShapeFactory> { public Type_factory__o_k_w_c_s_c_c_s_f_DelegateShapeFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DelegateShapeFactory.class, "Type_factory__o_k_w_c_s_c_c_s_f_DelegateShapeFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DelegateShapeFactory.class, AbstractShapeFactory.class, Object.class, ShapeFactory.class });
  }

  public DelegateShapeFactory createInstance(final ContextManager contextManager) {
    final DelegateShapeFactory instance = new DelegateShapeFactory();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DelegateShapeFactory) instance, contextManager);
  }

  public void destroyInstanceHelper(final DelegateShapeFactory instance, final ContextManager contextManager) {
    instance.destroy();
  }
}