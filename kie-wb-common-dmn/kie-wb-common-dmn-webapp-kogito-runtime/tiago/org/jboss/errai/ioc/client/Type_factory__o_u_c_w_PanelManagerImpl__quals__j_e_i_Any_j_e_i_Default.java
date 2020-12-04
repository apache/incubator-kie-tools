package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.HasWidgets;
import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UIPart;
import org.uberfire.client.workbench.BeanFactory;
import org.uberfire.client.workbench.DefaultBeanFactory;
import org.uberfire.client.workbench.LayoutSelection;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.PanelManagerImpl;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.events.DropPlaceEvent;
import org.uberfire.client.workbench.events.PanelFocusEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceHiddenEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.PlaceMaximizedEvent;
import org.uberfire.client.workbench.events.PlaceMinimizedEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.CustomPanelDefinition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.Menus;

public class Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PanelManagerImpl> { private class Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PanelManagerImpl implements Proxy<PanelManagerImpl> {
    private final ProxyHelper<PanelManagerImpl> proxyHelper = new ProxyHelperImpl<PanelManagerImpl>("Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null, null, null, null, null, null, null, null, null, null, null);
    }

    public void initProxyProperties(final PanelManagerImpl instance) {

    }

    public PanelManagerImpl asBeanType() {
      return this;
    }

    public void setInstance(final PanelManagerImpl instance) {
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

    @Override protected BeanFactory getBeanFactory() {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final BeanFactory retVal = PanelManagerImpl_getBeanFactory(proxiedInstance);
        return retVal;
      } else {
        return super.getBeanFactory();
      }
    }

    @Override public PanelDefinition getRoot() {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final PanelDefinition retVal = proxiedInstance.getRoot();
        return retVal;
      } else {
        return super.getRoot();
      }
    }

