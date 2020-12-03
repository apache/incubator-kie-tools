package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.resources.DMNSVGViewFactory;
import org.kie.workbench.common.dmn.client.resources.DMNSVGViewFactoryImpl;
import org.kie.workbench.common.stunner.svg.client.shape.view.factory.AbstractSVGViewFactory;

public class Type_factory__o_k_w_c_d_c_r_DMNSVGViewFactoryImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNSVGViewFactoryImpl> { public Type_factory__o_k_w_c_d_c_r_DMNSVGViewFactoryImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNSVGViewFactoryImpl.class, "Type_factory__o_k_w_c_d_c_r_DMNSVGViewFactoryImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNSVGViewFactoryImpl.class, AbstractSVGViewFactory.class, Object.class, DMNSVGViewFactory.class });
  }

  public DMNSVGViewFactoryImpl createInstance(final ContextManager contextManager) {
    final DMNSVGViewFactoryImpl instance = new DMNSVGViewFactoryImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}