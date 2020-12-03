package org.jboss.errai.ioc.client;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.Widget;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.views.pfly.dnd.CompassWidgetImpl;
import org.uberfire.client.workbench.widgets.dnd.CompassWidget;
import org.uberfire.workbench.model.Position;

public class Type_factory__o_u_c_v_p_d_CompassWidgetImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<CompassWidgetImpl> { private class Type_factory__o_u_c_v_p_d_CompassWidgetImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends CompassWidgetImpl implements Proxy<CompassWidgetImpl> {
    private final ProxyHelper<CompassWidgetImpl> proxyHelper = new ProxyHelperImpl<CompassWidgetImpl>("Type_factory__o_u_c_v_p_d_CompassWidgetImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final CompassWidgetImpl instance) {

    }

    public CompassWidgetImpl asBeanType() {
      return this;
    }

    public void setInstance(final CompassWidgetImpl instance) {
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

    @Override public void onEnter(DragContext context) {
      if (proxyHelper != null) {
        final CompassWidgetImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onEnter(context);
      } else {
        super.onEnter(context);
      }
    }

    @Override public void onLeave(DragContext context) {
      if (proxyHelper != null) {
        final CompassWidgetImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onLeave(context);
      } else {
        super.onLeave(context);
      }
    }

    @Override public void onMove(DragContext context) {
      if (proxyHelper != null) {
        final CompassWidgetImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onMove(context);
      } else {
        super.onMove(context);
      }
    }

    @Override public Position getDropPosition() {
      if (proxyHelper != null) {
        final CompassWidgetImpl proxiedInstance = proxyHelper.getInstance(this);
        final Position retVal = proxiedInstance.getDropPosition();
        return retVal;
      } else {
        return super.getDropPosition();
      }
    }

    @Override public Widget getDropTarget() {
      if (proxyHelper != null) {
        final CompassWidgetImpl proxiedInstance = proxyHelper.getInstance(this);
        final Widget retVal = proxiedInstance.getDropTarget();
        return retVal;
      } else {
        return super.getDropTarget();
      }
    }

    @Override public void onDrop(DragContext context) {
      if (proxyHelper != null) {
        final CompassWidgetImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onDrop(context);
      } else {
        super.onDrop(context);
      }
    }

    @Override public void onPreviewDrop(DragContext context) throws VetoDragException {
      if (proxyHelper != null) {
        final CompassWidgetImpl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.onPreviewDrop(context);
      } else {
        super.onPreviewDrop(context);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final CompassWidgetImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_v_p_d_CompassWidgetImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(CompassWidgetImpl.class, "Type_factory__o_u_c_v_p_d_CompassWidgetImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { CompassWidgetImpl.class, Object.class, CompassWidget.class, DropController.class });
  }

  public CompassWidgetImpl createInstance(final ContextManager contextManager) {
    final CompassWidgetImpl instance = new CompassWidgetImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public void invokePostConstructs(final CompassWidgetImpl instance) {
    CompassWidgetImpl_init(instance);
  }

  public Proxy createProxy(final Context context) {
    final Proxy<CompassWidgetImpl> proxyImpl = new Type_factory__o_u_c_v_p_d_CompassWidgetImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void CompassWidgetImpl_init(CompassWidgetImpl instance) /*-{
    instance.@org.uberfire.client.views.pfly.dnd.CompassWidgetImpl::init()();
  }-*/;
}