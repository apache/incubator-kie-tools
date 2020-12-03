package org.jboss.errai.ioc.client;

import javax.inject.Singleton;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.widget.LocaleSelector;

public class Type_factory__o_j_e_u_c_w_LocaleSelector__quals__j_e_i_Any_j_e_i_Default extends Factory<LocaleSelector> { public Type_factory__o_j_e_u_c_w_LocaleSelector__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LocaleSelector.class, "Type_factory__o_j_e_u_c_w_LocaleSelector__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LocaleSelector.class, Object.class });
  }

  public LocaleSelector createInstance(final ContextManager contextManager) {
    final LocaleSelector instance = new LocaleSelector();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}