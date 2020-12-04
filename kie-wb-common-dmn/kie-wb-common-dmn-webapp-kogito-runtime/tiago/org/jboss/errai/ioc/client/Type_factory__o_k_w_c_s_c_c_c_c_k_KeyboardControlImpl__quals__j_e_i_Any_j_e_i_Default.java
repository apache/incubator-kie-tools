package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyEventHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControlImpl;
import org.kie.workbench.common.stunner.kogito.runtime.client.session.command.impl.KogitoKeyEventHandlerImpl;

public class Type_factory__o_k_w_c_s_c_c_c_c_k_KeyboardControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<KeyboardControlImpl> { public Type_factory__o_k_w_c_s_c_c_c_c_k_KeyboardControlImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(KeyboardControlImpl.class, "Type_factory__o_k_w_c_s_c_c_c_c_k_KeyboardControlImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { KeyboardControlImpl.class, AbstractCanvasControl.class, Object.class, CanvasControl.class, KeyboardControl.class, SessionAware.class });
  }

  public KeyboardControlImpl createInstance(final ContextManager contextManager) {
    final KeyEventHandler _keyEventHandler_1 = (KogitoKeyEventHandlerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_k_r_c_s_c_i_KogitoKeyEventHandlerImpl__quals__j_e_i_Any_j_e_i_Default");
    final SessionManager _clientSessionManager_0 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final KeyboardControlImpl instance = new KeyboardControlImpl(_clientSessionManager_0, _keyEventHandler_1);
    registerDependentScopedReference(instance, _keyEventHandler_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}