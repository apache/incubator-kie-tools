package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.command.LienzoCanvasCommandFactory;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LocationControlImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasHandlerRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.LocationControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.ShapeLocationsChangedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;

public class Type_factory__o_k_w_c_s_c_l_c_c_LocationControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<LocationControlImpl> { public Type_factory__o_k_w_c_s_c_l_c_c_LocationControlImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LocationControlImpl.class, "Type_factory__o_k_w_c_s_c_l_c_c_LocationControlImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LocationControlImpl.class, AbstractCanvasHandlerRegistrationControl.class, AbstractCanvasHandlerControl.class, Object.class, CanvasControl.class, CanvasRegistrationControl.class, LocationControl.class, RequiresCommandManager.class, SessionAware.class });
  }

  public LocationControlImpl createInstance(final ContextManager contextManager) {
    final Event<ShapeLocationsChangedEvent> _shapeLocationsChangedEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ShapeLocationsChangedEvent.class }, new Annotation[] { });
    final Event<CanvasSelectionEvent> _selectionEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasSelectionEvent.class }, new Annotation[] { });
    final CanvasCommandFactory<AbstractCanvasHandler> _canvasCommandFactory_0 = (LienzoCanvasCommandFactory) contextManager.getInstance("Type_factory__o_k_w_c_s_c_l_c_c_LienzoCanvasCommandFactory__quals__j_e_i_Any_j_e_i_Default");
    final LocationControlImpl instance = new LocationControlImpl(_canvasCommandFactory_0, _shapeLocationsChangedEvent_1, _selectionEvent_2);
    registerDependentScopedReference(instance, _shapeLocationsChangedEvent_1);
    registerDependentScopedReference(instance, _selectionEvent_2);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onCanvasSelectionEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent", new AbstractCDIEventCallback<CanvasSelectionEvent>() {
      public void fireEvent(final CanvasSelectionEvent event) {
        LocationControlImpl_onCanvasSelectionEvent_CanvasSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasClearSelectionEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent", new AbstractCDIEventCallback<CanvasClearSelectionEvent>() {
      public void fireEvent(final CanvasClearSelectionEvent event) {
        LocationControlImpl_onCanvasClearSelectionEvent_CanvasClearSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LocationControlImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final LocationControlImpl instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasSelectionEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasClearSelectionEventSubscription", Subscription.class)).remove();
  }

  public native static void LocationControlImpl_onCanvasClearSelectionEvent_CanvasClearSelectionEvent(LocationControlImpl instance, CanvasClearSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LocationControlImpl::onCanvasClearSelectionEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/CanvasClearSelectionEvent;)(a0);
  }-*/;

  public native static void LocationControlImpl_onCanvasSelectionEvent_CanvasSelectionEvent(LocationControlImpl instance, CanvasSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LocationControlImpl::onCanvasSelectionEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/CanvasSelectionEvent;)(a0);
  }-*/;
}