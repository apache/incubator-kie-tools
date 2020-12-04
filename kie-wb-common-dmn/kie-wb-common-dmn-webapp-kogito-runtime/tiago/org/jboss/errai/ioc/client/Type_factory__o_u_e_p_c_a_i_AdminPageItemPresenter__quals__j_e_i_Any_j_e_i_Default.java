package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.ext.preferences.client.admin.item.AdminPageItemPresenter;
import org.uberfire.ext.preferences.client.admin.item.AdminPageItemPresenter.View;
import org.uberfire.ext.preferences.client.admin.item.AdminPageItemView;
import org.uberfire.ext.preferences.client.event.PreferencesCentralActionsConfigurationEvent;

public class Type_factory__o_u_e_p_c_a_i_AdminPageItemPresenter__quals__j_e_i_Any_j_e_i_Default extends Factory<AdminPageItemPresenter> { public Type_factory__o_u_e_p_c_a_i_AdminPageItemPresenter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AdminPageItemPresenter.class, "Type_factory__o_u_e_p_c_a_i_AdminPageItemPresenter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AdminPageItemPresenter.class, Object.class });
  }

  public AdminPageItemPresenter createInstance(final ContextManager contextManager) {
    final PlaceManager _placeManager_1 = (PlaceManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    final Event<PreferencesCentralActionsConfigurationEvent> _adminPageConfigurationEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PreferencesCentralActionsConfigurationEvent.class }, new Annotation[] { });
    final View _view_0 = (AdminPageItemView) contextManager.getInstance("Type_factory__o_u_e_p_c_a_i_AdminPageItemView__quals__j_e_i_Any_j_e_i_Default");
    final AdminPageItemPresenter instance = new AdminPageItemPresenter(_view_0, _placeManager_1, _adminPageConfigurationEvent_2);
    registerDependentScopedReference(instance, _adminPageConfigurationEvent_2);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}