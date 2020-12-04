package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.popups.about.AboutPopup;
import org.kie.workbench.common.widgets.client.popups.about.AboutPopup.View;
import org.kie.workbench.common.widgets.client.popups.about.AboutPopupConfig;
import org.kie.workbench.common.widgets.client.popups.about.AboutPopupView;

public class Type_factory__o_k_w_c_w_c_p_a_AboutPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<AboutPopup> { public Type_factory__o_k_w_c_w_c_p_a_AboutPopup__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AboutPopup.class, "Type_factory__o_k_w_c_w_c_p_a_AboutPopup__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AboutPopup.class, Object.class });
  }

  public AboutPopup createInstance(final ContextManager contextManager) {
    final ManagedInstance<AboutPopupConfig> _aboutPopupConfigs_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { AboutPopupConfig.class }, new Annotation[] { });
    final View _view_0 = (AboutPopupView) contextManager.getInstance("Type_factory__o_k_w_c_w_c_p_a_AboutPopupView__quals__j_e_i_Any_j_e_i_Default");
    final AboutPopup instance = new AboutPopup(_view_0, _aboutPopupConfigs_1);
    registerDependentScopedReference(instance, _aboutPopupConfigs_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final AboutPopup instance) {
    instance.setup();
  }
}