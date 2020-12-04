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
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.canvas.controls.selection.DomainObjectAwareLienzoMultipleSelectionControl;
import org.kie.workbench.common.dmn.client.editors.drd.DRDContextMenu;
import org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LienzoMultipleSelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.AbstractSelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.event.ShapeLocationsChangedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent;

public class Type_factory__o_k_w_c_d_c_c_c_s_DomainObjectAwareLienzoMultipleSelectionControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_s_MultipleSelection extends Factory<DomainObjectAwareLienzoMultipleSelectionControl> { public Type_factory__o_k_w_c_d_c_c_c_s_DomainObjectAwareLienzoMultipleSelectionControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_s_MultipleSelection() {
    super(new FactoryHandleImpl(DomainObjectAwareLienzoMultipleSelectionControl.class, "Type_factory__o_k_w_c_d_c_c_c_s_DomainObjectAwareLienzoMultipleSelectionControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor_o_k_w_c_s_c_c_c_c_s_MultipleSelection", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DomainObjectAwareLienzoMultipleSelectionControl.class, LienzoMultipleSelectionControl.class, AbstractSelectionControl.class, Object.class, SelectionControl.class, CanvasControl.class, CanvasRegistrationControl.class, CanvasControl.class, SessionAware.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
      }, new MultipleSelection() {
        public Class annotationType() {
          return MultipleSelection.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.canvas.controls.select.MultipleSelection()";
        }
    } });
  }

  public DomainObjectAwareLienzoMultipleSelectionControl createInstance(final ContextManager contextManager) {
    final Event<CanvasClearSelectionEvent> _clearSelectionEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasClearSelectionEvent.class }, new Annotation[] { });
    final Event<CanvasSelectionEvent> _canvasSelectionEvent_0 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasSelectionEvent.class }, new Annotation[] { });
    final DRDContextMenu _drdContextMenu_2 = (DRDContextMenu) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_d_DRDContextMenu__quals__j_e_i_Any_j_e_i_Default");
    final DomainObjectAwareLienzoMultipleSelectionControl instance = new DomainObjectAwareLienzoMultipleSelectionControl(_canvasSelectionEvent_0, _clearSelectionEvent_1, _drdContextMenu_2);
    registerDependentScopedReference(instance, _clearSelectionEvent_1);
    registerDependentScopedReference(instance, _canvasSelectionEvent_0);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "handleDomainObjectSelectedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent", new AbstractCDIEventCallback<DomainObjectSelectionEvent>() {
      public void fireEvent(final DomainObjectSelectionEvent event) {
        DomainObjectAwareLienzoMultipleSelectionControl_handleDomainObjectSelectedEvent_DomainObjectSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.DomainObjectSelectionEvent []";
      }
    }));
    thisInstance.setReference(instance, "onShapeLocationsChangedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.ShapeLocationsChangedEvent", new AbstractCDIEventCallback<ShapeLocationsChangedEvent>() {
      public void fireEvent(final ShapeLocationsChangedEvent event) {
        LienzoMultipleSelectionControl_onShapeLocationsChanged_ShapeLocationsChangedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.ShapeLocationsChangedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasSelectionSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent", new AbstractCDIEventCallback<CanvasSelectionEvent>() {
      public void fireEvent(final CanvasSelectionEvent event) {
        LienzoMultipleSelectionControl_onCanvasSelection_CanvasSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent []";
      }
    }));
    thisInstance.setReference(instance, "onShapeRemovedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent", new AbstractCDIEventCallback<CanvasShapeRemovedEvent>() {
      public void fireEvent(final CanvasShapeRemovedEvent event) {
        AbstractSelectionControl_onShapeRemovedEvent_CanvasShapeRemovedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasElementSelectedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent", new AbstractCDIEventCallback<CanvasSelectionEvent>() {
      public void fireEvent(final CanvasSelectionEvent event) {
        AbstractSelectionControl_onCanvasElementSelectedEvent_CanvasSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasClearSelectionEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent", new AbstractCDIEventCallback<CanvasClearSelectionEvent>() {
      public void fireEvent(final CanvasClearSelectionEvent event) {
        AbstractSelectionControl_onCanvasClearSelectionEvent_CanvasClearSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DomainObjectAwareLienzoMultipleSelectionControl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DomainObjectAwareLienzoMultipleSelectionControl instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "handleDomainObjectSelectedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onShapeLocationsChangedSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasSelectionSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onShapeRemovedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasElementSelectedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasClearSelectionEventSubscription", Subscription.class)).remove();
  }

  public native static void AbstractSelectionControl_onShapeRemovedEvent_CanvasShapeRemovedEvent(AbstractSelectionControl instance, CanvasShapeRemovedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.canvas.controls.select.AbstractSelectionControl::onShapeRemovedEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/registration/CanvasShapeRemovedEvent;)(a0);
  }-*/;

  public native static void LienzoMultipleSelectionControl_onShapeLocationsChanged_ShapeLocationsChangedEvent(LienzoMultipleSelectionControl instance, ShapeLocationsChangedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LienzoMultipleSelectionControl::onShapeLocationsChanged(Lorg/kie/workbench/common/stunner/core/client/canvas/event/ShapeLocationsChangedEvent;)(a0);
  }-*/;

  public native static void LienzoMultipleSelectionControl_onCanvasSelection_CanvasSelectionEvent(LienzoMultipleSelectionControl instance, CanvasSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.lienzo.canvas.controls.LienzoMultipleSelectionControl::onCanvasSelection(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/CanvasSelectionEvent;)(a0);
  }-*/;

  public native static void AbstractSelectionControl_onCanvasElementSelectedEvent_CanvasSelectionEvent(AbstractSelectionControl instance, CanvasSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.canvas.controls.select.AbstractSelectionControl::onCanvasElementSelectedEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/CanvasSelectionEvent;)(a0);
  }-*/;

  public native static void AbstractSelectionControl_onCanvasClearSelectionEvent_CanvasClearSelectionEvent(AbstractSelectionControl instance, CanvasClearSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.canvas.controls.select.AbstractSelectionControl::onCanvasClearSelectionEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/CanvasClearSelectionEvent;)(a0);
  }-*/;

  public native static void DomainObjectAwareLienzoMultipleSelectionControl_handleDomainObjectSelectedEvent_DomainObjectSelectionEvent(DomainObjectAwareLienzoMultipleSelectionControl instance, DomainObjectSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.dmn.client.canvas.controls.selection.DomainObjectAwareLienzoMultipleSelectionControl::handleDomainObjectSelectedEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/DomainObjectSelectionEvent;)(a0);
  }-*/;
}