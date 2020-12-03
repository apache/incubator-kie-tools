package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.crud.client.CrudComponentEntryPoint;

public class Type_factory__o_k_w_c_f_c_c_CrudComponentEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<CrudComponentEntryPoint> { public Type_factory__o_k_w_c_f_c_c_CrudComponentEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CrudComponentEntryPoint.class, "Type_factory__o_k_w_c_f_c_c_CrudComponentEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { CrudComponentEntryPoint.class, Object.class });
  }

  public CrudComponentEntryPoint createInstance(final ContextManager contextManager) {
    final CrudComponentEntryPoint instance = new CrudComponentEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}