package org.jboss.errai.ioc.client;

import javax.inject.Singleton;
import org.jboss.errai.databinding.client.DataBinderProvider;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_j_e_d_c_DataBinderProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<DataBinderProvider> { public Type_factory__o_j_e_d_c_DataBinderProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DataBinderProvider.class, "Type_factory__o_j_e_d_c_DataBinderProvider__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DataBinderProvider.class, Object.class, ContextualTypeProvider.class });
  }

  public DataBinderProvider createInstance(final ContextManager contextManager) {
    final DataBinderProvider instance = new DataBinderProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}