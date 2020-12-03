package org.jboss.errai.ioc.client;

import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.base.AbstractTextWidget;
import org.gwtbootstrap3.client.ui.base.HasId;
import org.gwtbootstrap3.client.ui.base.HasInlineStyle;
import org.gwtbootstrap3.client.ui.base.HasPull;
import org.gwtbootstrap3.client.ui.base.HasResponsiveness;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class ExtensionProvided_factory__o_g_c_u_HelpBlock__quals__j_e_i_Any_j_e_i_Default extends Factory<HelpBlock> { public ExtensionProvided_factory__o_g_c_u_HelpBlock__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(HelpBlock.class, "ExtensionProvided_factory__o_g_c_u_HelpBlock__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { HelpBlock.class, AbstractTextWidget.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, HasId.class, HasHTML.class, HasText.class, HasResponsiveness.class, HasInlineStyle.class, IsEditor.class, HasPull.class });
  }

  public HelpBlock createInstance(final ContextManager contextManager) {
    return new HelpBlock();
  }
}