package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.PerspectiveDropDown;

public class Type_factory__o_u_e_w_c_c_d_PerspectiveDropDown__quals__j_e_i_Any_j_e_i_Default extends Factory<PerspectiveDropDown> { public Type_factory__o_u_e_w_c_c_d_PerspectiveDropDown__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PerspectiveDropDown.class, "Type_factory__o_u_e_w_c_c_d_PerspectiveDropDown__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PerspectiveDropDown.class, Object.class, IsWidget.class });
  }

  public PerspectiveDropDown createInstance(final ContextManager contextManager) {
    final ActivityBeansCache _activityBeansCache_0 = (ActivityBeansCache) contextManager.getInstance("Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default");
    final LiveSearchDropDown _liveSearchDropDown_1 = (LiveSearchDropDown) contextManager.getInstance("Type_factory__o_u_e_w_c_c_d_LiveSearchDropDown__quals__j_e_i_Any_j_e_i_Default");
    final PerspectiveDropDown instance = new PerspectiveDropDown(_activityBeansCache_0, _liveSearchDropDown_1);
    registerDependentScopedReference(instance, _liveSearchDropDown_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final PerspectiveDropDown instance) {
    PerspectiveDropDown_init(instance);
  }

  public native static void PerspectiveDropDown_init(PerspectiveDropDown instance) /*-{
    instance.@org.uberfire.ext.widgets.common.client.dropdown.PerspectiveDropDown::init()();
  }-*/;
}