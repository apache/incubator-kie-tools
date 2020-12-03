package org.jboss.errai.ioc.client;

import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.HasMouseWheelHandlers;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.Dependent;
import org.gwtbootstrap3.client.ui.Label;
import org.gwtbootstrap3.client.ui.base.AbstractTextWidget;
import org.gwtbootstrap3.client.ui.base.HasId;
import org.gwtbootstrap3.client.ui.base.HasInlineStyle;
import org.gwtbootstrap3.client.ui.base.HasPull;
import org.gwtbootstrap3.client.ui.base.HasResponsiveness;
import org.gwtbootstrap3.client.ui.base.HasType;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.kie.workbench.common.widgets.client.handlers.PathLabel;
import org.uberfire.backend.vfs.VFSService;

public class Type_factory__o_k_w_c_w_c_h_PathLabel__quals__j_e_i_Any_j_e_i_Default extends Factory<PathLabel> { public Type_factory__o_k_w_c_w_c_h_PathLabel__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PathLabel.class, "Type_factory__o_k_w_c_w_c_h_PathLabel__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PathLabel.class, Label.class, AbstractTextWidget.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, HasId.class, HasHTML.class, HasText.class, HasResponsiveness.class, HasInlineStyle.class, IsEditor.class, HasPull.class, HasType.class, HasClickHandlers.class, HasAllMouseHandlers.class, HasMouseDownHandlers.class, HasMouseUpHandlers.class, HasMouseOutHandlers.class, HasMouseOverHandlers.class, HasMouseMoveHandlers.class, HasMouseWheelHandlers.class });
  }

  public PathLabel createInstance(final ContextManager contextManager) {
    final PathLabel instance = new PathLabel();
    setIncompleteInstance(instance);
    final Caller PathLabel_vfsService = (Caller) contextManager.getContextualInstance("ContextualProvider_factory__o_j_e_c_c_a_Caller__quals__Universal", new Class[] { VFSService.class }, new Annotation[] { });
    registerDependentScopedReference(instance, PathLabel_vfsService);
    PathLabel_Caller_vfsService(instance, PathLabel_vfsService);
    setIncompleteInstance(null);
    return instance;
  }

  native static Caller PathLabel_Caller_vfsService(PathLabel instance) /*-{
    return instance.@org.kie.workbench.common.widgets.client.handlers.PathLabel::vfsService;
  }-*/;

  native static void PathLabel_Caller_vfsService(PathLabel instance, Caller<VFSService> value) /*-{
    instance.@org.kie.workbench.common.widgets.client.handlers.PathLabel::vfsService = value;
  }-*/;
}