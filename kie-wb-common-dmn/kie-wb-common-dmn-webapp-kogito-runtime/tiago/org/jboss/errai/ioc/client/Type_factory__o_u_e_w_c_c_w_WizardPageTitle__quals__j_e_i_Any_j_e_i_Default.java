package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsRenderable;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageSelectedEvent;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageTitle;

public class Type_factory__o_u_e_w_c_c_w_WizardPageTitle__quals__j_e_i_Any_j_e_i_Default extends Factory<WizardPageTitle> { public Type_factory__o_u_e_w_c_c_w_WizardPageTitle__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(WizardPageTitle.class, "Type_factory__o_u_e_w_c_c_w_WizardPageTitle__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { WizardPageTitle.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class });
  }

  public WizardPageTitle createInstance(final ContextManager contextManager) {
    final WizardPageTitle instance = new WizardPageTitle();
    setIncompleteInstance(instance);
    final Event WizardPageTitle_selectPageEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { WizardPageSelectedEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, WizardPageTitle_selectPageEvent);
    WizardPageTitle_Event_selectPageEvent(instance, WizardPageTitle_selectPageEvent);
    setIncompleteInstance(null);
    return instance;
  }

  native static Event WizardPageTitle_Event_selectPageEvent(WizardPageTitle instance) /*-{
    return instance.@org.uberfire.ext.widgets.core.client.wizards.WizardPageTitle::selectPageEvent;
  }-*/;

  native static void WizardPageTitle_Event_selectPageEvent(WizardPageTitle instance, Event<WizardPageSelectedEvent> value) /*-{
    instance.@org.uberfire.ext.widgets.core.client.wizards.WizardPageTitle::selectPageEvent = value;
  }-*/;
}