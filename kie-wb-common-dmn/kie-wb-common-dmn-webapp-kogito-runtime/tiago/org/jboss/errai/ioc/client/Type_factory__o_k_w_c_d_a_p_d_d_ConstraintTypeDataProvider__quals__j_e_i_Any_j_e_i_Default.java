package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.property.dmn.dataproviders.ConstraintTypeDataProvider;
import org.kie.workbench.common.forms.dynamic.model.config.SelectorDataProvider;

public class Type_factory__o_k_w_c_d_a_p_d_d_ConstraintTypeDataProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<ConstraintTypeDataProvider> { public Type_factory__o_k_w_c_d_a_p_d_d_ConstraintTypeDataProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ConstraintTypeDataProvider.class, "Type_factory__o_k_w_c_d_a_p_d_d_ConstraintTypeDataProvider__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ConstraintTypeDataProvider.class, Object.class, SelectorDataProvider.class });
  }

  public ConstraintTypeDataProvider createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ConstraintTypeDataProvider instance = new ConstraintTypeDataProvider(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ConstraintTypeDataProvider instance) {
    ConstraintTypeDataProvider_init(instance);
  }

  public native static void ConstraintTypeDataProvider_init(ConstraintTypeDataProvider instance) /*-{
    instance.@org.kie.workbench.common.dmn.api.property.dmn.dataproviders.ConstraintTypeDataProvider::init()();
  }-*/;
}