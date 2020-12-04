package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.enterprise.client.cdi.AbstractCDIEventCallback;
import org.jboss.errai.enterprise.client.cdi.api.CDI;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.docks.UberfireDocksImpl;
import org.uberfire.client.docks.view.DocksBars;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockContainerReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDockReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.events.PerspectiveChange;

public class Type_factory__o_u_c_d_UberfireDocksImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<UberfireDocksImpl> { private class Type_factory__o_u_c_d_UberfireDocksImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends UberfireDocksImpl implements Proxy<UberfireDocksImpl> {
    private final ProxyHelper<UberfireDocksImpl> proxyHelper = new ProxyHelperImpl<UberfireDocksImpl>("Type_factory__o_u_c_d_UberfireDocksImpl__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_c_d_UberfireDocksImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null, null);
    }

    public void initProxyProperties(final UberfireDocksImpl instance) {

    }

    public UberfireDocksImpl asBeanType() {
      return this;
    }

    public void setInstance(final UberfireDocksImpl instance) {
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

    @Override protected void setup(UberfireDockContainerReadyEvent event) {
      if (proxyHelper != null) {
        final UberfireDocksImpl proxiedInstance = proxyHelper.getInstance(this);
        UberfireDocksImpl_setup_UberfireDockContainerReadyEvent(proxiedInstance, event);
      } else {
        super.setup(event);
      }
    }

    @Override public void add(UberfireDock[] docks) {
      if (proxyHelper != null) {
        final UberfireDocksImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.add(docks);
      } else {
        super.add(docks);
      }
    }

    @Override public void perspectiveChangeEvent(PerspectiveChange perspectiveChange) {
      if (proxyHelper != null) {
        final UberfireDocksImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.perspectiveChangeEvent(perspectiveChange);
      } else {
        super.perspectiveChangeEvent(perspectiveChange);
      }
    }

    @Override public void remove(UberfireDock[] docks) {
      if (proxyHelper != null) {
        final UberfireDocksImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.remove(docks);
      } else {
        super.remove(docks);
      }
    }

    @Override public void open(UberfireDock dock) {
      if (proxyHelper != null) {
        final UberfireDocksImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.open(dock);
      } else {
        super.open(dock);
      }
    }

    @Override public void close(UberfireDock dock) {
      if (proxyHelper != null) {
        final UberfireDocksImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.close(dock);
      } else {
        super.close(dock);
      }
    }

    @Override public void toggle(UberfireDock dock) {
      if (proxyHelper != null) {
        final UberfireDocksImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.toggle(dock);
      } else {
        super.toggle(dock);
      }
    }

    @Override public void hide(UberfireDockPosition position, String perspectiveName) {
      if (proxyHelper != null) {
        final UberfireDocksImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hide(position, perspectiveName);
      } else {
        super.hide(position, perspectiveName);
      }
    }

    @Override public void show(UberfireDockPosition position, String perspectiveName) {
      if (proxyHelper != null) {
        final UberfireDocksImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show(position, perspectiveName);
      } else {
        super.show(position, perspectiveName);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final UberfireDocksImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_d_UberfireDocksImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UberfireDocksImpl.class, "Type_factory__o_u_c_d_UberfireDocksImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { UberfireDocksImpl.class, Object.class, UberfireDocks.class });
  }

  public void init(final Context context) {
    CDI.subscribeLocal("org.uberfire.client.workbench.docks.UberfireDockContainerReadyEvent", new AbstractCDIEventCallback<UberfireDockContainerReadyEvent>() {
      public void fireEvent(final UberfireDockContainerReadyEvent event) {
        final UberfireDocksImpl instance = Factory.maybeUnwrapProxy((UberfireDocksImpl) context.getInstance("Type_factory__o_u_c_d_UberfireDocksImpl__quals__j_e_i_Any_j_e_i_Default"));
        UberfireDocksImpl_setup_UberfireDockContainerReadyEvent(instance, event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.docks.UberfireDockContainerReadyEvent []";
      }
    });
    CDI.subscribeLocal("org.uberfire.client.workbench.events.PerspectiveChange", new AbstractCDIEventCallback<PerspectiveChange>() {
      public void fireEvent(final PerspectiveChange event) {
        final UberfireDocksImpl instance = Factory.maybeUnwrapProxy((UberfireDocksImpl) context.getInstance("Type_factory__o_u_c_d_UberfireDocksImpl__quals__j_e_i_Any_j_e_i_Default"));
        instance.perspectiveChangeEvent(event);
      }
      public String toString() {
        return "Observer: org.uberfire.client.workbench.events.PerspectiveChange []";
      }
    });
  }

  public UberfireDocksImpl createInstance(final ContextManager contextManager) {
    final DocksBars _docksBars_0 = (DocksBars) contextManager.getInstance("Type_factory__o_u_c_d_v_DocksBars__quals__j_e_i_Any_j_e_i_Default");
    final Event<UberfireDockReadyEvent> _dockReadyEvent_1 = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { UberfireDockReadyEvent.class }, new Annotation[] { });
    final UberfireDocksImpl instance = new UberfireDocksImpl(_docksBars_0, _dockReadyEvent_1);
    registerDependentScopedReference(instance, _docksBars_0);
    registerDependentScopedReference(instance, _dockReadyEvent_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_c_d_UberfireDocksImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.client.docks.UberfireDocksImpl an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.client.docks.UberfireDocksImpl ([org.uberfire.client.docks.view.DocksBars, javax.enterprise.event.Event])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<UberfireDocksImpl> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void UberfireDocksImpl_setup_UberfireDockContainerReadyEvent(UberfireDocksImpl instance, UberfireDockContainerReadyEvent a0) /*-{
    instance.@org.uberfire.client.docks.UberfireDocksImpl::setup(Lorg/uberfire/client/workbench/docks/UberfireDockContainerReadyEvent;)(a0);
  }-*/;
}