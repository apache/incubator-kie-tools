package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourObserver;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.enterprise.client.cdi.api.Subscription;
import org.jboss.errai.ioc.client.api.Disposer;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.dmn.client.events.EditExpressionEvent;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers.GuidedTourGridObserver;

public class Type_factory__o_k_w_c_d_w_k_c_c_t_o_GuidedTourGridObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourGridObserver> { public Type_factory__o_k_w_c_d_w_k_c_c_t_o_GuidedTourGridObserver__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GuidedTourGridObserver.class, "Type_factory__o_k_w_c_d_w_k_c_c_t_o_GuidedTourGridObserver__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GuidedTourGridObserver.class, GuidedTourObserver.class, Object.class });
  }

  public GuidedTourGridObserver createInstance(final ContextManager contextManager) {
    final GuidedTourUtils _guidedTourUtils_2 = (GuidedTourUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_t_c_GuidedTourUtils__quals__j_e_i_Any_j_e_i_Default");
    final DMNGraphUtils _dmnGraphUtils_1 = (DMNGraphUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_c_g_DMNGraphUtils__quals__j_e_i_Any_j_e_i_Default");
    final Disposer<GuidedTourGridObserver> _disposer_0 = (Disposer) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_Disposer__quals__Universal", new Class[] { GuidedTourGridObserver.class }, new Annotation[] { });
    final GuidedTourGridObserver instance = new GuidedTourGridObserver(_disposer_0, _dmnGraphUtils_1, _guidedTourUtils_2);
    registerDependentScopedReference(instance, _guidedTourUtils_2);
    registerDependentScopedReference(instance, _dmnGraphUtils_1);
    registerDependentScopedReference(instance, _disposer_0);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onEditExpressionEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.dmn.client.events.EditExpressionEvent", new AbstractCDIEventCallback<EditExpressionEvent>() {
      public void fireEvent(final EditExpressionEvent event) {
        instance.onEditExpressionEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.events.EditExpressionEvent []";
      }
    }));
    thisInstance.setReference(instance, "onExpressionEditorChangedSubscription", CDI.subscribeLocal("org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged", new AbstractCDIEventCallback<ExpressionEditorChanged>() {
      public void fireEvent(final ExpressionEditorChanged event) {
        instance.onExpressionEditorChanged(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.dmn.client.widgets.grid.model.ExpressionEditorChanged []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((GuidedTourGridObserver) instance, contextManager);
  }

  public void destroyInstanceHelper(final GuidedTourGridObserver instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onEditExpressionEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onExpressionEditorChangedSubscription", Subscription.class)).remove();
  }
}