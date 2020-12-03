package org.jboss.errai.ioc.client;

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceHistoryHandler;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceRequestHistoryMapper;
import org.uberfire.client.mvp.PlaceRequestHistoryMapperImpl;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.mvp.PlaceRequest;

public class Type_factory__o_u_c_m_PlaceHistoryHandler__quals__j_e_i_Any_j_e_i_Default extends Factory<PlaceHistoryHandler> { private class Type_factory__o_u_c_m_PlaceHistoryHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PlaceHistoryHandler implements Proxy<PlaceHistoryHandler> {
    private final ProxyHelper<PlaceHistoryHandler> proxyHelper = new ProxyHelperImpl<PlaceHistoryHandler>("Type_factory__o_u_c_m_PlaceHistoryHandler__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PlaceHistoryHandler instance) {

    }

    public PlaceHistoryHandler asBeanType() {
      return this;
    }

    public void setInstance(final PlaceHistoryHandler instance) {
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

    @Override public void handleCurrentHistory() {
      if (proxyHelper != null) {
        final PlaceHistoryHandler proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.handleCurrentHistory();
      } else {
        super.handleCurrentHistory();
      }
    }

    @Override public HandlerRegistration initialize(PlaceManager placeManager, EventBus eventBus, PlaceRequest defaultPlaceRequest) {
      if (proxyHelper != null) {
        final PlaceHistoryHandler proxiedInstance = proxyHelper.getInstance(this);
        final HandlerRegistration retVal = proxiedInstance.initialize(placeManager, eventBus, defaultPlaceRequest);
        return retVal;
      } else {
        return super.initialize(placeManager, eventBus, defaultPlaceRequest);
      }
    }

    @Override public String getCurrentBookmarkableURLStatus() {
      if (proxyHelper != null) {
        final PlaceHistoryHandler proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getCurrentBookmarkableURLStatus();
        return retVal;
      } else {
        return super.getCurrentBookmarkableURLStatus();
      }
    }

    @Override public PlaceRequest getPerspectiveFromPlace(PlaceRequest place) {
      if (proxyHelper != null) {
        final PlaceHistoryHandler proxiedInstance = proxyHelper.getInstance(this);
        final PlaceRequest retVal = proxiedInstance.getPerspectiveFromPlace(place);
        return retVal;
      } else {
        return super.getPerspectiveFromPlace(place);
      }
    }

    @Override public void registerOpen(Activity activity, PlaceRequest place) {
      if (proxyHelper != null) {
        final PlaceHistoryHandler proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerOpen(activity, place);
      } else {
        super.registerOpen(activity, place);
      }
    }

    @Override public void registerClose(Activity activity, PlaceRequest place) {
      if (proxyHelper != null) {
        final PlaceHistoryHandler proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerClose(activity, place);
      } else {
        super.registerClose(activity, place);
      }
    }

    @Override public void flush() {
      if (proxyHelper != null) {
        final PlaceHistoryHandler proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.flush();
      } else {
        super.flush();
      }
    }

    @Override public String getToken() {
      if (proxyHelper != null) {
        final PlaceHistoryHandler proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getToken();
        return retVal;
      } else {
        return super.getToken();
      }
    }

    @Override public void registerOpenDock(UberfireDocksInteractionEvent event) {
      if (proxyHelper != null) {
        final PlaceHistoryHandler proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerOpenDock(event);
      } else {
        super.registerOpenDock(event);
      }
    }

    @Override public void registerCloseDock(UberfireDocksInteractionEvent event) {
      if (proxyHelper != null) {
        final PlaceHistoryHandler proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerCloseDock(event);
      } else {
        super.registerCloseDock(event);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PlaceHistoryHandler proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_m_PlaceHistoryHandler__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PlaceHistoryHandler.class, "Type_factory__o_u_c_m_PlaceHistoryHandler__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PlaceHistoryHandler.class, Object.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent", new AbstractCDIEventCallback<UberfireDocksInteractionEvent>() {
      public void fireEvent(final UberfireDocksInteractionEvent event) {
        final PlaceHistoryHandler instance = Factory.maybeUnwrapProxy((PlaceHistoryHandler) context.getInstance("Type_factory__o_u_c_m_PlaceHistoryHandler__quals__j_e_i_Any_j_e_i_Default"));
        instance.registerOpenDock(event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent", new AbstractCDIEventCallback<UberfireDocksInteractionEvent>() {
      public void fireEvent(final UberfireDocksInteractionEvent event) {
        final PlaceHistoryHandler instance = Factory.maybeUnwrapProxy((PlaceHistoryHandler) context.getInstance("Type_factory__o_u_c_m_PlaceHistoryHandler__quals__j_e_i_Any_j_e_i_Default"));
        instance.registerCloseDock(event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent []";
      }
    });
  }

  public PlaceHistoryHandler createInstance(final ContextManager contextManager) {
    final PlaceHistoryHandler instance = new PlaceHistoryHandler();
    setIncompleteInstance(instance);
    final PlaceRequestHistoryMapperImpl PlaceHistoryHandler_mapper = (PlaceRequestHistoryMapperImpl) contextManager.getInstance("Type_factory__o_u_c_m_PlaceRequestHistoryMapperImpl__quals__j_e_i_Any_j_e_i_Default");
    registerDependentScopedReference(instance, PlaceHistoryHandler_mapper);
    PlaceHistoryHandler_PlaceRequestHistoryMapper_mapper(instance, PlaceHistoryHandler_mapper);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PlaceHistoryHandler> proxyImpl = new Type_factory__o_u_c_m_PlaceHistoryHandler__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static PlaceRequestHistoryMapper PlaceHistoryHandler_PlaceRequestHistoryMapper_mapper(PlaceHistoryHandler instance) /*-{
    return instance.@org.uberfire.client.mvp.PlaceHistoryHandler::mapper;
  }-*/;

  native static void PlaceHistoryHandler_PlaceRequestHistoryMapper_mapper(PlaceHistoryHandler instance, PlaceRequestHistoryMapper value) /*-{
    instance.@org.uberfire.client.mvp.PlaceHistoryHandler::mapper = value;
  }-*/;
}