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
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
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
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.tables.popup.NewFilterPopup;
import org.uberfire.workbench.events.NotificationEvent;

public class Type_factory__o_u_e_w_c_c_t_p_NewFilterPopup__quals__j_e_i_Any_j_e_i_Default extends Factory<NewFilterPopup> { public Type_factory__o_u_e_w_c_c_t_p_NewFilterPopup__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(NewFilterPopup.class, "Type_factory__o_u_e_w_c_c_t_p_NewFilterPopup__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { NewFilterPopup.class, BaseModal.class, Modal.class, Div.class, ComplexWidget.class, ComplexPanel.class, Panel.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, ForIsWidget.class, HasWidgets.class, Iterable.class, com.google.gwt.user.client.ui.IndexedPanel.ForIsWidget.class, IndexedPanel.class, HasId.class, HasResponsiveness.class, HasInlineStyle.class, HasPull.class, IsClosable.class });
  }

  public NewFilterPopup createInstance(final ContextManager contextManager) {
    final NewFilterPopup instance = new NewFilterPopup();
    setIncompleteInstance(instance);
    final Event NewFilterPopup_notification = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NotificationEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, NewFilterPopup_notification);
    NewFilterPopup_Event_notification(instance, NewFilterPopup_notification);
    setIncompleteInstance(null);
    return instance;
  }

  native static Event NewFilterPopup_Event_notification(NewFilterPopup instance) /*-{
    return instance.@org.uberfire.ext.widgets.common.client.tables.popup.NewFilterPopup::notification;
  }-*/;

  native static void NewFilterPopup_Event_notification(NewFilterPopup instance, Event<NotificationEvent> value) /*-{
    instance.@org.uberfire.ext.widgets.common.client.tables.popup.NewFilterPopup::notification = value;
  }-*/;
}