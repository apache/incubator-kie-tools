package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.IsWidget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown.View;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchSelectorItem;

public class Type_factory__o_u_e_w_c_c_d_LiveSearchDropDown__quals__j_e_i_Any_j_e_i_Default extends Factory<LiveSearchDropDown> { public Type_factory__o_u_e_w_c_c_d_LiveSearchDropDown__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LiveSearchDropDown.class, "Type_factory__o_u_e_w_c_c_d_LiveSearchDropDown__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LiveSearchDropDown.class, Object.class, IsWidget.class });
  }

  public LiveSearchDropDown createInstance(final ContextManager contextManager) {
    final View _view_0 = (LiveSearchDropDownView) contextManager.getInstance("Type_factory__o_u_e_w_c_c_d_LiveSearchDropDownView__quals__j_e_i_Any_j_e_i_Default");
    final ManagedInstance<LiveSearchSelectorItem> _liveSearchSelectorItems_1 = (ManagedInstance) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_i_c_a_ManagedInstance__quals__Universal", new Class[] { LiveSearchSelectorItem.class }, new Annotation[] { });
    final LiveSearchDropDown instance = new LiveSearchDropDown(_view_0, _liveSearchSelectorItems_1);
    registerDependentScopedReference(instance, _view_0);
    registerDependentScopedReference(instance, _liveSearchSelectorItems_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((LiveSearchDropDown) instance, contextManager);
  }

  public void destroyInstanceHelper(final LiveSearchDropDown instance, final ContextManager contextManager) {
    instance.destroy();
  }
}