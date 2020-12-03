package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRendererManager;
import org.kie.workbench.common.forms.dynamic.client.rendering.FieldRendererManagerImpl;

public class Type_factory__o_k_w_c_f_d_c_r_FieldRendererManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<FieldRendererManagerImpl> { public Type_factory__o_k_w_c_f_d_c_r_FieldRendererManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(FieldRendererManagerImpl.class, "Type_factory__o_k_w_c_f_d_c_r_FieldRendererManagerImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { FieldRendererManagerImpl.class, Object.class, FieldRendererManager.class });
  }

  public FieldRendererManagerImpl createInstance(final ContextManager contextManager) {
    final ManagedInstance<FieldRenderer> _renderers_0 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { FieldRenderer.class }, new Annotation[] { });
    final FieldRendererManagerImpl instance = new FieldRendererManagerImpl(_renderers_0);
    registerDependentScopedReference(instance, _renderers_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((FieldRendererManagerImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final FieldRendererManagerImpl instance, final ContextManager contextManager) {
    instance.destroy();
  }
}