    @Override public void setRoot(PerspectiveActivity activity, PanelDefinition root) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setRoot(activity, root);
      } else {
        super.setRoot(activity, root);
      }
    }

    @Override public void addWorkbenchPart(PlaceRequest place, PartDefinition partDef, PanelDefinition panelDef, Menus menus, UIPart uiPart, String contextId, Integer preferredWidth, Integer preferredHeight) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addWorkbenchPart(place, partDef, panelDef, menus, uiPart, contextId, preferredWidth, preferredHeight);
      } else {
        super.addWorkbenchPart(place, partDef, panelDef, menus, uiPart, contextId, preferredWidth, preferredHeight);
      }
    }

    @Override public boolean removePartForPlace(PlaceRequest toRemove) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.removePartForPlace(toRemove);
        return retVal;
      } else {
        return super.removePartForPlace(toRemove);
      }
    }

    @Override public PanelDefinition addWorkbenchPanel(PanelDefinition targetPanel, Position position, Integer height, Integer width, Integer minHeight, Integer minWidth) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final PanelDefinition retVal = proxiedInstance.addWorkbenchPanel(targetPanel, position, height, width, minHeight, minWidth);
        return retVal;
      } else {
        return super.addWorkbenchPanel(targetPanel, position, height, width, minHeight, minWidth);
      }
    }

    @Override public PanelDefinition addWorkbenchPanel(PanelDefinition targetPanel, PanelDefinition childPanel, Position position) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final PanelDefinition retVal = proxiedInstance.addWorkbenchPanel(targetPanel, childPanel, position);
        return retVal;
      } else {
        return super.addWorkbenchPanel(targetPanel, childPanel, position);
      }
    }

    @Override public void removeWorkbenchPanel(PanelDefinition toRemove) throws IllegalStateException {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.removeWorkbenchPanel(toRemove);
      } else {
        super.removeWorkbenchPanel(toRemove);
      }
    }

    @Override public void onPartFocus(PartDefinition part) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onPartFocus(part);
      } else {
        super.onPartFocus(part);
      }
    }

    @Override public void onPartMaximized(PartDefinition part) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onPartMaximized(part);
      } else {
        super.onPartMaximized(part);
      }
    }

    @Override public void onPartMinimized(PartDefinition part) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onPartMinimized(part);
      } else {
        super.onPartMinimized(part);
      }
    }

    @Override public PartDefinition getFocusedPart() {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final PartDefinition retVal = proxiedInstance.getFocusedPart();
        return retVal;
      } else {
        return super.getFocusedPart();
      }
    }

    @Override public void onPartHidden(PartDefinition part) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onPartHidden(part);
      } else {
        super.onPartHidden(part);
      }
    }

    @Override public void onPartLostFocus() {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onPartLostFocus();
      } else {
        super.onPartLostFocus();
      }
    }

    @Override public void onPanelFocus(PanelDefinition panel) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onPanelFocus(panel);
      } else {
        super.onPanelFocus(panel);
      }
    }

    @Override public void closePart(PartDefinition part) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.closePart(part);
      } else {
        super.closePart(part);
      }
    }

    @Override public PanelDefinition getPanelForPlace(PlaceRequest place) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final PanelDefinition retVal = proxiedInstance.getPanelForPlace(place);
        return retVal;
      } else {
        return super.getPanelForPlace(place);
      }
    }

    @Override protected PartDefinition getPartForPlace(PlaceRequest place) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final PartDefinition retVal = PanelManagerImpl_getPartForPlace_PlaceRequest(proxiedInstance, place);
        return retVal;
      } else {
        return super.getPartForPlace(place);
      }
    }

    @Override protected void removePart(PartDefinition part) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        PanelManagerImpl_removePart_PartDefinition(proxiedInstance, part);
      } else {
        super.removePart(part);
      }
    }

    @Override public CustomPanelDefinition addCustomPanel(HasWidgets container, String panelType) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final CustomPanelDefinition retVal = proxiedInstance.addCustomPanel(container, panelType);
        return retVal;
      } else {
        return super.addCustomPanel(container, panelType);
      }
    }

    @Override public CustomPanelDefinition addCustomPanel(HTMLElement container, String panelType) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final CustomPanelDefinition retVal = proxiedInstance.addCustomPanel(container, panelType);
        return retVal;
      } else {
        return super.addCustomPanel(container, panelType);
      }
    }

    @Override public CustomPanelDefinition addCustomPanel(elemental2.dom.HTMLElement container, String panelType) {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final CustomPanelDefinition retVal = proxiedInstance.addCustomPanel(container, panelType);
        return retVal;
      } else {
        return super.addCustomPanel(container, panelType);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PanelManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PanelManagerImpl.class, "Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PanelManagerImpl.class, Object.class, PanelManager.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.client.workbench.events.SelectPlaceEvent", new AbstractCDIEventCallback<SelectPlaceEvent>() {
      public void fireEvent(final SelectPlaceEvent event) {
        final PanelManagerImpl instance = Factory.maybeUnwrapProxy((PanelManagerImpl) context.getInstance("Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default"));
        PanelManagerImpl_onSelectPlaceEvent_SelectPlaceEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.SelectPlaceEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.client.workbench.events.DropPlaceEvent", new AbstractCDIEventCallback<DropPlaceEvent>() {
      public void fireEvent(final DropPlaceEvent event) {
        final PanelManagerImpl instance = Factory.maybeUnwrapProxy((PanelManagerImpl) context.getInstance("Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default"));
        PanelManagerImpl_onDropPlaceEvent_DropPlaceEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.DropPlaceEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.client.workbench.events.ChangeTitleWidgetEvent", new AbstractCDIEventCallback<ChangeTitleWidgetEvent>() {
      public void fireEvent(final ChangeTitleWidgetEvent event) {
        final PanelManagerImpl instance = Factory.maybeUnwrapProxy((PanelManagerImpl) context.getInstance("Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default"));
        PanelManagerImpl_onChangeTitleWidgetEvent_ChangeTitleWidgetEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.ChangeTitleWidgetEvent []";
      }
    });
  }

  public PanelManagerImpl createInstance(final ContextManager contextManager) {
    final Event<PanelFocusEvent> _panelFocusEvent_2 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PanelFocusEvent.class }, new Annotation[] { });
    final Event<PlaceMinimizedEvent> _placeMinimizedEventEvent_5 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PlaceMinimizedEvent.class }, new Annotation[] { });
    final Event<PlaceLostFocusEvent> _placeLostFocusEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PlaceLostFocusEvent.class }, new Annotation[] { });
    final BeanFactory _beanFactory_10 = (DefaultBeanFactory) contextManager.getInstance("Type_factory__o_u_c_w_DefaultBeanFactory__quals__j_e_i_Any_j_e_i_Default");
    final Event<SelectPlaceEvent> _selectPlaceEvent_3 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { SelectPlaceEvent.class }, new Annotation[] { });
    final Event<PlaceMaximizedEvent> _placeMaximizedEvent_4 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PlaceMaximizedEvent.class }, new Annotation[] { });
    final SyncBeanManager _iocManager_7 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final LayoutSelection _layoutSelection_9 = (LayoutSelection) contextManager.getInstance("Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_Default");
    final Elemental2DomUtil _elemental2DomUtil_11 = (Elemental2DomUtil) contextManager.getInstance("Type_factory__o_j_e_c_c_d_e_Elemental2DomUtil__quals__j_e_i_Any_j_e_i_Default");
    final Event<PlaceGainFocusEvent> _placeGainFocusEvent_0 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PlaceGainFocusEvent.class }, new Annotation[] { });
    final Instance<PlaceManager> _placeManager_8 = (Instance) contextManager.getContextualInstance("ContextualProvider_factory__j_e_i_Instance__quals__Universal", new Class[] { PlaceManager.class }, new Annotation[] { });
    final Event<PlaceHiddenEvent> _placeHiddenEvent_6 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { PlaceHiddenEvent.class }, new Annotation[] { });
    final PanelManagerImpl instance = new PanelManagerImpl(_placeGainFocusEvent_0, _placeLostFocusEvent_1, _panelFocusEvent_2, _selectPlaceEvent_3, _placeMaximizedEvent_4, _placeMinimizedEventEvent_5, _placeHiddenEvent_6, _iocManager_7, _placeManager_8, _layoutSelection_9, _beanFactory_10, _elemental2DomUtil_11);
    registerDependentScopedReference(instance, _panelFocusEvent_2);
    registerDependentScopedReference(instance, _placeMinimizedEventEvent_5);
    registerDependentScopedReference(instance, _placeLostFocusEvent_1);
    registerDependentScopedReference(instance, _selectPlaceEvent_3);
    registerDependentScopedReference(instance, _placeMaximizedEvent_4);
    registerDependentScopedReference(instance, _iocManager_7);
    registerDependentScopedReference(instance, _elemental2DomUtil_11);
    registerDependentScopedReference(instance, _placeGainFocusEvent_0);
    registerDependentScopedReference(instance, _placeManager_8);
    registerDependentScopedReference(instance, _placeHiddenEvent_6);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void generatedDestroyInstance(final Object instance, final ContextManager contextManager) {
    destroyInstanceHelper((PanelManagerImpl) instance, contextManager);
  }

  public void destroyInstanceHelper(final PanelManagerImpl instance, final ContextManager contextManager) {
    PanelManagerImpl_teardown(instance);
  }

  public void invokePostConstructs(final PanelManagerImpl instance) {
    PanelManagerImpl_setup(instance);
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.client.workbench.PanelManagerImpl an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.client.workbench.PanelManagerImpl ([javax.enterprise.event.Event, javax.enterprise.event.Event, javax.enterprise.event.Event, javax.enterprise.event.Event, javax.enterprise.event.Event, javax.enterprise.event.Event, javax.enterprise.event.Event, org.jboss.errai.ioc.client.container.SyncBeanManager, javax.enterprise.inject.Instance, org.uberfire.client.workbench.LayoutSelection, org.uberfire.client.workbench.BeanFactory, org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PanelManagerImpl> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static BeanFactory PanelManagerImpl_getBeanFactory(PanelManagerImpl instance) /*-{
    return instance.@org.uberfire.client.workbench.PanelManagerImpl::getBeanFactory()();
  }-*/;

  public native static PartDefinition PanelManagerImpl_getPartForPlace_PlaceRequest(PanelManagerImpl instance, PlaceRequest a0) /*-{
    return instance.@org.uberfire.client.workbench.PanelManagerImpl::getPartForPlace(Lorg/uberfire/mvp/PlaceRequest;)(a0);
  }-*/;

  public native static void PanelManagerImpl_onChangeTitleWidgetEvent_ChangeTitleWidgetEvent(PanelManagerImpl instance, ChangeTitleWidgetEvent a0) /*-{
    instance.@org.uberfire.client.workbench.PanelManagerImpl::onChangeTitleWidgetEvent(Lorg/uberfire/client/workbench/events/ChangeTitleWidgetEvent;)(a0);
  }-*/;

  public native static void PanelManagerImpl_teardown(PanelManagerImpl instance) /*-{
    instance.@org.uberfire.client.workbench.PanelManagerImpl::teardown()();
  }-*/;

  public native static void PanelManagerImpl_onSelectPlaceEvent_SelectPlaceEvent(PanelManagerImpl instance, SelectPlaceEvent a0) /*-{
    instance.@org.uberfire.client.workbench.PanelManagerImpl::onSelectPlaceEvent(Lorg/uberfire/client/workbench/events/SelectPlaceEvent;)(a0);
  }-*/;

  public native static void PanelManagerImpl_setup(PanelManagerImpl instance) /*-{
    instance.@org.uberfire.client.workbench.PanelManagerImpl::setup()();
  }-*/;

  public native static void PanelManagerImpl_onDropPlaceEvent_DropPlaceEvent(PanelManagerImpl instance, DropPlaceEvent a0) /*-{
    instance.@org.uberfire.client.workbench.PanelManagerImpl::onDropPlaceEvent(Lorg/uberfire/client/workbench/events/DropPlaceEvent;)(a0);
  }-*/;

  public native static void PanelManagerImpl_removePart_PartDefinition(PanelManagerImpl instance, PartDefinition a0) /*-{
    instance.@org.uberfire.client.workbench.PanelManagerImpl::removePart(Lorg/uberfire/workbench/model/PartDefinition;)(a0);
  }-*/;
}