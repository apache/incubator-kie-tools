package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.file.RestoreUtil;

public class Type_factory__o_u_e_e_c_c_f_RestoreUtil__quals__j_e_i_Any_j_e_i_Default extends Factory<RestoreUtil> { public Type_factory__o_u_e_e_c_c_f_RestoreUtil__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(RestoreUtil.class, "Type_factory__o_u_e_e_c_c_f_RestoreUtil__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { RestoreUtil.class, Object.class });
  }

  public RestoreUtil createInstance(final ContextManager contextManager) {
    final RestoreUtil instance = new RestoreUtil();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}