package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.preferences.client.admin.category.AdminPageCategoryPresenter;
import org.uberfire.ext.preferences.client.admin.category.AdminPageCategoryPresenter.View;
import org.uberfire.ext.preferences.client.admin.category.AdminPageCategoryView;
import org.uberfire.ext.preferences.client.admin.item.AdminPageItemPresenter;

public class Type_factory__o_u_e_p_c_a_c_AdminPageCategoryPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPageCategoryPresenter> { public Type_factory__o_u_e_p_c_a_c_AdminPageCategoryPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AdminPageCategoryPresenter.class, "Type_factory__o_u_e_p_c_a_c_AdminPageCategoryPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AdminPageCategoryPresenter.class, Object.class });
  }

  public AdminPageCategoryPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (AdminPageCategoryView) contextManager.getInstance("Type_factory__o_u_e_p_c_a_c_AdminPageCategoryView__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<AdminPageItemPresenter> _adminPageItemPresenterProvider_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { AdminPageItemPresenter.class }, new Annotation[] { });
    final AdminPageCategoryPresenter instance = new AdminPageCategoryPresenter(_view_0, _adminPageItemPresenterProvider_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _adminPageItemPresenterProvider_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}