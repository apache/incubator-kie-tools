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
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasRegistrationControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.AbstractToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.DefaultToolboxControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasShapeRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.ActionsToolboxFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox;
import org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphActionsToolbox;

public class Type_factory__o_k_w_c_s_c_c_c_c_t_DefaultToolboxControl__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultToolboxControl> { public Type_factory__o_k_w_c_s_c_c_c_c_t_DefaultToolboxControl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultToolboxControl.class, "Type_factory__o_k_w_c_s_c_c_c_c_t_DefaultToolboxControl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultToolboxControl.class, AbstractToolboxControl.class, Object.class, ToolboxControl.class, CanvasRegistrationControl.class, CanvasControl.class });
  }

  public DefaultToolboxControl createInstance(final ContextManager contextManager) {
    final ManagedInstance<ActionsToolboxFactory> _morphActionsToolboxFactories_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ActionsToolboxFactory.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
      }, new MorphActionsToolbox() {
        public Class annotationType() {
          return MorphActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.MorphActionsToolbox()";
        }
    } });
    final ManagedInstance<ActionsToolboxFactory> _flowActionsToolboxFactories_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ActionsToolboxFactory.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
      }, new FlowActionsToolbox() {
        public Class annotationType() {
          return FlowActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.FlowActionsToolbox()";
        }
    } });
    final ManagedInstance<ActionsToolboxFactory> _commonActionsToolboxFactories_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { ActionsToolboxFactory.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
      }, new CommonActionsToolbox() {
        public Class annotationType() {
          return CommonActionsToolbox.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.components.toolbox.actions.CommonActionsToolbox()";
        }
    } });
    final DefaultToolboxControl instance = new DefaultToolboxControl(_flowActionsToolboxFactories_0, _morphActionsToolboxFactories_1, _commonActionsToolboxFactories_2);
    registerDependentScopedReference(instance, _morphActionsToolboxFactories_1);
    registerDependentScopedReference(instance, _flowActionsToolboxFactories_0);
    registerDependentScopedReference(instance, _commonActionsToolboxFactories_2);
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
    destroyInstanceHelper((DefaultToolboxControl) instance, contextManager);
  }

  public void destroyInstanceHelper(final DefaultToolboxControl instance, final ContextManager contextManager) {
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