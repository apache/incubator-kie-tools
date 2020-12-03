package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import org.jboss.errai.common.client.api.IsElement;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.workbench.docks.UberfireDockContainerReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocksContainer;
import org.uberfire.mvp.Command;

public class Type_factory__o_u_c_w_d_UberfireDocksContainer__quals__j_e_i_Any_j_e_i_Default extends Factory<UberfireDocksContainer> { private class Type_factory__o_u_c_w_d_UberfireDocksContainer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends UberfireDocksContainer implements Proxy<UberfireDocksContainer> {
    private final ProxyHelper<UberfireDocksContainer> proxyHelper = new ProxyHelperImpl<UberfireDocksContainer>("Type_factory__o_u_c_w_d_UberfireDocksContainer__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final UberfireDocksContainer instance) {

    }

    public UberfireDocksContainer asBeanType() {
      return this;
    }

    public void setInstance(final UberfireDocksContainer instance) {
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

    @Override public void setup(DockLayoutPanel rootContainer, Command resizeCommand) {
      if (proxyHelper != null) {
        final UberfireDocksContainer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setup(rootContainer, resizeCommand);
      } else {
        super.setup(rootContainer, resizeCommand);
      }
    }

    @Override public void add(UberfireDockPosition position, Widget widget, Double size) {
      if (proxyHelper != null) {
        final UberfireDocksContainer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.add(position, widget, size);
      } else {
        super.add(position, widget, size);
      }
    }

    @Override public void addBreadcrumbs(IsElement isElement, Double size) {
      if (proxyHelper != null) {
        final UberfireDocksContainer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addBreadcrumbs(isElement, size);
      } else {
        super.addBreadcrumbs(isElement, size);
      }
    }

    @Override public void show(Widget widget) {
      if (proxyHelper != null) {
        final UberfireDocksContainer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show(widget);
      } else {
        super.show(widget);
      }
    }

    @Override public void show(IsElement isElement) {
      if (proxyHelper != null) {
        final UberfireDocksContainer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.show(isElement);
      } else {
        super.show(isElement);
      }
    }

    @Override public void hide(Widget widget) {
      if (proxyHelper != null) {
        final UberfireDocksContainer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hide(widget);
      } else {
        super.hide(widget);
      }
    }

    @Override public void hide(IsElement isElement) {
      if (proxyHelper != null) {
        final UberfireDocksContainer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.hide(isElement);
      } else {
        super.hide(isElement);
      }
    }

    @Override public void setWidgetSize(Widget widget, double size) {
      if (proxyHelper != null) {
        final UberfireDocksContainer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setWidgetSize(widget, size);
      } else {
        super.setWidgetSize(widget, size);
      }
    }

    @Override public void resize() {
      if (proxyHelper != null) {
        final UberfireDocksContainer proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.resize();
      } else {
        super.resize();
      }
    }

    @Override public int getOffsetHeight() {
      if (proxyHelper != null) {
        final UberfireDocksContainer proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getOffsetHeight();
        return retVal;
      } else {
        return super.getOffsetHeight();
      }
    }

    @Override public int getOffsetWidth() {
      if (proxyHelper != null) {
        final UberfireDocksContainer proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getOffsetWidth();
        return retVal;
      } else {
        return super.getOffsetWidth();
      }
    }

    @Override public int getClientWidth() {
      if (proxyHelper != null) {
        final UberfireDocksContainer proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getClientWidth();
        return retVal;
      } else {
        return super.getClientWidth();
      }
    }

    @Override public boolean isReady() {
      if (proxyHelper != null) {
        final UberfireDocksContainer proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isReady();
        return retVal;
      } else {
        return super.isReady();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final UberfireDocksContainer proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_d_UberfireDocksContainer__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(UberfireDocksContainer.class, "Type_factory__o_u_c_w_d_UberfireDocksContainer__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { UberfireDocksContainer.class, Object.class });
  }

  public UberfireDocksContainer createInstance(final ContextManager contextManager) {
    final UberfireDocksContainer instance = new UberfireDocksContainer();
    setIncompleteInstance(instance);
    final Event UberfireDocksContainer_event = (Event) contextManager.getContextualInstance("ContextualProvider_factory__j_e_e_Event__quals__Universal", new Class[] { UberfireDockContainerReadyEvent.class }, new Annotation[] { });
    registerDependentScopedReference(instance, UberfireDocksContainer_event);
    UberfireDocksContainer_Event_event(instance, UberfireDocksContainer_event);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<UberfireDocksContainer> proxyImpl = new Type_factory__o_u_c_w_d_UberfireDocksContainer__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  native static Event UberfireDocksContainer_Event_event(UberfireDocksContainer instance) /*-{
    return instance.@org.uberfire.client.workbench.docks.UberfireDocksContainer::event;
  }-*/;

  native static void UberfireDocksContainer_Event_event(UberfireDocksContainer instance, Event<UberfireDockContainerReadyEvent> value) /*-{
    instance.@org.uberfire.client.workbench.docks.UberfireDocksContainer::event = value;
  }-*/;
}