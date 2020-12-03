package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.kogito.bridge.client.keyboardshortcuts.KeyboardShortcutsApi;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyEventHandler;
import org.kie.workbench.common.stunner.kogito.runtime.client.session.command.impl.KogitoKeyEventHandlerImpl;

public class Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoKeyEventHandlerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<KogitoKeyEventHandlerImpl> { public Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoKeyEventHandlerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KogitoKeyEventHandlerImpl.class, "Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoKeyEventHandlerImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KogitoKeyEventHandlerImpl.class, Object.class, KeyEventHandler.class });
  }

  public KogitoKeyEventHandlerImpl createInstance(final ContextManager contextManager) {
    final KogitoKeyEventHandlerImpl instance = new KogitoKeyEventHandlerImpl();
    setIncompleteInstance(instance);
    final KeyboardShortcutsApi KogitoKeyEventHandlerImpl_keyboardShortcutsApi = (KeyboardShortcutsApi) contextManager.getInstance("Producer_factory__o_a_k_b_c_k_KeyboardShortcutsApi__quals__j_e_i_Any_j_e_i_Default");
    KogitoKeyEventHandlerImpl_KeyboardShortcutsApi_keyboardShortcutsApi(instance, KogitoKeyEventHandlerImpl_keyboardShortcutsApi);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((KogitoKeyEventHandlerImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final KogitoKeyEventHandlerImpl instance, final ContextManager contextManager) {
    instance.clear();
  }

  native static KeyboardShortcutsApi KogitoKeyEventHandlerImpl_KeyboardShortcutsApi_keyboardShortcutsApi(KogitoKeyEventHandlerImpl instance) /*-{
    return instance.@org.kie.workbench.common.stunner.kogito.runtime.client.session.command.impl.KogitoKeyEventHandlerImpl::keyboardShortcutsApi;
  }-*/;

  native static void KogitoKeyEventHandlerImpl_KeyboardShortcutsApi_keyboardShortcutsApi(KogitoKeyEventHandlerImpl instance, KeyboardShortcutsApi value) /*-{
    instance.@org.kie.workbench.common.stunner.kogito.runtime.client.session.command.impl.KogitoKeyEventHandlerImpl::keyboardShortcutsApi = value;
  }-*/;
}