package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.components.glyph.DOMGlyphRenderers;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer;
import org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer.View;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManagerImpl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactory;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.TextPropertyProviderFactoryImpl;
import org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessor;
import org.kie.workbench.common.stunner.core.graph.processing.traverse.content.ChildrenTraverseProcessorImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_w_e_t_TreeExplorer__quals__j_e_i_Any_j_e_i_Default extends Factory<TreeExplorer> { public Type_factory__o_k_w_c_s_c_w_e_t_TreeExplorer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TreeExplorer.class, "Type_factory__o_k_w_c_s_c_w_e_t_TreeExplorer__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TreeExplorer.class, Object.class, IsWidget.class });
  }

  public TreeExplorer createInstance(final ContextManager contextManager) {
    final ChildrenTraverseProcessor _childrenTraverseProcessor_0 = (ChildrenTraverseProcessorImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_g_p_t_c_ChildrenTraverseProcessorImpl__quals__j_e_i_Any_j_e_i_Default");
    final ShapeManager _shapeManager_4 = (ShapeManagerImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ShapeManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final DOMGlyphRenderers _domGlyphRenderers_5 = (DOMGlyphRenderers) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_c_g_DOMGlyphRenderers__quals__j_e_i_Any_j_e_i_Default");
    final TextPropertyProviderFactory _textPropertyProviderFactory_1 = (TextPropertyProviderFactoryImpl) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_c_c_a_TextPropertyProviderFactoryImpl__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasSelectionEvent> _selectionEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasSelectionEvent.class }, new Annotation[] { });
    final DefinitionUtils _definitionUtils_3 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<View> _views_6 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { View.class }, new Annotation[] { new Any() {
        public Class annotationType() {
          return Any.class;
        }
        public String toString() {
          return "@javax.enterprise.inject.Any()";
        }
    } });
    final TreeExplorer instance = new TreeExplorer(_childrenTraverseProcessor_0, _textPropertyProviderFactory_1, _selectionEvent_2, _definitionUtils_3, _shapeManager_4, _domGlyphRenderers_5, _views_6);
    registerDependentScopedReference(instance, _childrenTraverseProcessor_0);
    registerDependentScopedReference(instance, _selectionEvent_2);
    registerDependentScopedReference(instance, _views_6);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onCanvasClearEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent", new AbstractCDIEventCallback<CanvasClearEvent>() {
      public void fireEvent(final CanvasClearEvent event) {
        TreeExplorer_onCanvasClearEvent_CanvasClearEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.CanvasClearEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasElementAddedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent", new AbstractCDIEventCallback<CanvasElementAddedEvent>() {
      public void fireEvent(final CanvasElementAddedEvent event) {
        TreeExplorer_onCanvasElementAddedEvent_CanvasElementAddedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasElementRemovedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent", new AbstractCDIEventCallback<CanvasElementRemovedEvent>() {
      public void fireEvent(final CanvasElementRemovedEvent event) {
        TreeExplorer_onCanvasElementRemovedEvent_CanvasElementRemovedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasElementsClearEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent", new AbstractCDIEventCallback<CanvasElementsClearEvent>() {
      public void fireEvent(final CanvasElementsClearEvent event) {
        TreeExplorer_onCanvasElementsClearEvent_CanvasElementsClearEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasElementUpdatedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent", new AbstractCDIEventCallback<CanvasElementUpdatedEvent>() {
      public void fireEvent(final CanvasElementUpdatedEvent event) {
        TreeExplorer_onCanvasElementUpdatedEvent_CanvasElementUpdatedEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasSelectionEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent", new AbstractCDIEventCallback<CanvasSelectionEvent>() {
      public void fireEvent(final CanvasSelectionEvent event) {
        TreeExplorer_onCanvasSelectionEvent_CanvasSelectionEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((TreeExplorer) instance, contextManager);
  }

  public void destroyInstanceHelper(final TreeExplorer instance, final ContextManager contextManager) {
    instance.destroy();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasClearEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasElementAddedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasElementRemovedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasElementsClearEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasElementUpdatedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasSelectionEventSubscription", Subscription.class)).remove();
  }

  public native static void TreeExplorer_onCanvasElementsClearEvent_CanvasElementsClearEvent(TreeExplorer instance, CanvasElementsClearEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer::onCanvasElementsClearEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/registration/CanvasElementsClearEvent;)(a0);
  }-*/;

  public native static void TreeExplorer_onCanvasElementUpdatedEvent_CanvasElementUpdatedEvent(TreeExplorer instance, CanvasElementUpdatedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer::onCanvasElementUpdatedEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/registration/CanvasElementUpdatedEvent;)(a0);
  }-*/;

  public native static void TreeExplorer_onCanvasSelectionEvent_CanvasSelectionEvent(TreeExplorer instance, CanvasSelectionEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer::onCanvasSelectionEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/selection/CanvasSelectionEvent;)(a0);
  }-*/;

  public native static void TreeExplorer_onCanvasClearEvent_CanvasClearEvent(TreeExplorer instance, CanvasClearEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer::onCanvasClearEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/CanvasClearEvent;)(a0);
  }-*/;

  public native static void TreeExplorer_onCanvasElementRemovedEvent_CanvasElementRemovedEvent(TreeExplorer instance, CanvasElementRemovedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer::onCanvasElementRemovedEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/registration/CanvasElementRemovedEvent;)(a0);
  }-*/;

  public native static void TreeExplorer_onCanvasElementAddedEvent_CanvasElementAddedEvent(TreeExplorer instance, CanvasElementAddedEvent a0) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.explorer.tree.TreeExplorer::onCanvasElementAddedEvent(Lorg/kie/workbench/common/stunner/core/client/canvas/event/registration/CanvasElementAddedEvent;)(a0);
  }-*/;
}