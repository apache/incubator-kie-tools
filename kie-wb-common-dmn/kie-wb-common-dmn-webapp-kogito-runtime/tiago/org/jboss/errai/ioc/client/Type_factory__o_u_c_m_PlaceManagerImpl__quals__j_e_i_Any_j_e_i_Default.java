package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.HasWidgets;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.api.SharedSingleton;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityLifecycleErrorHandler;
import org.uberfire.client.mvp.ActivityManager;
import org.uberfire.client.mvp.ActivityManagerImpl;
import org.uberfire.client.mvp.PerspectiveManager;
import org.uberfire.client.mvp.PerspectiveManagerImpl;
import org.uberfire.client.mvp.PlaceHistoryHandler;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceManagerImpl;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.workbench.LayoutSelection;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.PanelManagerImpl;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.client.workbench.events.NewSplashScreenActiveEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.mvp.BiParameterizedCommand;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public class Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PlaceManagerImpl> { private class Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PlaceManagerImpl implements Proxy<PlaceManagerImpl> {
    private final ProxyHelper<PlaceManagerImpl> proxyHelper = new ProxyHelperImpl<PlaceManagerImpl>("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PlaceManagerImpl instance) {

    }

    public PlaceManagerImpl asBeanType() {
      return this;
    }

    public void setInstance(final PlaceManagerImpl instance) {
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

    @Override public void initPlaceHistoryHandler() {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.initPlaceHistoryHandler();
      } else {
        super.initPlaceHistoryHandler();
      }
    }

    @Override public void goTo(String identifier, PanelDefinition panel) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.goTo(identifier, panel);
      } else {
        super.goTo(identifier, panel);
      }
    }

    @Override public void goTo(String identifier) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.goTo(identifier);
      } else {
        super.goTo(identifier);
      }
    }

    @Override public void goTo(PlaceRequest place) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.goTo(place);
      } else {
        super.goTo(place);
      }
    }

    @Override public void goTo(Path path, PanelDefinition panel) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.goTo(path, panel);
      } else {
        super.goTo(path, panel);
      }
    }

    @Override public void goTo(Path path) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.goTo(path);
      } else {
        super.goTo(path);
      }
    }

    @Override public void goTo(Path path, PlaceRequest placeRequest, PanelDefinition panel) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.goTo(path, placeRequest, panel);
      } else {
        super.goTo(path, placeRequest, panel);
      }
    }

    @Override public void goTo(Path path, PlaceRequest placeRequest) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.goTo(path, placeRequest);
      } else {
        super.goTo(path, placeRequest);
      }
    }

    @Override public void goTo(PlaceRequest place, PanelDefinition panel) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.goTo(place, panel);
      } else {
        super.goTo(place, panel);
      }
    }

    @Override public void goTo(PlaceRequest place, HasWidgets addTo) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.goTo(place, addTo);
      } else {
        super.goTo(place, addTo);
      }
    }

    @Override public void goTo(String id, HTMLElement addTo) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.goTo(id, addTo);
      } else {
        super.goTo(id, addTo);
      }
    }

    @Override public void goTo(PlaceRequest place, HTMLElement addTo) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.goTo(place, addTo);
      } else {
        super.goTo(place, addTo);
      }
    }

    @Override public void goTo(PlaceRequest place, elemental2.dom.HTMLElement addTo) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.goTo(place, addTo);
      } else {
        super.goTo(place, addTo);
      }
    }

    @Override public void goTo(PartDefinition part, PanelDefinition panel) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.goTo(part, panel);
      } else {
        super.goTo(part, panel);
      }
    }

    @Override public Activity getActivity(PlaceRequest place) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Activity retVal = proxiedInstance.getActivity(place);
        return retVal;
      } else {
        return super.getActivity(place);
      }
    }

    @Override public PlaceStatus getStatus(String id) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final PlaceStatus retVal = proxiedInstance.getStatus(id);
        return retVal;
      } else {
        return super.getStatus(id);
      }
    }

    @Override public PlaceStatus getStatus(PlaceRequest place) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final PlaceStatus retVal = proxiedInstance.getStatus(place);
        return retVal;
      } else {
        return super.getStatus(place);
      }
    }

    @Override public void closePlace(String id) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.closePlace(id);
      } else {
        super.closePlace(id);
      }
    }

    @Override public void closePlace(PlaceRequest placeToClose) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.closePlace(placeToClose);
      } else {
        super.closePlace(placeToClose);
      }
    }

    @Override public void closePlace(PlaceRequest placeToClose, Command doAfterClose) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.closePlace(placeToClose, doAfterClose);
      } else {
        super.closePlace(placeToClose, doAfterClose);
      }
    }

    @Override public void tryClosePlace(PlaceRequest placeToClose, Command onAfterClose) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.tryClosePlace(placeToClose, onAfterClose);
      } else {
        super.tryClosePlace(placeToClose, onAfterClose);
      }
    }

    @Override public void forceClosePlace(String id) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.forceClosePlace(id);
      } else {
        super.forceClosePlace(id);
      }
    }

    @Override public void forceClosePlace(PlaceRequest placeToClose) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.forceClosePlace(placeToClose);
      } else {
        super.forceClosePlace(placeToClose);
      }
    }

    @Override public void closeAllPlaces() {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.closeAllPlaces();
      } else {
        super.closeAllPlaces();
      }
    }

    @Override public void forceCloseAllPlaces() {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.forceCloseAllPlaces();
      } else {
        super.forceCloseAllPlaces();
      }
    }

    @Override public boolean closeAllPlacesOrNothing() {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.closeAllPlacesOrNothing();
        return retVal;
      } else {
        return super.closeAllPlacesOrNothing();
      }
    }

    @Override public List getUncloseablePlaces() {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getUncloseablePlaces();
        return retVal;
      } else {
        return super.getUncloseablePlaces();
      }
    }

    @Override public void registerOnOpenCallback(PlaceRequest place, Command callback) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerOnOpenCallback(place, callback);
      } else {
        super.registerOnOpenCallback(place, callback);
      }
    }

    @Override public void unregisterOnOpenCallbacks(PlaceRequest place) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.unregisterOnOpenCallbacks(place);
      } else {
        super.unregisterOnOpenCallbacks(place);
      }
    }

    @Override public void registerOnCloseCallback(PlaceRequest place, Command callback) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerOnCloseCallback(place, callback);
      } else {
        super.registerOnCloseCallback(place, callback);
      }
    }

    @Override public void unregisterOnCloseCallbacks(PlaceRequest place) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.unregisterOnCloseCallbacks(place);
      } else {
        super.unregisterOnCloseCallbacks(place);
      }
    }

    @Override public void registerPerspectiveCloseChain(String perspectiveIdentifier, BiParameterizedCommand closeChain) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerPerspectiveCloseChain(perspectiveIdentifier, closeChain);
      } else {
        super.registerPerspectiveCloseChain(perspectiveIdentifier, closeChain);
      }
    }

    @Override public Collection getActiveSplashScreens() {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getActiveSplashScreens();
        return retVal;
      } else {
        return super.getActiveSplashScreens();
      }
    }

    @Override public Collection getActivitiesForResourceType(ResourceTypeDefinition type) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getActivitiesForResourceType(type);
        return retVal;
      } else {
        return super.getActivitiesForResourceType(type);
      }
    }

    @Override public Collection getActivePlaceRequests() {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getActivePlaceRequests();
        return retVal;
      } else {
        return super.getActivePlaceRequests();
      }
    }

    @Override public Collection getActivePlaceRequestsWithPath() {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getActivePlaceRequestsWithPath();
        return retVal;
      } else {
        return super.getActivePlaceRequestsWithPath();
      }
    }

    @Override public boolean canClosePlace(PlaceRequest place) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.canClosePlace(place);
        return retVal;
      } else {
        return super.canClosePlace(place);
      }
    }

    @Override public boolean canCloseAllPlaces() {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.canCloseAllPlaces();
        return retVal;
      } else {
        return super.canCloseAllPlaces();
      }
    }

    @Override public List getOnOpenCallbacks(PlaceRequest place) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getOnOpenCallbacks(place);
        return retVal;
      } else {
        return super.getOnOpenCallbacks(place);
      }
    }

    @Override public List getOnCloseCallbacks(PlaceRequest place) {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getOnCloseCallbacks(place);
        return retVal;
      } else {
        return super.getOnCloseCallbacks(place);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PlaceManagerImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PlaceManagerImpl.class, "Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default", SharedSingleton.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PlaceManagerImpl.class, Object.class, PlaceManager.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.client.workbench.events.PlaceGainFocusEvent", new AbstractCDIEventCallback<PlaceGainFocusEvent>() {
      public void fireEvent(final PlaceGainFocusEvent event) {
        final PlaceManagerImpl instance = Factory.maybeUnwrapProxy((PlaceManagerImpl) context.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default"));
        PlaceManagerImpl_onWorkbenchPartOnFocus_PlaceGainFocusEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PlaceGainFocusEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.client.workbench.events.PlaceLostFocusEvent", new AbstractCDIEventCallback<PlaceLostFocusEvent>() {
      public void fireEvent(final PlaceLostFocusEvent event) {
        final PlaceManagerImpl instance = Factory.maybeUnwrapProxy((PlaceManagerImpl) context.getInstance("Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_Default"));
        PlaceManagerImpl_onWorkbenchPartLostFocus_PlaceLostFocusEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PlaceLostFocusEvent []";
      }
    });
  }

  public PlaceManagerImpl createInstance(final ContextManager contextManager) {
    final PlaceManagerImpl instance = new PlaceManagerImpl();
    setIncompleteInstance(instance);
    final Event PlaceManagerImpl_newSplashScreenActiveEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { NewSplashScreenActiveEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, PlaceManagerImpl_newSplashScreenActiveEvent);
    PlaceManagerImpl_Event_newSplashScreenActiveEvent(instance, PlaceManagerImpl_newSplashScreenActiveEvent);
    final PanelManagerImpl PlaceManagerImpl_panelManager = (PanelManagerImpl) contextManager.getInstance("Type_factory__o_u_c_w_PanelManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    PlaceManagerImpl_PanelManager_panelManager(instance, PlaceManagerImpl_panelManager);
    final PlaceHistoryHandler PlaceManagerImpl_placeHistoryHandler = (PlaceHistoryHandler) contextManager.getInstance("Type_factory__o_u_c_m_PlaceHistoryHandler__quals__j_e_i_Any_j_e_i_Default");
    PlaceManagerImpl_PlaceHistoryHandler_placeHistoryHandler(instance, PlaceManagerImpl_placeHistoryHandler);
    final Event PlaceManagerImpl_selectWorkbenchPartEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { SelectPlaceEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, PlaceManagerImpl_selectWorkbenchPartEvent);
    PlaceManagerImpl_Event_selectWorkbenchPartEvent(instance, PlaceManagerImpl_selectWorkbenchPartEvent);
    final Event PlaceManagerImpl_workbenchPartCloseEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { ClosePlaceEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, PlaceManagerImpl_workbenchPartCloseEvent);
    PlaceManagerImpl_Event_workbenchPartCloseEvent(instance, PlaceManagerImpl_workbenchPartCloseEvent);
    final LayoutSelection PlaceManagerImpl_layoutSelection = (LayoutSelection) contextManager.getInstance("Type_factory__o_u_c_w_LayoutSelection__quals__j_e_i_Any_j_e_i_Default");
    PlaceManagerImpl_LayoutSelection_layoutSelection(instance, PlaceManagerImpl_layoutSelection);
    final ActivityLifecycleErrorHandler PlaceManagerImpl_lifecycleErrorHandler = (ActivityLifecycleErrorHandler) contextManager.getInstance("Type_factory__o_u_c_m_ActivityLifecycleErrorHandler__quals__j_e_i_Any_j_e_i_Default");
    PlaceManagerImpl_ActivityLifecycleErrorHandler_lifecycleErrorHandler(instance, PlaceManagerImpl_lifecycleErrorHandler);
    final ActivityManagerImpl PlaceManagerImpl_activityManager = (ActivityManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_ActivityManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    PlaceManagerImpl_ActivityManager_activityManager(instance, PlaceManagerImpl_activityManager);
    final PerspectiveManagerImpl PlaceManagerImpl_perspectiveManager = (PerspectiveManagerImpl) contextManager.getInstance("Type_factory__o_u_c_m_PerspectiveManagerImpl__quals__j_e_i_Any_j_e_i_Default");
    PlaceManagerImpl_PerspectiveManager_perspectiveManager(instance, PlaceManagerImpl_perspectiveManager);
    final Event PlaceManagerImpl_workbenchPartBeforeCloseEvent = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { BeforeClosePlaceEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, PlaceManagerImpl_workbenchPartBeforeCloseEvent);
    PlaceManagerImpl_Event_workbenchPartBeforeCloseEvent(instance, PlaceManagerImpl_workbenchPartBeforeCloseEvent);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final PlaceManagerImpl instance) {
    instance.initPlaceHistoryHandler();
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PlaceManagerImpl> proxyImpl = new Type_factory__o_u_c_m_PlaceManagerImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static PlaceHistoryHandler PlaceManagerImpl_PlaceHistoryHandler_placeHistoryHandler(PlaceManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PlaceManagerImpl::placeHistoryHandler;
  }-*/;

  native static void PlaceManagerImpl_PlaceHistoryHandler_placeHistoryHandler(PlaceManagerImpl instance, PlaceHistoryHandler value) /*-{
    instance.@org.uberfire.client.mvp.PlaceManagerImpl::placeHistoryHandler = value;
  }-*/;

  native static ActivityManager PlaceManagerImpl_ActivityManager_activityManager(PlaceManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PlaceManagerImpl::activityManager;
  }-*/;

  native static void PlaceManagerImpl_ActivityManager_activityManager(PlaceManagerImpl instance, ActivityManager value) /*-{
    instance.@org.uberfire.client.mvp.PlaceManagerImpl::activityManager = value;
  }-*/;

  native static LayoutSelection PlaceManagerImpl_LayoutSelection_layoutSelection(PlaceManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PlaceManagerImpl::layoutSelection;
  }-*/;

  native static void PlaceManagerImpl_LayoutSelection_layoutSelection(PlaceManagerImpl instance, LayoutSelection value) /*-{
    instance.@org.uberfire.client.mvp.PlaceManagerImpl::layoutSelection = value;
  }-*/;

  native static ActivityLifecycleErrorHandler PlaceManagerImpl_ActivityLifecycleErrorHandler_lifecycleErrorHandler(PlaceManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PlaceManagerImpl::lifecycleErrorHandler;
  }-*/;

  native static void PlaceManagerImpl_ActivityLifecycleErrorHandler_lifecycleErrorHandler(PlaceManagerImpl instance, ActivityLifecycleErrorHandler value) /*-{
    instance.@org.uberfire.client.mvp.PlaceManagerImpl::lifecycleErrorHandler = value;
  }-*/;

  native static PerspectiveManager PlaceManagerImpl_PerspectiveManager_perspectiveManager(PlaceManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PlaceManagerImpl::perspectiveManager;
  }-*/;

  native static void PlaceManagerImpl_PerspectiveManager_perspectiveManager(PlaceManagerImpl instance, PerspectiveManager value) /*-{
    instance.@org.uberfire.client.mvp.PlaceManagerImpl::perspectiveManager = value;
  }-*/;

  native static PanelManager PlaceManagerImpl_PanelManager_panelManager(PlaceManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PlaceManagerImpl::panelManager;
  }-*/;

  native static void PlaceManagerImpl_PanelManager_panelManager(PlaceManagerImpl instance, PanelManager value) /*-{
    instance.@org.uberfire.client.mvp.PlaceManagerImpl::panelManager = value;
  }-*/;

  native static Event PlaceManagerImpl_Event_selectWorkbenchPartEvent(PlaceManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PlaceManagerImpl::selectWorkbenchPartEvent;
  }-*/;

  native static void PlaceManagerImpl_Event_selectWorkbenchPartEvent(PlaceManagerImpl instance, Event<SelectPlaceEvent> value) /*-{
    instance.@org.uberfire.client.mvp.PlaceManagerImpl::selectWorkbenchPartEvent = value;
  }-*/;

  native static Event PlaceManagerImpl_Event_workbenchPartCloseEvent(PlaceManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PlaceManagerImpl::workbenchPartCloseEvent;
  }-*/;

  native static void PlaceManagerImpl_Event_workbenchPartCloseEvent(PlaceManagerImpl instance, Event<ClosePlaceEvent> value) /*-{
    instance.@org.uberfire.client.mvp.PlaceManagerImpl::workbenchPartCloseEvent = value;
  }-*/;

  native static Event PlaceManagerImpl_Event_newSplashScreenActiveEvent(PlaceManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PlaceManagerImpl::newSplashScreenActiveEvent;
  }-*/;

  native static void PlaceManagerImpl_Event_newSplashScreenActiveEvent(PlaceManagerImpl instance, Event<NewSplashScreenActiveEvent> value) /*-{
    instance.@org.uberfire.client.mvp.PlaceManagerImpl::newSplashScreenActiveEvent = value;
  }-*/;

  native static Event PlaceManagerImpl_Event_workbenchPartBeforeCloseEvent(PlaceManagerImpl instance) /*-{
    return instance.@org.uberfire.client.mvp.PlaceManagerImpl::workbenchPartBeforeCloseEvent;
  }-*/;

  native static void PlaceManagerImpl_Event_workbenchPartBeforeCloseEvent(PlaceManagerImpl instance, Event<BeforeClosePlaceEvent> value) /*-{
    instance.@org.uberfire.client.mvp.PlaceManagerImpl::workbenchPartBeforeCloseEvent = value;
  }-*/;

  public native static void PlaceManagerImpl_onWorkbenchPartOnFocus_PlaceGainFocusEvent(PlaceManagerImpl instance, PlaceGainFocusEvent a0) /*-{
    instance.@org.uberfire.client.mvp.PlaceManagerImpl::onWorkbenchPartOnFocus(Lorg/uberfire/client/workbench/events/PlaceGainFocusEvent;)(a0);
  }-*/;

  public native static void PlaceManagerImpl_onWorkbenchPartLostFocus_PlaceLostFocusEvent(PlaceManagerImpl instance, PlaceLostFocusEvent a0) /*-{
    instance.@org.uberfire.client.mvp.PlaceManagerImpl::onWorkbenchPartLostFocus(Lorg/uberfire/client/workbench/events/PlaceLostFocusEvent;)(a0);
  }-*/;
}