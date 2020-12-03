package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFunctionalFactory;

public class Type_factory__o_k_w_c_s_c_c_s_f_ShapeDefFunctionalFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<ShapeDefFunctionalFactory> { public Type_factory__o_k_w_c_s_c_c_s_f_ShapeDefFunctionalFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ShapeDefFunctionalFactory.class, "Type_factory__o_k_w_c_s_c_c_s_f_ShapeDefFunctionalFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ShapeDefFunctionalFactory.class, Object.class, ShapeDefFactory.class });
  }

  public ShapeDefFunctionalFactory createInstance(final ContextManager contextManager) {
    final ShapeDefFunctionalFactory instance = new ShapeDefFunctionalFactory();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ShapeDefFunctionalFactory) instance, contextManager);
  }

  public void destroyInstanceHelper(final ShapeDefFunctionalFactory instance, final ContextManager contextManager) {
    instance.destroy();
  }
}