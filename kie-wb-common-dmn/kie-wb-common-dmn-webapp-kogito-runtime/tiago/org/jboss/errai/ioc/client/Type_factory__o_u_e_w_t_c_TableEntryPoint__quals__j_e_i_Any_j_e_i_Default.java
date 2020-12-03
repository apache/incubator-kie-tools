package org.jboss.errai.ioc.client;

import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.table.client.TableEntryPoint;

public class Type_factory__o_u_e_w_t_c_TableEntryPoint__quals__j_e_i_Any_j_e_i_Default extends Factory<TableEntryPoint> { public Type_factory__o_u_e_w_t_c_TableEntryPoint__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TableEntryPoint.class, "Type_factory__o_u_e_w_t_c_TableEntryPoint__quals__j_e_i_Any_j_e_i_Default", EntryPoint.class, true, null, true));
    handle.setAssignableTypes(new Class[] { TableEntryPoint.class, Object.class });
  }

  public TableEntryPoint createInstance(final ContextManager contextManager) {
    final TableEntryPoint instance = new TableEntryPoint();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final TableEntryPoint instance) {
    instance.startApp();
  }
}