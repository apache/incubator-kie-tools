package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopup;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupView;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupView.Presenter;
import org.guvnor.common.services.project.client.repositories.ConflictingRepositoriesPopupViewImpl;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.security.shared.api.identity.User;

public class Type_factory__o_g_c_s_p_c_r_ConflictingRepositoriesPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<ConflictingRepositoriesPopup> { public Type_factory__o_g_c_s_p_c_r_ConflictingRepositoriesPopup__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ConflictingRepositoriesPopup.class, "Type_factory__o_g_c_s_p_c_r_ConflictingRepositoriesPopup__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ConflictingRepositoriesPopup.class, Object.class, Presenter.class });
  }

  public ConflictingRepositoriesPopup createInstance(final ContextManager contextManager) {
    final ConflictingRepositoriesPopupView _view_1 = (ConflictingRepositoriesPopupViewImpl) contextManager.getInstance("Type_factory__o_g_c_s_p_c_r_ConflictingRepositoriesPopupViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final User _identity_0 = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    final ConflictingRepositoriesPopup instance = new ConflictingRepositoriesPopup(_identity_0, _view_1);
    registerDependentScopedReference(instance, _view_1);
    registerDependentScopedReference(instance, _identity_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}