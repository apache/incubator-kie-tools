package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourObserver;
import org.appformer.kogito.bridge.client.guided.tour.observers.GlobalHTMLObserver;
import org.jboss.errai.ioc.client.api.Disposer;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_a_k_b_c_g_t_o_GlobalHTMLObserver__quals__j_e_i_Any_j_e_i_Default extends Factory<GlobalHTMLObserver> { public Type_factory__o_a_k_b_c_g_t_o_GlobalHTMLObserver__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GlobalHTMLObserver.class, "Type_factory__o_a_k_b_c_g_t_o_GlobalHTMLObserver__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GlobalHTMLObserver.class, GuidedTourObserver.class, Object.class });
  }

  public GlobalHTMLObserver createInstance(final ContextManager contextManager) {
    final Disposer<GlobalHTMLObserver> _selfDisposer_0 = (Disposer) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_Disposer__quals__Universal", new Class[] { GlobalHTMLObserver.class }, new Annotation[] { });
    final GlobalHTMLObserver instance = new GlobalHTMLObserver(_selfDisposer_0);
    registerDependentScopedReference(instance, _selfDisposer_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final GlobalHTMLObserver instance) {
    instance.init();
  }
}