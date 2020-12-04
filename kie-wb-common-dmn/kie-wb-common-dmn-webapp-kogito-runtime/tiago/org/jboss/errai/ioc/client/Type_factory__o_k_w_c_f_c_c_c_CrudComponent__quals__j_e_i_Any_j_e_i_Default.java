package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.crud.client.component.CrudComponent;
import org.kie.workbench.common.forms.crud.client.component.CrudComponent.CrudComponentView;
import org.kie.workbench.common.forms.crud.client.component.CrudComponentViewImpl;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.embedded.EmbeddedFormDisplayer;
import org.kie.workbench.common.forms.crud.client.component.formDisplay.modal.ModalFormDisplayer;

public class Type_factory__o_k_w_c_f_c_c_c_CrudComponent__quals__j_e_i_Any_j_e_i_Default extends Factory<CrudComponent> { public Type_factory__o_k_w_c_f_c_c_c_CrudComponent__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CrudComponent.class, "Type_factory__o_k_w_c_f_c_c_c_CrudComponent__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CrudComponent.class, Object.class, IsWidget.class });
  }

  public CrudComponent createInstance(final ContextManager contextManager) {
    final EmbeddedFormDisplayer _embeddedFormDisplayer_1 = (EmbeddedFormDisplayer) contextManager.getInstance("Type_factory__o_k_w_c_f_c_c_c_f_e_EmbeddedFormDisplayer__quals__j_e_i_Any_j_e_i_Default");
    final TranslationService _translationService_3 = (TranslationService) contextManager.getInstance("Provider_factory__o_j_e_u_c_l_s_TranslationService__quals__j_e_i_Any_j_e_i_Default");
    final CrudComponentView _view_0 = (CrudComponentViewImpl) contextManager.getInstance("Type_factory__o_k_w_c_f_c_c_c_CrudComponentViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final ModalFormDisplayer _modalFormDisplayer_2 = (ModalFormDisplayer) contextManager.getInstance("Type_factory__o_k_w_c_f_c_c_c_f_m_ModalFormDisplayer__quals__j_e_i_Any_j_e_i_Default");
    final CrudComponent instance = new CrudComponent(_view_0, _embeddedFormDisplayer_1, _modalFormDisplayer_2, _translationService_3);
    registerDependentScopedReference(instance, _embeddedFormDisplayer_1);
    registerDependentScopedReference(instance, _translationService_3);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _modalFormDisplayer_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}