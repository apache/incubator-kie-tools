package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.preferences.client.admin.AdminPagePresenter;
import org.uberfire.ext.preferences.client.admin.AdminPagePresenter.View;
import org.uberfire.ext.preferences.client.admin.AdminPageView;
import org.uberfire.ext.preferences.client.admin.category.AdminPageCategoryPresenter;
import org.uberfire.ext.preferences.client.admin.page.AdminPage;
import org.uberfire.ext.preferences.client.admin.page.AdminPageImpl;
import org.uberfire.workbench.events.NotificationEvent;

public class Type_factory__o_u_e_p_c_a_AdminPagePresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPagePresenter> { public Type_factory__o_u_e_p_c_a_AdminPagePresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AdminPagePresenter.class, "Type_factory__o_u_e_p_c_a_AdminPagePresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AdminPagePresenter.class, Object.class });
  }

  public AdminPagePresenter createInstance(final ContextManager contextManager) {
    final ManagedInstance<AdminPageCategoryPresenter> _categoryPresenterProvider_2 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { AdminPageCategoryPresenter.class }, new Annotation[] { });
    final AdminPage _adminPage_1 = (AdminPageImpl) contextManager.getInstance("Type_factory__o_u_e_p_c_a_p_AdminPageImpl__quals__j_e_i_Any_j_e_i_Default");
    final View _view_0 = (AdminPageView) contextManager.getInstance("Type_factory__o_u_e_p_c_a_AdminPageView__quals__j_e_i_Any_j_e_i_Default");
    final Event<NotificationEvent> _notification_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NotificationEvent.class }, new Annotation[] { });
    final AdminPagePresenter instance = new AdminPagePresenter(_view_0, _adminPage_1, _categoryPresenterProvider_2, _notification_3);
    registerDependentScopedReference(instance, _categoryPresenterProvider_2);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _notification_3);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}