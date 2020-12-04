package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopup;
import org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopup.View;
import org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopupView;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

public class Type_factory__o_k_w_c_s_c_w_m_MarshallingResponsePopup__quals__j_e_i_Any_j_e_i_Default extends Factory<MarshallingResponsePopup> { public Type_factory__o_k_w_c_s_c_w_m_MarshallingResponsePopup__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(MarshallingResponsePopup.class, "Type_factory__o_k_w_c_s_c_w_m_MarshallingResponsePopup__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { MarshallingResponsePopup.class, Object.class });
  }

  public MarshallingResponsePopup createInstance(final ContextManager contextManager) {
    final View _view_0 = (MarshallingResponsePopupView) contextManager.getInstance("Type_factory__o_k_w_c_s_c_w_m_MarshallingResponsePopupView__quals__j_e_i_Any_j_e_i_Default");
    final ClientTranslationService _translationService_1 = (ClientTranslationService) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default");
    final MarshallingResponsePopup instance = new MarshallingResponsePopup(_view_0, _translationService_1);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final MarshallingResponsePopup instance) {
    MarshallingResponsePopup_init(instance);
  }

  public native static void MarshallingResponsePopup_init(MarshallingResponsePopup instance) /*-{
    instance.@org.kie.workbench.common.stunner.client.widgets.marshaller.MarshallingResponsePopup::init()();
  }-*/;
}