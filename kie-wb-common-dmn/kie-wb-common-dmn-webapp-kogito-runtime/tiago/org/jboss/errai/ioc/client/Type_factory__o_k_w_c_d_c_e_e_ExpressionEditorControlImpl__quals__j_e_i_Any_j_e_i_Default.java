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
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorPresenter;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChanger;
import org.kie.workbench.common.dmn.client.editors.drd.DRDNameChangerView;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorControl;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorControlImpl;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorView;
import org.kie.workbench.common.dmn.client.editors.expressions.ExpressionEditorViewImpl;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl.SessionAware;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;

public class Type_factory__o_k_w_c_d_c_e_e_ExpressionEditorControlImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<ExpressionEditorControlImpl> { public Type_factory__o_k_w_c_d_c_e_e_ExpressionEditorControlImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ExpressionEditorControlImpl.class, "Type_factory__o_k_w_c_d_c_e_e_ExpressionEditorControlImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ExpressionEditorControlImpl.class, AbstractCanvasControl.class, Object.class, CanvasControl.class, ExpressionEditorControl.class, SessionAware.class });
  }

  public ExpressionEditorControlImpl createInstance(final ContextManager contextManager) {
    final DecisionNavigatorPresenter _decisionNavigator_1 = (DecisionNavigatorPresenter) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_DecisionNavigatorPresenter__quals__j_e_i_Any_j_e_i_Default");
    final ExpressionEditorView _view_0 = (ExpressionEditorViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_e_ExpressionEditorViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final DRDNameChanger _drdNameChanger_5 = (DRDNameChangerView) contextManager.getInstance("Type_factory__o_k_w_c_d_c_e_d_DRDNameChangerView__quals__j_e_i_Any_j_e_i_Default");
    final DMNGraphUtils _dmnGraphUtils_2 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final DMNDiagramsSession _dmnDiagramsSession_3 = (DMNDiagramsSession) contextManager.getInstance("Type_factory__o_k_w_c_d_c_d_n_d_DMNDiagramsSession__quals__j_e_i_Any_j_e_i_Default");
    final Event<CanvasElementUpdatedEvent> _canvasElementUpdatedEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { CanvasElementUpdatedEvent.class }, new Annotation[] { });
    final ExpressionEditorControlImpl instance = new ExpressionEditorControlImpl(_view_0, _decisionNavigator_1, _dmnGraphUtils_2, _dmnDiagramsSession_3, _canvasElementUpdatedEvent_4, _drdNameChanger_5);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _dmnGraphUtils_2);
    registerDependentScopedReference(instance, _canvasElementUpdatedEvent_4);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onCanvasFocusedSelectionEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent", new AbstractCDIEventCallback<CanvasSelectionEvent>() {
      public void fireEvent(final CanvasSelectionEvent event) {
        instance.onCanvasFocusedSelectionEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasElementUpdatedSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent", new AbstractCDIEventCallback<CanvasElementUpdatedEvent>() {
      public void fireEvent(final CanvasElementUpdatedEvent event) {
        instance.onCanvasElementUpdated(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((ExpressionEditorControlImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final ExpressionEditorControlImpl instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasFocusedSelectionEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasElementUpdatedSubscription", Subscription.class)).remove();
  }
}