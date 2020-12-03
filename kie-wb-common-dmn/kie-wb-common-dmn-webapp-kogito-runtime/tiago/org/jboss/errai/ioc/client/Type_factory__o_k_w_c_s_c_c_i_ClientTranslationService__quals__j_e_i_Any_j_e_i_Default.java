package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.inject.Singleton;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.stunner.core.client.api.GlobalSessionManager;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.i18n.AbstractTranslationService;
import org.kie.workbench.common.stunner.core.i18n.StunnerTranslationService;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.validation.DiagramElementNameProvider;

public class Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default extends Factory<ClientTranslationService> { public Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ClientTranslationService.class, "Type_factory__o_k_w_c_s_c_c_i_ClientTranslationService__quals__j_e_i_Any_j_e_i_Default", Singleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ClientTranslationService.class, AbstractTranslationService.class, Object.class, StunnerTranslationService.class });
  }

  public ClientTranslationService createInstance(final ContextManager contextManager) {
    final TranslationService _erraiTranslationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils _definitionUtils_3 = (DefinitionUtils) contextManager.getInstance("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<DiagramElementNameProvider> _elementNameProviders_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { DiagramElementNameProvider.class }, new Annotation[] { });
    final SessionManager _sessionManager_2 = (GlobalSessionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_GlobalSessionManager__quals__j_e_i_Any_j_e_i_Default");
    final ClientTranslationService instance = new ClientTranslationService(_erraiTranslationService_0, _elementNameProviders_1, _sessionManager_2, _definitionUtils_3);
    registerDependentScopedReference(instance, _erraiTranslationService_0);
    registerDependentScopedReference(instance, _elementNameProviders_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}