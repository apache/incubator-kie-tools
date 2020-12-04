package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonPresenter;
import org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonPresenter.View;
import org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonView;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;

public class Type_factory__o_g_m_c_c_w_b_ViewHideAlertsButtonPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<ViewHideAlertsButtonPresenter> { public Type_factory__o_g_m_c_c_w_b_ViewHideAlertsButtonPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ViewHideAlertsButtonPresenter.class, "Type_factory__o_g_m_c_c_w_b_ViewHideAlertsButtonPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ViewHideAlertsButtonPresenter.class, Object.class });
  }

  public ViewHideAlertsButtonPresenter createInstance(final ContextManager contextManager) {
    final View _view_1 = (ViewHideAlertsButtonView) contextManager.getInstance("Type_factory__o_g_m_c_c_w_b_ViewHideAlertsButtonView__quals__j_e_i_Any_j_e_i_Default");
    final PlaceManager _placeManager_0 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final ViewHideAlertsButtonPresenter instance = new ViewHideAlertsButtonPresenter(_placeManager_0, _view_1);
    registerDependentScopedReference(instance, _view_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final ViewHideAlertsButtonPresenter instance) {
    instance.init();
  }
}