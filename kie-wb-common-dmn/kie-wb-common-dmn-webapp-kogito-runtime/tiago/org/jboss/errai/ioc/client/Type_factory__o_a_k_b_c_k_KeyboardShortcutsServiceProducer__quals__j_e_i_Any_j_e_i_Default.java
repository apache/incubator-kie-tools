package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.kogito.bridge.client.keyboardshortcuts.KeyboardShortcutsServiceProducer;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_a_k_b_c_k_KeyboardShortcutsServiceProducer__quals__j_e_i_Any_j_e_i_Default extends Factory<KeyboardShortcutsServiceProducer> { public Type_factory__o_a_k_b_c_k_KeyboardShortcutsServiceProducer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KeyboardShortcutsServiceProducer.class, "Type_factory__o_a_k_b_c_k_KeyboardShortcutsServiceProducer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KeyboardShortcutsServiceProducer.class, Object.class });
  }

  public KeyboardShortcutsServiceProducer createInstance(final ContextManager contextManager) {
    final KeyboardShortcutsServiceProducer instance = new KeyboardShortcutsServiceProducer();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}