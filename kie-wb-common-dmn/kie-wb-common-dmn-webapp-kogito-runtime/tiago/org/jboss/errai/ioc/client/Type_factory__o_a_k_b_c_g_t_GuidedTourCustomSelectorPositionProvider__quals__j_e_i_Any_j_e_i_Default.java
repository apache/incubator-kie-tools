package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourCustomSelectorPositionProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_a_k_b_c_g_t_GuidedTourCustomSelectorPositionProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<GuidedTourCustomSelectorPositionProvider> { public Type_factory__o_a_k_b_c_g_t_GuidedTourCustomSelectorPositionProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GuidedTourCustomSelectorPositionProvider.class, "Type_factory__o_a_k_b_c_g_t_GuidedTourCustomSelectorPositionProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GuidedTourCustomSelectorPositionProvider.class, Object.class });
  }

  public GuidedTourCustomSelectorPositionProvider createInstance(final ContextManager contextManager) {
    final GuidedTourCustomSelectorPositionProvider instance = GuidedTourCustomSelectorPositionProvider_();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public native static GuidedTourCustomSelectorPositionProvider GuidedTourCustomSelectorPositionProvider_() /*-{
    return @org.appformer.kogito.bridge.client.guided.tour.GuidedTourCustomSelectorPositionProvider::new()();
  }-*/;
}