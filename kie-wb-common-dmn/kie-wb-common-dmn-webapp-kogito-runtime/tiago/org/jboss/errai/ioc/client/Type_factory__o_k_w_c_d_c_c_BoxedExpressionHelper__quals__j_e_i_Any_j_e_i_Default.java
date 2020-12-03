package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.common.BoxedExpressionHelper;

public class Type_factory__o_k_w_c_d_c_c_BoxedExpressionHelper__quals__j_e_i_Any_j_e_i_Default extends Factory<BoxedExpressionHelper> { public Type_factory__o_k_w_c_d_c_c_BoxedExpressionHelper__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(BoxedExpressionHelper.class, "Type_factory__o_k_w_c_d_c_c_BoxedExpressionHelper__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { BoxedExpressionHelper.class, Object.class });
  }

  public BoxedExpressionHelper createInstance(final ContextManager contextManager) {
    final BoxedExpressionHelper instance = new BoxedExpressionHelper();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}