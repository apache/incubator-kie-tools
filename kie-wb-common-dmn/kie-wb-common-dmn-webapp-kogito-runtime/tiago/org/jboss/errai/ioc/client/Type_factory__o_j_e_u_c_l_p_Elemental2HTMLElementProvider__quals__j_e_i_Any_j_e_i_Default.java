package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.producer.Elemental2HTMLElementProvider;

public class Type_factory__o_j_e_u_c_l_p_Elemental2HTMLElementProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<Elemental2HTMLElementProvider> { public Type_factory__o_j_e_u_c_l_p_Elemental2HTMLElementProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Elemental2HTMLElementProvider.class, "Type_factory__o_j_e_u_c_l_p_Elemental2HTMLElementProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Elemental2HTMLElementProvider.class, Object.class, ContextualTypeProvider.class });
  }

  public Elemental2HTMLElementProvider createInstance(final ContextManager contextManager) {
    final Elemental2HTMLElementProvider instance = new Elemental2HTMLElementProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}