package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonView;
import org.uberfire.ext.editor.commons.client.history.VersionMenuDropDownButtonViewImpl;

public class Type_factory__o_u_e_e_c_c_h_VersionMenuDropDownButtonViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<VersionMenuDropDownButtonViewImpl> { public Type_factory__o_u_e_e_c_c_h_VersionMenuDropDownButtonViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(VersionMenuDropDownButtonViewImpl.class, "Type_factory__o_u_e_e_c_c_h_VersionMenuDropDownButtonViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { VersionMenuDropDownButtonViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, VersionMenuDropDownButtonView.class, HasEnabled.class });
  }

  public VersionMenuDropDownButtonViewImpl createInstance(final ContextManager contextManager) {
    final VersionMenuDropDownButtonViewImpl instance = new VersionMenuDropDownButtonViewImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}