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
import javax.enterprise.context.Dependent;
import org.guvnor.common.services.project.client.GAVEditorView;
import org.guvnor.common.services.project.client.GAVEditorViewImpl;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class Type_factory__o_g_c_s_p_c_GAVEditorViewImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<GAVEditorViewImpl> { public Type_factory__o_g_c_s_p_c_GAVEditorViewImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(GAVEditorViewImpl.class, "Type_factory__o_g_c_s_p_c_GAVEditorViewImpl__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { GAVEditorViewImpl.class, Composite.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, IsRenderable.class, GAVEditorView.class });
  }

  public GAVEditorViewImpl createInstance(final ContextManager contextManager) {
    final GAVEditorViewImpl instance = new GAVEditorViewImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}