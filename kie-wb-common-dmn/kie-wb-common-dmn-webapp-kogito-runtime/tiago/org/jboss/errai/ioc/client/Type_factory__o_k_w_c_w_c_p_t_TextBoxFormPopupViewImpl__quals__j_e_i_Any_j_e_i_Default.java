package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HasWidgets.ForIsWidget;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.gwtbootstrap3.client.ui.IsClosable;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.base.ComplexWidget;
import org.gwtbootstrap3.client.ui.base.HasId;
import org.gwtbootstrap3.client.ui.base.HasInlineStyle;
import org.gwtbootstrap3.client.ui.base.HasPull;
import org.gwtbootstrap3.client.ui.base.HasResponsiveness;
import org.gwtbootstrap3.client.ui.html.Div;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.popups.text.FormPopupView;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupView;
import org.kie.workbench.common.widgets.client.popups.text.TextBoxFormPopupViewImpl;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

public class Type_factory__o_k_w_c_w_c_p_t_TextBoxFormPopupViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<TextBoxFormPopupViewImpl> { public Type_factory__o_k_w_c_w_c_p_t_TextBoxFormPopupViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(TextBoxFormPopupViewImpl.class, "Type_factory__o_k_w_c_w_c_p_t_TextBoxFormPopupViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { TextBoxFormPopupViewImpl.class, BaseModal.class, Modal.class, Div.class, ComplexWidget.class, ComplexPanel.class, Panel.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, ForIsWidget.class, HasWidgets.class, Iterable.class, com.google.gwt.user.client.ui.IndexedPanel.ForIsWidget.class, IndexedPanel.class, HasId.class, HasResponsiveness.class, HasInlineStyle.class, HasPull.class, IsClosable.class, TextBoxFormPopupView.class, FormPopupView.class });
  }

  public TextBoxFormPopupViewImpl createInstance(final ContextManager contextManager) {
    final TextBoxFormPopupViewImpl instance = new TextBoxFormPopupViewImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}