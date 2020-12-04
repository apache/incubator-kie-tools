package org.jboss.errai.ioc.client;

import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.HasAttachHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
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
import com.google.gwt.user.client.ui.WidgetCollection;
import java.util.Iterator;
import javax.enterprise.context.ApplicationScoped;
import org.gwtbootstrap3.client.ui.base.ComplexWidget;
import org.gwtbootstrap3.client.ui.base.HasId;
import org.gwtbootstrap3.client.ui.base.HasInlineStyle;
import org.gwtbootstrap3.client.ui.base.HasPull;
import org.gwtbootstrap3.client.ui.base.HasResponsiveness;
import org.gwtbootstrap3.client.ui.constants.DeviceSize;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.html.UnorderedList;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.views.pfly.menu.HasMenuItems;
import org.uberfire.client.views.pfly.menu.UtilityMenuBarView;
import org.uberfire.client.workbench.widgets.menu.HasMenus;
import org.uberfire.client.workbench.widgets.menu.UtilityMenuBarPresenter.View;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.security.impl.authz.DefaultAuthorizationManager;
import org.uberfire.workbench.model.menu.MenuPosition;
import org.uberfire.workbench.model.menu.Menus;

public class Type_factory__o_u_c_v_p_m_UtilityMenuBarView__quals__j_e_i_Any_j_e_i_Default extends Factory<UtilityMenuBarView> { private class Type_factory__o_u_c_v_p_m_UtilityMenuBarView__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends UtilityMenuBarView implements Proxy<UtilityMenuBarView> {
    private final ProxyHelper<UtilityMenuBarView> proxyHelper = new ProxyHelperImpl<UtilityMenuBarView>("Type_factory__o_u_c_v_p_m_UtilityMenuBarView__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final UtilityMenuBarView instance) {

    }

    public UtilityMenuBarView asBeanType() {
      return this;
    }

    public void setInstance(final UtilityMenuBarView instance) {
      proxyHelper.setInstance(instance);
    }

    public void clearInstance() {
      proxyHelper.clearInstance();
    }

    public void setProxyContext(final Context context) {
      proxyHelper.setProxyContext(context);
    }

    public Context getProxyContext() {
      return proxyHelper.getProxyContext();
    }

    public Object unwrap() {
      return proxyHelper.getInstance(this);
    }

    public boolean equals(Object obj) {
      obj = Factory.maybeUnwrapProxy(obj);
      return proxyHelper.getInstance(this).equals(obj);
    }

