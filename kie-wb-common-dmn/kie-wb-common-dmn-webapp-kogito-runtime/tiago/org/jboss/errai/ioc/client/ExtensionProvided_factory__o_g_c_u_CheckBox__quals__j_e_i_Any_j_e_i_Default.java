package org.jboss.errai.ioc.client;

import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.event.dom.client.HasAllDragAndDropHandlers;
import com.google.gwt.event.dom.client.HasAllFocusHandlers;
import com.google.gwt.event.dom.client.HasAllGestureHandlers;
import com.google.gwt.event.dom.client.HasAllKeyHandlers;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.HasDragEndHandlers;
import com.google.gwt.event.dom.client.HasDragEnterHandlers;
import com.google.gwt.event.dom.client.HasDragHandlers;
import com.google.gwt.event.dom.client.HasDragLeaveHandlers;
import com.google.gwt.event.dom.client.HasDragOverHandlers;
import com.google.gwt.event.dom.client.HasDragStartHandlers;
import com.google.gwt.event.dom.client.HasDropHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasGestureChangeHandlers;
import com.google.gwt.event.dom.client.HasGestureEndHandlers;
import com.google.gwt.event.dom.client.HasGestureStartHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
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
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.i18n.shared.HasDirectionEstimator;
import com.google.gwt.safehtml.client.HasSafeHtml;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasDirectionalSafeHtml;
import com.google.gwt.user.client.ui.HasDirectionalText;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasFocus;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWordWrap;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SourcesClickEvents;
import com.google.gwt.user.client.ui.SourcesFocusEvents;
import com.google.gwt.user.client.ui.SourcesKeyboardEvents;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.base.HasFormValue;
import org.gwtbootstrap3.client.ui.base.HasId;
import org.gwtbootstrap3.client.ui.base.HasPull;
import org.gwtbootstrap3.client.ui.base.HasResponsiveness;
import org.gwtbootstrap3.client.ui.gwt.ButtonBase;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;

public class ExtensionProvided_factory__o_g_c_u_CheckBox__quals__j_e_i_Any_j_e_i_Default extends Factory<CheckBox> { public ExtensionProvided_factory__o_g_c_u_CheckBox__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CheckBox.class, "ExtensionProvided_factory__o_g_c_u_CheckBox__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CheckBox.class, ButtonBase.class, com.google.gwt.user.client.ui.ButtonBase.class, FocusWidget.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, SourcesClickEvents.class, HasClickHandlers.class, HasDoubleClickHandlers.class, HasFocus.class, Focusable.class, SourcesFocusEvents.class, SourcesKeyboardEvents.class, HasEnabled.class, HasAllDragAndDropHandlers.class, HasDragEndHandlers.class, HasDragEnterHandlers.class, HasDragLeaveHandlers.class, HasDragHandlers.class, HasDragOverHandlers.class, HasDragStartHandlers.class, HasDropHandlers.class, HasAllFocusHandlers.class, HasFocusHandlers.class, HasBlurHandlers.class, HasAllGestureHandlers.class, HasGestureStartHandlers.class, HasGestureChangeHandlers.class, HasGestureEndHandlers.class, HasAllKeyHandlers.class, HasKeyUpHandlers.class, HasKeyDownHandlers.class, HasKeyPressHandlers.class, HasAllMouseHandlers.class, HasMouseDownHandlers.class, HasMouseUpHandlers.class, HasMouseOutHandlers.class, HasMouseOverHandlers.class, HasMouseMoveHandlers.class, HasMouseWheelHandlers.class, HasAllTouchHandlers.class, HasTouchStartHandlers.class, HasTouchMoveHandlers.class, HasTouchEndHandlers.class, HasTouchCancelHandlers.class, SourcesMouseEvents.class, HasHTML.class, HasText.class, HasSafeHtml.class, HasResponsiveness.class, HasId.class, HasPull.class, HasName.class, HasValue.class, TakesValue.class, HasValueChangeHandlers.class, HasWordWrap.class, HasDirectionalSafeHtml.class, HasDirectionalText.class, HasDirectionEstimator.class, IsEditor.class, HasFormValue.class, HasChangeHandlers.class });
  }

  public CheckBox createInstance(final ContextManager contextManager) {
    return new CheckBox();
  }
}