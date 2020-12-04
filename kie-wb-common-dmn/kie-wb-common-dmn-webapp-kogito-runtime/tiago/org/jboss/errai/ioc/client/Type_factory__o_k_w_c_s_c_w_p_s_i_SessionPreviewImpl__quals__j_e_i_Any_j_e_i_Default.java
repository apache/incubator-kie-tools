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
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.client.widgets.canvas.PreviewLienzoPanel;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.SessionDiagramPreview;
import org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPreviewImpl;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperView;
import org.kie.workbench.common.stunner.client.widgets.views.WidgetWrapperViewImpl;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManagerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.BaseCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.MediatorsControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactoryImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SingleSelection;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.MouseRequestLifecycle;
import org.kie.workbench.common.stunner.core.client.preferences.StunnerPreferencesRegistries;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_w_p_s_i_SessionPreviewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<SessionPreviewImpl> { public Type_factory__o_k_w_c_s_c_w_p_s_i_SessionPreviewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SessionPreviewImpl.class, "Type_factory__o_k_w_c_s_c_w_p_s_i_SessionPreviewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SessionDiagramPreview.class, Object.class });
  }

  public SessionPreviewImpl createInstance(final ContextManager contextManager) {
    final ManagedInstance<SelectionControl<AbstractCanvasHandler, Element>> _selectionControls_9 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { SelectionControl.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
      }, new SingleSelection() {
        public Class annotationType() {
          return SingleSelection.class;
        }
        public String toString() {
          return "@org.kie.workbench.common.stunner.core.client.canvas.controls.select.SingleSelection()";
        }
    } });
    final StunnerPreferencesRegistries _preferencesRegistries_13 = (StunnerPreferencesRegistries) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_p_StunnerPreferencesRegistries__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<CanvasCommandManager<AbstractCanvasHandler>> _canvasCommandManagers_11 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CanvasCommandManager.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final TextPropertyProviderFactory _textPropertyProviderFactory_3 = (TextPropertyProviderFactoryImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_c_a_TextPropertyProviderFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    final MouseRequestLifecycle _requestLifecycle_4 = (MouseRequestLifecycle) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_MouseRequestLifecycle__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<MediatorsControl<AbstractCanvas>> _mediatorControls_8 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { MediatorsControl.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final WidgetWrapperView _view_12 = (WidgetWrapperViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_v_WidgetWrapperViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final ShapeManager _shapeManager_2 = (ShapeManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<PreviewLienzoPanel> _canvasPanels_6 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { PreviewLienzoPanel.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final DefinitionUtils _definitionUtils_0 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<BaseCanvasHandler> _canvasHandlers_7 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { BaseCanvasHandler.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<CanvasCommandFactory> _canvasCommandFactories_10 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { CanvasCommandFactory.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final ManagedInstance<WiresCanvas> _canvases_5 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { WiresCanvas.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final GraphUtils _graphUtils_1 = (GraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_u_GraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final SessionPreviewImpl instance = new SessionPreviewImpl(_definitionUtils_0, _graphUtils_1, _shapeManager_2, _textPropertyProviderFactory_3, _requestLifecycle_4, _canvases_5, _canvasPanels_6, _canvasHandlers_7, _mediatorControls_8, _selectionControls_9, _canvasCommandFactories_10, _canvasCommandManagers_11, _view_12, _preferencesRegistries_13);
    registerDependentScopedReference(instance, _selectionControls_9);
    registerDependentScopedReference(instance, _canvasCommandManagers_11);
    registerDependentScopedReference(instance, _requestLifecycle_4);
    registerDependentScopedReference(instance, _mediatorControls_8);
    registerDependentScopedReference(instance, _view_12);
    registerDependentScopedReference(instance, _canvasPanels_6);
    registerDependentScopedReference(instance, _canvasHandlers_7);
    registerDependentScopedReference(instance, _canvasCommandFactories_10);
    registerDependentScopedReference(instance, _canvases_5);
    registerDependentScopedReference(instance, _graphUtils_1);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "commandExecutedFiredSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent", new AbstractCDIEventCallback<CanvasCommandExecutedEvent>() {
      public void fireEvent(final CanvasCommandExecutedEvent event) {
        SessionPreviewImpl_commandExecutedFired_CanvasCommandExecutedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent []";
      }
    }));
    thisInstance.setReference(instance, "commandUndoExecutedFiredSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent", new AbstractCDIEventCallback<CanvasCommandUndoneEvent>() {
      public void fireEvent(final CanvasCommandUndoneEvent event) {
        SessionPreviewImpl_commandUndoExecutedFired_CanvasCommandUndoneEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((SessionPreviewImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final SessionPreviewImpl instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "commandExecutedFiredSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "commandUndoExecutedFiredSubscription", Subscription.class)).remove();
  }

  public void invokePostConstructs(final SessionPreviewImpl instance) {
    instance.init();
  }

  public native static void SessionPreviewImpl_commandExecutedFired_CanvasCommandExecutedEvent(SessionPreviewImpl instance, CanvasCommandExecutedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPreviewImpl::commandExecutedFired(Lorg/kie/workbench/common/stunner/core/client/canvas/event/command/CanvasCommandExecutedEvent;)(a0);
  }-*/;

  public native static void SessionPreviewImpl_commandUndoExecutedFired_CanvasCommandUndoneEvent(SessionPreviewImpl instance, CanvasCommandUndoneEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.presenters.session.impl.SessionPreviewImpl::commandUndoExecutedFired(Lorg/kie/workbench/common/stunner/core/client/canvas/event/command/CanvasCommandUndoneEvent;)(a0);
  }-*/;
}