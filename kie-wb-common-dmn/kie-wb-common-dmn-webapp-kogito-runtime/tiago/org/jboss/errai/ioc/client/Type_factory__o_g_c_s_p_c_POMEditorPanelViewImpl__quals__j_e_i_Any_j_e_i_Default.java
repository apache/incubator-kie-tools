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
import org.guvnor.common.services.project.client.GAVEditor;
import org.guvnor.common.services.project.client.POMEditorPanelView;
import org.guvnor.common.services.project.client.POMEditorPanelViewImpl;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;
import org.uberfire.workbench.events.NotificationEvent;

public class Type_factory__o_g_c_s_p_c_POMEditorPanelViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<POMEditorPanelViewImpl> { public Type_factory__o_g_c_s_p_c_POMEditorPanelViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(POMEditorPanelViewImpl.class, "Type_factory__o_g_c_s_p_c_POMEditorPanelViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { POMEditorPanelViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, POMEditorPanelView.class, HasBusyIndicator.class });
  }

  public POMEditorPanelViewImpl createInstance(final ContextManager contextManager) {
    final GAVEditor _gavEditor_2 = (GAVEditor) contextManager.getInstance("Type_factory__o_g_c_s_p_c_GAVEditor__quals__j_e_i_Any_j_e_i_Default");
    final Event<NotificationEvent> _notificationEvent_0 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NotificationEvent.class }, new Annotation[] { });
    final GAVEditor _parentGavEditor_1 = (GAVEditor) contextManager.getInstance("Type_factory__o_g_c_s_p_c_GAVEditor__quals__j_e_i_Any_j_e_i_Default");
    final POMEditorPanelViewImpl instance = new POMEditorPanelViewImpl(_notificationEvent_0, _parentGavEditor_1, _gavEditor_2);
    registerDependentScopedReference(instance, _gavEditor_2);
    registerDependentScopedReference(instance, _notificationEvent_0);
    registerDependentScopedReference(instance, _parentGavEditor_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}