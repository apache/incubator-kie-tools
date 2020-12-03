package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.ext.editor.commons.client.menu.DownloadMenuItemBuilder;

public class Type_factory__o_u_e_e_c_c_m_DownloadMenuItemBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<DownloadMenuItemBuilder> { public Type_factory__o_u_e_e_c_c_m_DownloadMenuItemBuilder__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DownloadMenuItemBuilder.class, "Type_factory__o_u_e_e_c_c_m_DownloadMenuItemBuilder__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DownloadMenuItemBuilder.class, Object.class });
  }

  public DownloadMenuItemBuilder createInstance(final ContextManager contextManager) {
    final TranslationService _translationService_0 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final DownloadMenuItemBuilder instance = new DownloadMenuItemBuilder(_translationService_0);
    registerDependentScopedReference(instance, _translationService_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}