package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.canvas.StunnerLienzoBoundsPanel;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseDownEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.mouse.CanvasMouseUpEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyDownEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyPressEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyUpEvent;

public class Type_factory__o_k_w_c_s_c_w_c_StunnerLienzoBoundsPanel__quals__j_e_i_Any_j_e_i_Default extends Factory<StunnerLienzoBoundsPanel> { public Type_factory__o_k_w_c_s_c_w_c_StunnerLienzoBoundsPanel__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(StunnerLienzoBoundsPanel.class, "Type_factory__o_k_w_c_s_c_w_c_StunnerLienzoBoundsPanel__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { StunnerLienzoBoundsPanel.class, Object.class });
  }

  public StunnerLienzoBoundsPanel createInstance(final ContextManager contextManager) {
    final Event<CanvasMouseUpEvent> _mouseUpEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasMouseUpEvent.class }, new Annotation[] { });
    final Event<KeyPressEvent> _keyPressEvent_0 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { KeyPressEvent.class }, new Annotation[] { });
    final Event<KeyDownEvent> _keyDownEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { KeyDownEvent.class }, new Annotation[] { });
    final Event<KeyUpEvent> _keyUpEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { KeyUpEvent.class }, new Annotation[] { });
    final Event<CanvasMouseDownEvent> _mouseDownEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasMouseDownEvent.class }, new Annotation[] { });
    final StunnerLienzoBoundsPanel instance = new StunnerLienzoBoundsPanel(_keyPressEvent_0, _keyDownEvent_1, _keyUpEvent_2, _mouseDownEvent_3, _mouseUpEvent_4);
    registerDependentScopedReference(instance, _mouseUpEvent_4);
    registerDependentScopedReference(instance, _keyPressEvent_0);
    registerDependentScopedReference(instance, _keyDownEvent_1);
    registerDependentScopedReference(instance, _keyUpEvent_2);
    registerDependentScopedReference(instance, _mouseDownEvent_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}