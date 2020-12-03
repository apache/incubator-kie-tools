package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.appformer.client.keyboardShortcuts.KeyboardShortcutsApiOpts;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class JsType_factory__o_a_c_k_KeyboardShortcutsApiOpts__quals__Universal extends Factory<KeyboardShortcutsApiOpts> { public JsType_factory__o_a_c_k_KeyboardShortcutsApiOpts__quals__Universal() {
    super(new FactoryHandleImpl(KeyboardShortcutsApiOpts.class, "JsType_factory__o_a_c_k_KeyboardShortcutsApiOpts__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { KeyboardShortcutsApiOpts.class, Object.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public KeyboardShortcutsApiOpts createInstance(final ContextManager contextManager) {
    return (KeyboardShortcutsApiOpts) WindowInjectionContextStorage.createOrGet().getBean("org.appformer.client.keyboardShortcuts.KeyboardShortcutsApiOpts");
  }
}