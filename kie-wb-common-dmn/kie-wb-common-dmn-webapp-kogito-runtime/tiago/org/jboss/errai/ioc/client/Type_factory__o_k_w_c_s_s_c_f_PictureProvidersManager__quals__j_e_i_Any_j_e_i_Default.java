package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.shapes.client.factory.PictureProvidersManager;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureProvider;

public class Type_factory__o_k_w_c_s_s_c_f_PictureProvidersManager__quals__j_e_i_Any_j_e_i_Default extends Factory<PictureProvidersManager> { public Type_factory__o_k_w_c_s_s_c_f_PictureProvidersManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PictureProvidersManager.class, "Type_factory__o_k_w_c_s_s_c_f_PictureProvidersManager__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PictureProvidersManager.class, Object.class });
  }

  public PictureProvidersManager createInstance(final ContextManager contextManager) {
    final ManagedInstance<PictureProvider> _pictureProviderManagedInstances_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { PictureProvider.class }, new Annotation[] { });
    final PictureProvidersManager instance = new PictureProvidersManager(_pictureProviderManagedInstances_0);
    registerDependentScopedReference(instance, _pictureProviderManagedInstances_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((PictureProvidersManager) instance, contextManager);
  }

  public void destroyInstanceHelper(final PictureProvidersManager instance, final ContextManager contextManager) {
    instance.destroy();
  }

  public void invokePostConstructs(final PictureProvidersManager instance) {
    instance.init();
  }
}