package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchSelectorItem;
import org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItem;
import org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemView;
import org.uberfire.ext.widgets.common.client.dropdown.items.LiveSearchSelectorDropDownItemViewImpl;

public class Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItem__quals__j_e_i_Any_j_e_i_Default extends Factory<LiveSearchSelectorDropDownItem> { public Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItem__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LiveSearchSelectorDropDownItem.class, "Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItem__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LiveSearchSelectorDropDownItem.class, Object.class, LiveSearchSelectorItem.class, IsElement.class });
  }

  public LiveSearchSelectorDropDownItem createInstance(final ContextManager contextManager) {
    final LiveSearchSelectorDropDownItemView _view_0 = (LiveSearchSelectorDropDownItemViewImpl) contextManager.getInstance("Type_factory__o_u_e_w_c_c_d_i_LiveSearchSelectorDropDownItemViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final LiveSearchSelectorDropDownItem instance = new LiveSearchSelectorDropDownItem(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}