package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.api.ReadOnlyProviderImpl;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNCommonActionsToolbox;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNFlowActionsToolbox;
import org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNToolboxControl;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.AbstractToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;

public class Type_factory__o_k_w_c_d_c_c_c_t_DMNToolboxControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor extends Factory<DMNToolboxControl> { public Type_factory__o_k_w_c_d_c_c_c_t_DMNToolboxControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor() {
    super(new FactoryHandleImpl(DMNToolboxControl.class, "Type_factory__o_k_w_c_d_c_c_c_t_DMNToolboxControl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DMNToolboxControl.class, AbstractToolboxControl.class, Object.class, ToolboxControl.class, CanvasRegistrationControl.class, CanvasControl.class });
    handle.setQualifiers(new Annotation[] { QualifierUtil.ANY_ANNOTATION, new DMNEditor() {
        public Class annotationType() {
          return DMNEditor.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.api.qualifiers.DMNEditor()";
        }
    } });
  }

  public DMNToolboxControl createInstance(final ContextManager contextManager) {
    final ManagedInstance<ActionsToolboxFactory> _flowActionsToolboxFactories_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ActionsToolboxFactory.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
      }, new DMNFlowActionsToolbox() {
        public Class annotationType() {
          return DMNFlowActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNFlowActionsToolbox()";
        }
    } });
    final ManagedInstance<ActionsToolboxFactory> _commonActionsToolboxFactories_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ActionsToolboxFactory.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
      }, new DMNCommonActionsToolbox() {
        public Class annotationType() {
          return DMNCommonActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.dmn.client.canvas.controls.toolbox.DMNCommonActionsToolbox()";
        }
    } });
    final ReadOnlyProvider _readOnlyProvider_2 = (ReadOnlyProviderImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_a_ReadOnlyProviderImpl__quals__j_e_i_Any_o_k_w_c_d_a_q_DMNEditor");
    final DMNToolboxControl instance = new DMNToolboxControl(_flowActionsToolboxFactories_0, _commonActionsToolboxFactories_1, _readOnlyProvider_2);
    registerDependentScopedReference(instance, _flowActionsToolboxFactories_0);
    registerDependentScopedReference(instance, _commonActionsToolboxFactories_1);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onCanvasSelectionEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent", new AbstractCDIEventCallback<CanvasSelectionEvent>() {
      public void fireEvent(final CanvasSelectionEvent event) {
        AbstractToolboxControl_onCanvasSelectionEvent_CanvasSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasClearSelectionEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent", new AbstractCDIEventCallback<CanvasClearSelectionEvent>() {
      public void fireEvent(final CanvasClearSelectionEvent event) {
        AbstractToolboxControl_onCanvasClearSelectionEvent_CanvasClearSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasShapeRemovedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent", new AbstractCDIEventCallback<CanvasShapeRemovedEvent>() {
      public void fireEvent(final CanvasShapeRemovedEvent event) {
        AbstractToolboxControl_onCanvasShapeRemovedEvent_CanvasShapeRemovedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((DMNToolboxControl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DMNToolboxControl instance, final ContextManager contextManager) {
    instance.destroy();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasSelectionEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasClearSelectionEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasShapeRemovedEventSubscription", Subscription.class)).remove();
  }

  public native static void AbstractToolboxControl_onCanvasSelectionEvent_CanvasSelectionEvent(AbstractToolboxControl instance, CanvasSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.AbstractToolboxControl::onCanvasSelectionEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/CanvasSelectionEvent;)(a0);
  }-*/;

  public native static void AbstractToolboxControl_onCanvasShapeRemovedEvent_CanvasShapeRemovedEvent(AbstractToolboxControl instance, CanvasShapeRemovedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.AbstractToolboxControl::onCanvasShapeRemovedEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/registration/CanvasShapeRemovedEvent;)(a0);
  }-*/;

  public native static void AbstractToolboxControl_onCanvasClearSelectionEvent_CanvasClearSelectionEvent(AbstractToolboxControl instance, CanvasClearSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.AbstractToolboxControl::onCanvasClearSelectionEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/CanvasClearSelectionEvent;)(a0);
  }-*/;
}