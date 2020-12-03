package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_j_e_c_c_d_e_Elemental2DomUtil__quals__j_e_i_Any_j_e_i_Default extends Factory<Elemental2DomUtil> { public Type_factory__o_j_e_c_c_d_e_Elemental2DomUtil__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Elemental2DomUtil.class, "Type_factory__o_j_e_c_c_d_e_Elemental2DomUtil__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Elemental2DomUtil.class, Object.class });
  }

  public Elemental2DomUtil createInstance(final ContextManager contextManager) {
    final Elemental2DomUtil instance = new Elemental2DomUtil();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}