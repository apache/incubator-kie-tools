package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.IsWidget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButton;
import org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonView;
import org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonView.Presenter;
import org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonViewImpl;

public class Type_factory__o_u_e_e_c_c_h_VersionMenuDropDownButton__quals__j_e_i_Any_j_e_i_Default extends Factory<VersionMenuDropDownButton> { public Type_factory__o_u_e_e_c_c_h_VersionMenuDropDownButton__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(VersionMenuDropDownButton.class, "Type_factory__o_u_e_e_c_c_h_VersionMenuDropDownButton__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { VersionMenuDropDownButton.class, Object.class, Presenter.class, HasEnabled.class, IsWidget.class });
  }

  public VersionMenuDropDownButton createInstance(final ContextManager contextManager) {
    final VersionMenuDropDownButtonView _view_0 = (VersionMenuDropDownButtonViewImpl) contextManager.getInstance("Type_factory__o_u_e_e_c_c_h_VersionMenuDropDownButtonViewImpl__quals__j_e_i_Any_j_e_i_Default");
    final VersionMenuDropDownButton instance = new VersionMenuDropDownButton(_view_0);
    registerDependentScopedReference(instance, _view_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}