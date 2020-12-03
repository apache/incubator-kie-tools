package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.shapes.client.factory.PictureProvidersManager;
import org.kie.workbench.common.stunner.shapes.client.view.ShapeViewFactory;

public class Type_factory__o_k_w_c_s_s_c_v_ShapeViewFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<ShapeViewFactory> { public Type_factory__o_k_w_c_s_s_c_v_ShapeViewFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ShapeViewFactory.class, "Type_factory__o_k_w_c_s_s_c_v_ShapeViewFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ShapeViewFactory.class, Object.class });
  }

  public ShapeViewFactory createInstance(final ContextManager contextManager) {
    final PictureProvidersManager _pictureProvidersManager_0 = (PictureProvidersManager) contextManager.getInstance("Type_factory__o_k_w_c_s_s_c_f_PictureProvidersManager__quals__j_e_i_Any_j_e_i_Default");
    final ShapeViewFactory instance = new ShapeViewFactory(_pictureProvidersManager_0);
    registerDependentScopedReference(instance, _pictureProvidersManager_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}