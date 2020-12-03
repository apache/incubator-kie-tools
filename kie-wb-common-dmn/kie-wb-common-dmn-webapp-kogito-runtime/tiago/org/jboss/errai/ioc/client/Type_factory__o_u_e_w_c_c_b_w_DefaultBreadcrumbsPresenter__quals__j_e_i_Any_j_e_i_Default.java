package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.BreadcrumbPresenter;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsPresenter;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsPresenter.View;
import org.uberfire.ext.widgets.common.client.breadcrumbs.widget.DefaultBreadcrumbsView;

public class Type_factory__o_u_e_w_c_c_b_w_DefaultBreadcrumbsPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<DefaultBreadcrumbsPresenter> { public Type_factory__o_u_e_w_c_c_b_w_DefaultBreadcrumbsPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefaultBreadcrumbsPresenter.class, "Type_factory__o_u_e_w_c_c_b_w_DefaultBreadcrumbsPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefaultBreadcrumbsPresenter.class, Object.class, BreadcrumbPresenter.class });
  }

  public DefaultBreadcrumbsPresenter createInstance(final ContextManager contextManager) {
    final View _view_0 = (DefaultBreadcrumbsView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_b_w_DefaultBreadcrumbsView__quals__j_e_i_Any_j_e_i_Default");
    final DefaultBreadcrumbsPresenter instance = new DefaultBreadcrumbsPresenter(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final DefaultBreadcrumbsPresenter instance) {
    instance.init();
  }
}