package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers.HTMLElementsPositionProviderFactory;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers.PositionProviderFactory;

public class Type_factory__o_k_w_c_d_w_k_c_c_t_p_HTMLElementsPositionProviderFactory__quals__j_e_i_Any_j_e_i_Default extends Factory<HTMLElementsPositionProviderFactory> { public Type_factory__o_k_w_c_d_w_k_c_c_t_p_HTMLElementsPositionProviderFactory__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(HTMLElementsPositionProviderFactory.class, "Type_factory__o_k_w_c_d_w_k_c_c_t_p_HTMLElementsPositionProviderFactory__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { HTMLElementsPositionProviderFactory.class, Object.class, PositionProviderFactory.class });
  }

  public HTMLElementsPositionProviderFactory createInstance(final ContextManager contextManager) {
    final HTMLElementsPositionProviderFactory instance = new HTMLElementsPositionProviderFactory();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}