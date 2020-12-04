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
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.observers.GuidedTourGraphObserver;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;

public class Type_factory__o_k_w_c_d_w_k_c_c_t_o_GuidedTourGraphObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourGraphObserver> { public Type_factory__o_k_w_c_d_w_k_c_c_t_o_GuidedTourGraphObserver__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GuidedTourGraphObserver.class, "Type_factory__o_k_w_c_d_w_k_c_c_t_o_GuidedTourGraphObserver__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GuidedTourGraphObserver.class, GuidedTourObserver.class, Object.class });
  }

  public GuidedTourGraphObserver createInstance(final ContextManager contextManager) {
    final GuidedTourUtils _guidedTourUtils_1 = (GuidedTourUtils) contextManager.getInstance("Type_factory__o_k_w_c_d_w_k_c_c_t_c_GuidedTourUtils__quals__j_e_i_Any_j_e_i_Default");
    final Disposer<GuidedTourGraphObserver> _disposer_0 = (Disposer) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_Disposer__quals__Universal", new Class[] { GuidedTourGraphObserver.class }, new Annotation[] { });
    final GuidedTourGraphObserver instance = new GuidedTourGraphObserver(_disposer_0, _guidedTourUtils_1);
    registerDependentScopedReference(instance, _guidedTourUtils_1);
    registerDependentScopedReference(instance, _disposer_0);
    setIncompleteInstance(instance);
    thisInstance.setReference(instance, "onCanvasElementAddedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent", new AbstractCDIEventCallback<CanvasElementAddedEvent>() {
      public void fireEvent(final CanvasElementAddedEvent event) {
        instance.onCanvasElementAddedEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementAddedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onCanvasElementUpdatedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent", new AbstractCDIEventCallback<CanvasElementUpdatedEvent>() {
      public void fireEvent(final CanvasElementUpdatedEvent event) {
        instance.onCanvasElementUpdatedEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent []";
      }
    }));
    thisInstance.setReference(instance, "onAbstractCanvasElementRemovedEventSubscription", CDI.subscribeLocal("org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent", new AbstractCDIEventCallback<CanvasElementRemovedEvent>() {
      public void fireEvent(final CanvasElementRemovedEvent event) {
        instance.onAbstractCanvasElementRemovedEvent(event);
      }
      public String toString() {
        return "Observer: org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementRemovedEvent []";
      }
    }));
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((GuidedTourGraphObserver) instance, contextManager);
  }

  public void destroyInstanceHelper(final GuidedTourGraphObserver instance, final ContextManager contextManager) {
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasElementAddedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onCanvasElementUpdatedEventSubscription", Subscription.class)).remove();
    ((Subscription) thisInstance.getReferenceAs(instance, "onAbstractCanvasElementRemovedEventSubscription", Subscription.class)).remove();
  }
}