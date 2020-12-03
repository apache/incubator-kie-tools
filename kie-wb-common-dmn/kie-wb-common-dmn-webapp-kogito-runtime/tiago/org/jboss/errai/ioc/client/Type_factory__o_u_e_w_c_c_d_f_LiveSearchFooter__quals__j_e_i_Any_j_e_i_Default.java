package org.jboss.errai.ioc.client;

import javax.enterprise.context.Dependent;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooter;
import org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterView;
import org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterView.Presenter;
import org.uberfire.ext.widgets.common.client.dropdown.footer.LiveSearchFooterViewImpl;

public class Type_factory__o_u_e_w_c_c_d_f_LiveSearchFooter__quals__j_e_i_Any_j_e_i_Default extends Factory<LiveSearchFooter> { public Type_factory__o_u_e_w_c_c_d_f_LiveSearchFooter__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LiveSearchFooter.class, "Type_factory__o_u_e_w_c_c_d_f_LiveSearchFooter__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LiveSearchFooter.class, Object.class, Presenter.class, IsElement.class });
  }

  public LiveSearchFooter createInstance(final ContextManager contextManager) {
    final LiveSearchFooterView _view_0 = (LiveSearchFooterViewImpl) contextManager.getInstance("Type_factory__o_u_e_w_c_c_d_f_LiveSearchFooterViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final LiveSearchFooter instance = new LiveSearchFooter(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}