package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.guvnor.messageconsole.client.console.widget.button.AlertsButtonMenuItemBuilder;
import org.guvnor.messageconsole.client.console.widget.button.ViewHideAlertsButtonPresenter;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_g_m_c_c_w_b_AlertsButtonMenuItemBuilder__quals__j_e_i_Any_j_e_i_Default extends Factory<AlertsButtonMenuItemBuilder> { public Type_factory__o_g_m_c_c_w_b_AlertsButtonMenuItemBuilder__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(AlertsButtonMenuItemBuilder.class, "Type_factory__o_g_m_c_c_w_b_AlertsButtonMenuItemBuilder__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { AlertsButtonMenuItemBuilder.class, Object.class });
  }

  public AlertsButtonMenuItemBuilder createInstance(final ContextManager contextManager) {
    final ViewHideAlertsButtonPresenter _viewHideAlertsButtonPresenter_0 = (ViewHideAlertsButtonPresenter) contextManager.getInstance("Type_factory__o_g_m_c_c_w_b_ViewHideAlertsButtonPresenter__quals__j_e_i_Any_j_e_i_Default");
    final AlertsButtonMenuItemBuilder instance = new AlertsButtonMenuItemBuilder(_viewHideAlertsButtonPresenter_0);
    registerDependentScopedReference(instance, _viewHideAlertsButtonPresenter_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}