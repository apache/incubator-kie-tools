package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.command.LienzoCanvasCommandFactory;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.ControlPointControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.connection.ControlPointControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;

public class Type_factory__o_k_w_c_s_c_l_c_c_ControlPointControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ControlPointControlImpl> { public Type_factory__o_k_w_c_s_c_l_c_c_ControlPointControlImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ControlPointControlImpl.class, "Type_factory__o_k_w_c_s_c_l_c_c_ControlPointControlImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ControlPointControlImpl.class, AbstractCanvasHandlerRegistrationControl.class, AbstractCanvasHandlerControl.class, Object.class, CanvasControl.class, CanvasRegistrationControl.class, ControlPointControl.class, RequiresCommandManager.class });
  }

  public ControlPointControlImpl createInstance(final ContextManager contextManager) {
    final Event<CanvasSelectionEvent> _selectionEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasSelectionEvent.class }, new Annotation[] { });
    final CanvasCommandFactory<AbstractCanvasHandler> _canvasCommandFactory_0 = (LienzoCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_c_LienzoCanvasCommandFactory__quals__j_e_i_Any_j_e_i_Default");
    final ControlPointControlImpl instance = new ControlPointControlImpl(_canvasCommandFactory_0, _selectionEvent_1);
    registerDependentScopedReference(instance, _selectionEvent_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}