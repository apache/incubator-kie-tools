package org.jboss.errai.ioc.client;

import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.event.dom.client.HasAllDragAndDropHandlers;
import com.google.gwt.event.dom.client.HasAllGestureHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.HasDragEndHandlers;
import com.google.gwt.event.dom.client.HasDragEnterHandlers;
import com.google.gwt.event.dom.client.HasDragHandlers;
import com.google.gwt.event.dom.client.HasDragLeaveHandlers;
import com.google.gwt.event.dom.client.HasDragOverHandlers;
import com.google.gwt.event.dom.client.HasDragStartHandlers;
import com.google.gwt.event.dom.client.HasDropHandlers;
import com.google.gwt.event.dom.client.HasGestureChangeHandlers;
import com.google.gwt.event.dom.client.HasGestureEndHandlers;
import com.google.gwt.event.dom.client.HasGestureStartHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.HasMouseWheelHandlers;
import com.google.gwt.event.dom.client.HasTouchCancelHandlers;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.HasTouchMoveHandlers;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.i18n.shared.HasDirectionEstimator;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HasAutoHorizontalAlignment;
import com.google.gwt.user.client.ui.HasDirectionalText;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWordWrap;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LabelBase;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class ExtensionProvided_factory__c_g_g_u_c_u_Label__quals__j_e_i_Any_j_e_i_Default extends Factory<Label> { public ExtensionProvided_factory__c_g_g_u_c_u_Label__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(Label.class, "ExtensionProvided_factory__c_g_g_u_c_u_Label__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { Label.class, LabelBase.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, HasWordWrap.class, HasDirectionEstimator.class, HasAutoHorizontalAlignment.class, HasHorizontalAlignment.class, HasDirectionalText.class, HasText.class, HasDirection.class, HasClickHandlers.class, HasDoubleClickHandlers.class, SourcesClickEvents.class, SourcesMouseEvents.class, HasAllDragAndDropHandlers.class, HasDragEndHandlers.class, HasDragEnterHandlers.class, HasDragLeaveHandlers.class, HasDragHandlers.class, HasDragOverHandlers.class, HasDragStartHandlers.class, HasDropHandlers.class, HasAllGestureHandlers.class, HasGestureStartHandlers.class, HasGestureChangeHandlers.class, HasGestureEndHandlers.class, HasAllMouseHandlers.class, HasMouseDownHandlers.class, HasMouseUpHandlers.class, HasMouseOutHandlers.class, HasMouseOverHandlers.class, HasMouseMoveHandlers.class, HasMouseWheelHandlers.class, HasAllTouchHandlers.class, HasTouchStartHandlers.class, HasTouchMoveHandlers.class, HasTouchEndHandlers.class, HasTouchCancelHandlers.class, IsEditor.class });
  }

  public Label createInstance(final ContextManager contextManager) {
    return new Label();
  }
}