    @Override public void setup() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setup();
      } else {
        super.setup();
      }
    }

    @Override public void addMenus(Menus menus) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addMenus(menus);
      } else {
        super.addMenus(menus);
      }
    }

    @Override public void addMenuItem(MenuPosition position, Widget menuContent) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addMenuItem(position, menuContent);
      } else {
        super.addMenuItem(position, menuContent);
      }
    }

    @Override public void clear() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clear();
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public Widget asWidget() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final Widget retVal = proxiedInstance.asWidget();
        return retVal;
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }

    @Override public void setUnstyled(boolean unstyled) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setUnstyled(unstyled);
      } else {
        super.setUnstyled(unstyled);
      }
    }

    @Override public boolean isUnstyled() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isUnstyled();
        return retVal;
      } else {
        return super.isUnstyled();
      }
    }

    @Override public void setInline(boolean inline) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setInline(inline);
      } else {
        super.setInline(inline);
      }
    }

    @Override public boolean isInline() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isInline();
        return retVal;
      } else {
        return super.isInline();
      }
    }

    @Override public void add(Widget child) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.add(child);
      } else {
        super.add(child);
      }
    }

    @Override public void insert(Widget child, int beforeIndex) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.insert(child, beforeIndex);
      } else {
        super.insert(child, beforeIndex);
      }
    }

    @Override protected void insert(Widget child, Element container, int beforeIndex, boolean domInsert) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        ComplexWidget_insert_Widget_Element_int_boolean(proxiedInstance, child, container, beforeIndex, domInsert);
      } else {
        super.insert(child, container, beforeIndex, domInsert);
      }
    }

    @Override public boolean remove(Widget w) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.remove(w);
        return retVal;
      } else {
        return super.remove(w);
      }
    }

    @Override public void setId(String id) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setId(id);
      } else {
        super.setId(id);
      }
    }

    @Override public String getId() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getId();
        return retVal;
      } else {
        return super.getId();
      }
    }

    @Override public void setVisibleOn(DeviceSize deviceSize) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setVisibleOn(deviceSize);
      } else {
        super.setVisibleOn(deviceSize);
      }
    }

    @Override public void setHiddenOn(DeviceSize deviceSize) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setHiddenOn(deviceSize);
      } else {
        super.setHiddenOn(deviceSize);
      }
    }

    @Override public void setMarginTop(double margin) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setMarginTop(margin);
      } else {
        super.setMarginTop(margin);
      }
    }

    @Override public void setMarginLeft(double margin) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setMarginLeft(margin);
      } else {
        super.setMarginLeft(margin);
      }
    }

    @Override public void setMarginRight(double margin) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setMarginRight(margin);
      } else {
        super.setMarginRight(margin);
      }
    }

    @Override public void setMarginBottom(double margin) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setMarginBottom(margin);
      } else {
        super.setMarginBottom(margin);
      }
    }

    @Override public void setPaddingTop(double padding) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setPaddingTop(padding);
      } else {
        super.setPaddingTop(padding);
      }
    }

    @Override public void setPaddingLeft(double padding) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setPaddingLeft(padding);
      } else {
        super.setPaddingLeft(padding);
      }
    }

    @Override public void setPaddingRight(double padding) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setPaddingRight(padding);
      } else {
        super.setPaddingRight(padding);
      }
    }

    @Override public void setPaddingBottom(double padding) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setPaddingBottom(padding);
      } else {
        super.setPaddingBottom(padding);
      }
    }

    @Override public void setColor(String color) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setColor(color);
      } else {
        super.setColor(color);
      }
    }

    @Override public void setPull(Pull pull) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setPull(pull);
      } else {
        super.setPull(pull);
      }
    }

    @Override public Pull getPull() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final Pull retVal = proxiedInstance.getPull();
        return retVal;
      } else {
        return super.getPull();
      }
    }

    @Override public Widget getWidget(int index) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final Widget retVal = proxiedInstance.getWidget(index);
        return retVal;
      } else {
        return super.getWidget(index);
      }
    }

    @Override public int getWidgetCount() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getWidgetCount();
        return retVal;
      } else {
        return super.getWidgetCount();
      }
    }

    @Override public int getWidgetIndex(Widget child) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getWidgetIndex(child);
        return retVal;
      } else {
        return super.getWidgetIndex(child);
      }
    }

    @Override public Iterator iterator() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final Iterator retVal = proxiedInstance.iterator();
        return retVal;
      } else {
        return super.iterator();
      }
    }

    @Override public boolean remove(int index) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.remove(index);
        return retVal;
      } else {
        return super.remove(index);
      }
    }

    @Override protected void add(Widget child, com.google.gwt.dom.client.Element container) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        ComplexPanel_add_Widget_Element(proxiedInstance, child, container);
      } else {
        super.add(child, container);
      }
    }

    @Override protected void add(Widget child, Element container) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        ComplexPanel_add_Widget_Element(proxiedInstance, child, container);
      } else {
        super.add(child, container);
      }
    }

    @Override protected int adjustIndex(Widget child, int beforeIndex) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = ComplexPanel_adjustIndex_Widget_int(proxiedInstance, child, beforeIndex);
        return retVal;
      } else {
        return super.adjustIndex(child, beforeIndex);
      }
    }

    @Override protected void checkIndexBoundsForAccess(int index) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        ComplexPanel_checkIndexBoundsForAccess_int(proxiedInstance, index);
      } else {
        super.checkIndexBoundsForAccess(index);
      }
    }

    @Override protected void checkIndexBoundsForInsertion(int index) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        ComplexPanel_checkIndexBoundsForInsertion_int(proxiedInstance, index);
      } else {
        super.checkIndexBoundsForInsertion(index);
      }
    }

    @Override protected WidgetCollection getChildren() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final WidgetCollection retVal = ComplexPanel_getChildren(proxiedInstance);
        return retVal;
      } else {
        return super.getChildren();
      }
    }

    @Override protected void doAttachChildren() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        Panel_doAttachChildren(proxiedInstance);
      } else {
        super.doAttachChildren();
      }
    }

    @Override protected void doDetachChildren() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        Panel_doDetachChildren(proxiedInstance);
      } else {
        super.doDetachChildren();
      }
    }

    @Override public HandlerRegistration addAttachHandler(Handler handler) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final HandlerRegistration retVal = proxiedInstance.addAttachHandler(handler);
        return retVal;
      } else {
        return super.addAttachHandler(handler);
      }
    }

    @Override public void fireEvent(GwtEvent event) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.fireEvent(event);
      } else {
        super.fireEvent(event);
      }
    }

    @Override public Object getLayoutData() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final Object retVal = proxiedInstance.getLayoutData();
        return retVal;
      } else {
        return super.getLayoutData();
      }
    }

    @Override public Widget getParent() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final Widget retVal = proxiedInstance.getParent();
        return retVal;
      } else {
        return super.getParent();
      }
    }

    @Override public boolean isAttached() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isAttached();
        return retVal;
      } else {
        return super.isAttached();
      }
    }

    @Override public void onBrowserEvent(Event event) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onBrowserEvent(event);
      } else {
        super.onBrowserEvent(event);
      }
    }

    @Override public void removeFromParent() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.removeFromParent();
      } else {
        super.removeFromParent();
      }
    }

    @Override public void setLayoutData(Object layoutData) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setLayoutData(layoutData);
      } else {
        super.setLayoutData(layoutData);
      }
    }

    @Override public void sinkEvents(int eventBitsToAdd) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.sinkEvents(eventBitsToAdd);
      } else {
        super.sinkEvents(eventBitsToAdd);
      }
    }

    @Override public void unsinkEvents(int eventBitsToRemove) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.unsinkEvents(eventBitsToRemove);
      } else {
        super.unsinkEvents(eventBitsToRemove);
      }
    }

    @Override protected HandlerManager createHandlerManager() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final HandlerManager retVal = Widget_createHandlerManager(proxiedInstance);
        return retVal;
      } else {
        return super.createHandlerManager();
      }
    }

    @Override protected void delegateEvent(Widget target, GwtEvent event) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        Widget_delegateEvent_Widget_GwtEvent(proxiedInstance, target, event);
      } else {
        super.delegateEvent(target, event);
      }
    }

    @Override protected int getHandlerCount(Type type) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = Widget_getHandlerCount_Type(proxiedInstance, type);
        return retVal;
      } else {
        return super.getHandlerCount(type);
      }
    }

    @Override protected void onAttach() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        Widget_onAttach(proxiedInstance);
      } else {
        super.onAttach();
      }
    }

    @Override protected void onDetach() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        Widget_onDetach(proxiedInstance);
      } else {
        super.onDetach();
      }
    }

    @Override protected void onLoad() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        Widget_onLoad(proxiedInstance);
      } else {
        super.onLoad();
      }
    }

    @Override protected void onUnload() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        Widget_onUnload(proxiedInstance);
      } else {
        super.onUnload();
      }
    }

    @Override public boolean isVisible() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isVisible();
        return retVal;
      } else {
        return super.isVisible();
      }
    }

    @Override public void setVisible(boolean visible) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setVisible(visible);
      } else {
        super.setVisible(visible);
      }
    }

    @Override public String getStyleName() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getStyleName();
        return retVal;
      } else {
        return super.getStyleName();
      }
    }

    @Override public String getStylePrimaryName() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getStylePrimaryName();
        return retVal;
      } else {
        return super.getStylePrimaryName();
      }
    }

    @Override public void setStyleName(String style, boolean add) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setStyleName(style, add);
      } else {
        super.setStyleName(style, add);
      }
    }

    @Override public void setStyleName(String style) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setStyleName(style);
      } else {
        super.setStyleName(style);
      }
    }

    @Override public void setStylePrimaryName(String style) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setStylePrimaryName(style);
      } else {
        super.setStylePrimaryName(style);
      }
    }

    @Override public void addStyleDependentName(String styleSuffix) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addStyleDependentName(styleSuffix);
      } else {
        super.addStyleDependentName(styleSuffix);
      }
    }

    @Override public void addStyleName(String style) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addStyleName(style);
      } else {
        super.addStyleName(style);
      }
    }

    @Override public int getAbsoluteLeft() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getAbsoluteLeft();
        return retVal;
      } else {
        return super.getAbsoluteLeft();
      }
    }

    @Override public int getAbsoluteTop() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getAbsoluteTop();
        return retVal;
      } else {
        return super.getAbsoluteTop();
      }
    }

    @Override public Element getElement() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final Element retVal = proxiedInstance.getElement();
        return retVal;
      } else {
        return super.getElement();
      }
    }

    @Override public int getOffsetHeight() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getOffsetHeight();
        return retVal;
      } else {
        return super.getOffsetHeight();
      }
    }

    @Override public int getOffsetWidth() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getOffsetWidth();
        return retVal;
      } else {
        return super.getOffsetWidth();
      }
    }

    @Override public String getTitle() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getTitle();
        return retVal;
      } else {
        return super.getTitle();
      }
    }

    @Override public void removeStyleDependentName(String styleSuffix) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.removeStyleDependentName(styleSuffix);
      } else {
        super.removeStyleDependentName(styleSuffix);
      }
    }

    @Override public void removeStyleName(String style) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.removeStyleName(style);
      } else {
        super.removeStyleName(style);
      }
    }

    @Override public void setHeight(String height) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setHeight(height);
      } else {
        super.setHeight(height);
      }
    }

    @Override public void setPixelSize(int width, int height) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setPixelSize(width, height);
      } else {
        super.setPixelSize(width, height);
      }
    }

    @Override public void setSize(String width, String height) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setSize(width, height);
      } else {
        super.setSize(width, height);
      }
    }

    @Override public void setStyleDependentName(String styleSuffix, boolean add) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setStyleDependentName(styleSuffix, add);
      } else {
        super.setStyleDependentName(styleSuffix, add);
      }
    }

    @Override public void setTitle(String title) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setTitle(title);
      } else {
        super.setTitle(title);
      }
    }

    @Override public void setWidth(String width) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setWidth(width);
      } else {
        super.setWidth(width);
      }
    }

    @Override public void sinkBitlessEvent(String eventTypeName) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.sinkBitlessEvent(eventTypeName);
      } else {
        super.sinkBitlessEvent(eventTypeName);
      }
    }

    @Override protected Element getStyleElement() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final Element retVal = UIObject_getStyleElement(proxiedInstance);
        return retVal;
      } else {
        return super.getStyleElement();
      }
    }

    @Override protected void onEnsureDebugId(String baseID) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        UIObject_onEnsureDebugId_String(proxiedInstance, baseID);
      } else {
        super.onEnsureDebugId(baseID);
      }
    }

    @Override protected com.google.gwt.dom.client.Element resolvePotentialElement() {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        final com.google.gwt.dom.client.Element retVal = UIObject_resolvePotentialElement(proxiedInstance);
        return retVal;
      } else {
        return super.resolvePotentialElement();
      }
    }

    @Override protected void setElement(Element elem) {
      if (proxyHelper != null) {
        final UtilityMenuBarView proxiedInstance = proxyHelper.getInstance(this);
        UIObject_setElement_Element(proxiedInstance, elem);
      } else {
        super.setElement(elem);
      }
    }
  }
  public Type_factory__o_u_c_v_p_m_UtilityMenuBarView__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UtilityMenuBarView.class, "Type_factory__o_u_c_v_p_m_UtilityMenuBarView__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { UtilityMenuBarView.class, UnorderedList.class, ComplexWidget.class, ComplexPanel.class, Panel.class, Widget.class, UIObject.class, Object.class, HasVisibility.class, EventListener.class, HasAttachHandlers.class, HasHandlers.class, IsWidget.class, ForIsWidget.class, HasWidgets.class, Iterable.class, com.google.gwt.user.client.ui.IndexedPanel.ForIsWidget.class, IndexedPanel.class, HasId.class, HasResponsiveness.class, HasInlineStyle.class, HasPull.class, View.class, HasMenus.class, HasMenuItems.class });
  }

  public UtilityMenuBarView createInstance(final ContextManager contextManager) {
    final UtilityMenuBarView instance = new UtilityMenuBarView();
    setIncompleteInstance(instance);
    final DefaultAuthorizationManager UtilityMenuBarView_authzManager = (DefaultAuthorizationManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultAuthorizationManager__quals__j_e_i_Any_j_e_i_Default");
    UtilityMenuBarView_AuthorizationManager_authzManager(instance, UtilityMenuBarView_authzManager);
    final User UtilityMenuBarView_identity = (User) contextManager.getInstance("Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, UtilityMenuBarView_identity);
    UtilityMenuBarView_User_identity(instance, UtilityMenuBarView_identity);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final UtilityMenuBarView instance) {
    instance.setup();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<UtilityMenuBarView> proxyImpl = new Type_factory__o_u_c_v_p_m_UtilityMenuBarView__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static User UtilityMenuBarView_User_identity(UtilityMenuBarView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.UtilityMenuBarView::identity;
  }-*/;

  native static void UtilityMenuBarView_User_identity(UtilityMenuBarView instance, User value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.UtilityMenuBarView::identity = value;
  }-*/;

  native static AuthorizationManager UtilityMenuBarView_AuthorizationManager_authzManager(UtilityMenuBarView instance) /*-{
    return instance.@org.uberfire.client.views.pfly.menu.UtilityMenuBarView::authzManager;
  }-*/;

  native static void UtilityMenuBarView_AuthorizationManager_authzManager(UtilityMenuBarView instance, AuthorizationManager value) /*-{
    instance.@org.uberfire.client.views.pfly.menu.UtilityMenuBarView::authzManager = value;
  }-*/;

  public native static void ComplexPanel_checkIndexBoundsForInsertion_int(ComplexPanel instance, int a0) /*-{
    instance.@com.google.gwt.user.client.ui.ComplexPanel::checkIndexBoundsForInsertion(I)(a0);
  }-*/;

  public native static HandlerManager Widget_createHandlerManager(Widget instance) /*-{
    return instance.@com.google.gwt.user.client.ui.Widget::createHandlerManager()();
  }-*/;

  public native static void UIObject_setElement_Element(UIObject instance, Element a0) /*-{
    instance.@com.google.gwt.user.client.ui.UIObject::setElement(Lcom/google/gwt/user/client/Element;)(a0);
  }-*/;

  public native static int ComplexPanel_adjustIndex_Widget_int(ComplexPanel instance, Widget a0, int a1) /*-{
    return instance.@com.google.gwt.user.client.ui.ComplexPanel::adjustIndex(Lcom/google/gwt/user/client/ui/Widget;I)(a0, a1);
  }-*/;

  public native static void Panel_doAttachChildren(Panel instance) /*-{
    instance.@com.google.gwt.user.client.ui.Panel::doAttachChildren()();
  }-*/;

  public native static void Widget_onDetach(Widget instance) /*-{
    instance.@com.google.gwt.user.client.ui.Widget::onDetach()();
  }-*/;

  public native static void ComplexWidget_insert_Widget_Element_int_boolean(ComplexWidget instance, Widget a0, Element a1, int a2, boolean a3) /*-{
    instance.@org.gwtbootstrap3.client.ui.base.ComplexWidget::insert(Lcom/google/gwt/user/client/ui/Widget;Lcom/google/gwt/user/client/Element;IZ)(a0, a1, a2, a3);
  }-*/;

  public native static void Panel_doDetachChildren(Panel instance) /*-{
    instance.@com.google.gwt.user.client.ui.Panel::doDetachChildren()();
  }-*/;

  public native static void Widget_delegateEvent_Widget_GwtEvent(Widget instance, Widget a0, GwtEvent a1) /*-{
    instance.@com.google.gwt.user.client.ui.Widget::delegateEvent(Lcom/google/gwt/user/client/ui/Widget;Lcom/google/gwt/event/shared/GwtEvent;)(a0, a1);
  }-*/;

  public native static int Widget_getHandlerCount_Type(Widget instance, Type a0) /*-{
    return instance.@com.google.gwt.user.client.ui.Widget::getHandlerCount(Lcom/google/gwt/event/shared/GwtEvent$Type;)(a0);
  }-*/;

  public native static com.google.gwt.dom.client.Element UIObject_resolvePotentialElement(UIObject instance) /*-{
    return instance.@com.google.gwt.user.client.ui.UIObject::resolvePotentialElement()();
  }-*/;

  public native static void ComplexPanel_add_Widget_Element(ComplexPanel instance, Widget a0, com.google.gwt.dom.client.Element a1) /*-{
    instance.@com.google.gwt.user.client.ui.ComplexPanel::add(Lcom/google/gwt/user/client/ui/Widget;Lcom/google/gwt/dom/client/Element;)(a0, a1);
  }-*/;

  public native static WidgetCollection ComplexPanel_getChildren(ComplexPanel instance) /*-{
    return instance.@com.google.gwt.user.client.ui.ComplexPanel::getChildren()();
  }-*/;

  public native static Element UIObject_getStyleElement(UIObject instance) /*-{
    return instance.@com.google.gwt.user.client.ui.UIObject::getStyleElement()();
  }-*/;

  public native static void ComplexPanel_add_Widget_Element(ComplexPanel instance, Widget a0, Element a1) /*-{
    instance.@com.google.gwt.user.client.ui.ComplexPanel::add(Lcom/google/gwt/user/client/ui/Widget;Lcom/google/gwt/user/client/Element;)(a0, a1);
  }-*/;

  public native static void Widget_onLoad(Widget instance) /*-{
    instance.@com.google.gwt.user.client.ui.Widget::onLoad()();
  }-*/;

  public native static void ComplexPanel_checkIndexBoundsForAccess_int(ComplexPanel instance, int a0) /*-{
    instance.@com.google.gwt.user.client.ui.ComplexPanel::checkIndexBoundsForAccess(I)(a0);
  }-*/;

  public native static void Widget_onUnload(Widget instance) /*-{
    instance.@com.google.gwt.user.client.ui.Widget::onUnload()();
  }-*/;

  public native static void Widget_onAttach(Widget instance) /*-{
    instance.@com.google.gwt.user.client.ui.Widget::onAttach()();
  }-*/;

  public native static void UIObject_onEnsureDebugId_String(UIObject instance, String a0) /*-{
    instance.@com.google.gwt.user.client.ui.UIObject::onEnsureDebugId(Ljava/lang/String;)(a0);
  }-*/;
}