package org.jboss.errai.ioc.client;

import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.gwtbootstrap3.client.ui.base.HasId;
import org.gwtbootstrap3.client.ui.base.HasReadOnly;
import org.gwtbootstrap3.client.ui.base.HasResponsiveness;
import org.gwtbootstrap3.client.ui.base.HasSize;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.ToggleSwitchBase;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class ExtensionProvided_factory__o_g_e_t_c_u_ToggleSwitch__quals__j_e_i_Any_j_e_i_Default extends Factory<ToggleSwitch> { public ExtensionProvided_factory__o_g_e_t_c_u_ToggleSwitch__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ToggleSwitch.class, "ExtensionProvided_factory__o_g_e_t_c_u_ToggleSwitch__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ToggleSwitch.class, ToggleSwitchBase.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, HasSize.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class, HasEnabled.class, HasId.class, HasName.class, HasReadOnly.class, HasResponsiveness.class, IsEditor.class });
  }

  public ToggleSwitch createInstance(final ContextManager contextManager) {
    return new ToggleSwitch();
  }
}