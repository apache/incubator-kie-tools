package org.jboss.errai.ioc.client;

import javax.inject.Singleton;
import org.jboss.errai.ioc.client.api.ContextualTypeProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.widget.ListWidgetProvider;

public class Type_factory__o_j_e_u_c_w_ListWidgetProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ListWidgetProvider> { public Type_factory__o_j_e_u_c_w_ListWidgetProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ListWidgetProvider.class, "Type_factory__o_j_e_u_c_w_ListWidgetProvider__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ListWidgetProvider.class, Object.class, ContextualTypeProvider.class });
  }

  public ListWidgetProvider createInstance(final ContextManager contextManager) {
    final ListWidgetProvider instance = new ListWidgetProvider();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}