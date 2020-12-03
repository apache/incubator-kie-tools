package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourCustomSelectorPositionProvider;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class JsType_factory__o_a_k_b_c_g_t_GuidedTourCustomSelectorPositionProvider__quals__Universal extends Factory<GuidedTourCustomSelectorPositionProvider> { public JsType_factory__o_a_k_b_c_g_t_GuidedTourCustomSelectorPositionProvider__quals__Universal() {
    super(new FactoryHandleImpl(GuidedTourCustomSelectorPositionProvider.class, "JsType_factory__o_a_k_b_c_g_t_GuidedTourCustomSelectorPositionProvider__quals__Universal", Dependent.class, false, null, false));
    handle.setAssignableTypes(new Class[] { GuidedTourCustomSelectorPositionProvider.class, Object.class });
    handle.setQualifiers(new Annotation[] { });
  }

  public GuidedTourCustomSelectorPositionProvider createInstance(final ContextManager contextManager) {
    return (GuidedTourCustomSelectorPositionProvider) WindowInjectionContextStorage.createOrGet().getBean("org.appformer.kogito.bridge.client.guided.tour.GuidedTourCustomSelectorPositionProvider");
  }
}