package org.jboss.errai.ioc.client;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HasWidgets.ForIsWidget;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.Dependent;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.base.AbstractAnchorListItem;
import org.gwtbootstrap3.client.ui.base.AbstractListItem;
import org.gwtbootstrap3.client.ui.base.ComplexWidget;
import org.gwtbootstrap3.client.ui.base.HasActive;
import org.gwtbootstrap3.client.ui.base.HasBadge;
import org.gwtbootstrap3.client.ui.base.HasDataToggle;
import org.gwtbootstrap3.client.ui.base.HasHref;
import org.gwtbootstrap3.client.ui.base.HasIcon;
import org.gwtbootstrap3.client.ui.base.HasIconPosition;
import org.gwtbootstrap3.client.ui.base.HasId;
import org.gwtbootstrap3.client.ui.base.HasInlineStyle;
import org.gwtbootstrap3.client.ui.base.HasPull;
import org.gwtbootstrap3.client.ui.base.HasResponsiveness;
import org.gwtbootstrap3.client.ui.base.HasTarget;
import org.gwtbootstrap3.client.ui.base.HasTargetHistoryToken;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.uberfire.client.menu.SplashScreenMenuPresenter.View;
import org.uberfire.client.mvp.HasPresenter;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.views.pfly.menu.SplashScreenMenuView;

public class Type_factory__o_u_c_v_p_m_SplashScreenMenuView__quals__j_e_i_Any_j_e_i_Default extends Factory<SplashScreenMenuView> { public Type_factory__o_u_c_v_p_m_SplashScreenMenuView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(SplashScreenMenuView.class, "Type_factory__o_u_c_v_p_m_SplashScreenMenuView__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { SplashScreenMenuView.class, AnchorListItem.class, AbstractAnchorListItem.class, AbstractListItem.class, ComplexWidget.class, ComplexPanel.class, Panel.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, ForIsWidget.class, HasWidgets.class, Iterable.class, com.google.gwt.user.client.ui.IndexedPanel.ForIsWidget.class, IndexedPanel.class, HasId.class, HasResponsiveness.class, HasInlineStyle.class, HasPull.class, HasEnabled.class, HasActive.class, HasHref.class, HasTargetHistoryToken.class, HasClickHandlers.class, Focusable.class, HasDataToggle.class, HasIcon.class, HasIconPosition.class, HasBadge.class, HasTarget.class, HasText.class, View.class, UberView.class, HasPresenter.class });
  }

  public SplashScreenMenuView createInstance(final ContextManager contextManager) {
    final SplashScreenMenuView instance = new SplashScreenMenuView();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }
}