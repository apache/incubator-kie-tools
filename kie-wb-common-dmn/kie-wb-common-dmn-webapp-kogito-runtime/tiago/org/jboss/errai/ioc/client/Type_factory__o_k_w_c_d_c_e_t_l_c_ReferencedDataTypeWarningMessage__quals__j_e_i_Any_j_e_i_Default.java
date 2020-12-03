package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.client.editors.types.listview.confirmation.ReferencedDataTypeWarningMessage;

public class Type_factory__o_k_w_c_d_c_e_t_l_c_ReferencedDataTypeWarningMessage__quals__j_e_i_Any_j_e_i_Default extends Factory<ReferencedDataTypeWarningMessage> { public Type_factory__o_k_w_c_d_c_e_t_l_c_ReferencedDataTypeWarningMessage__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ReferencedDataTypeWarningMessage.class, "Type_factory__o_k_w_c_d_c_e_t_l_c_ReferencedDataTypeWarningMessage__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ReferencedDataTypeWarningMessage.class, Object.class });
  }

  public ReferencedDataTypeWarningMessage createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final ReferencedDataTypeWarningMessage instance = new ReferencedDataTypeWarningMessage(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}