package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.session.DMNCanvasShortcutsControl;
import org.kie.workbench.common.dmn.client.session.DMNCanvasShortcutsControlProxy;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.session.KogitoDMNCanvasShortcutsControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.AbstractCanvasShortcutsControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KeyShortcutCallback;

public class Type_factory__o_k_w_c_d_c_s_DMNCanvasShortcutsControlProxy__quals__j_e_i_Any_j_e_i_Default extends Factory<DMNCanvasShortcutsControlProxy> { public Type_factory__o_k_w_c_d_c_s_DMNCanvasShortcutsControlProxy__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DMNCanvasShortcutsControlProxy.class, "Type_factory__o_k_w_c_d_c_s_DMNCanvasShortcutsControlProxy__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNCanvasShortcutsControlProxy.class, Object.class, DMNCanvasShortcutsControl.class, CanvasControl.class, SessionAware.class, KeyShortcutCallback.class });
  }

  public DMNCanvasShortcutsControlProxy createInstance(final ContextManager contextManager) {
    final AbstractCanvasShortcutsControlImpl _delegate_0 = (KogitoDMNCanvasShortcutsControlImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_s_KogitoDMNCanvasShortcutsControlImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final DMNCanvasShortcutsControlProxy instance = new DMNCanvasShortcutsControlProxy(_delegate_0);
    registerDependentScopedReference(instance, _delegate_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}