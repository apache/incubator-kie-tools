package org.jboss.errai.ioc.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.AbstractCanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.CanvasControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.EdgeClipboard;
import org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.LocalClipboardControl;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;

public class Type_factory__o_k_w_c_s_c_c_c_c_c_LocalClipboardControl__quals__j_e_i_Any_j_e_i_Default extends Factory<LocalClipboardControl> { private class Type_factory__o_k_w_c_s_c_c_c_c_c_LocalClipboardControl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LocalClipboardControl implements Proxy<LocalClipboardControl> {
    private final ProxyHelper<LocalClipboardControl> proxyHelper = new ProxyHelperImpl<LocalClipboardControl>("Type_factory__o_k_w_c_s_c_c_c_c_c_LocalClipboardControl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final LocalClipboardControl instance) {

    }

    public LocalClipboardControl asBeanType() {
      return this;
    }

    public void setInstance(final LocalClipboardControl instance) {
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

    @Override public ClipboardControl set(Element[] element) {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        final ClipboardControl retVal = proxiedInstance.set(element);
        return retVal;
      } else {
        return super.set(element);
      }
    }

    @Override public ClipboardControl remove(Element[] element) {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        final ClipboardControl retVal = proxiedInstance.remove(element);
        return retVal;
      } else {
        return super.remove(element);
      }
    }

    @Override public Collection getElements() {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getElements();
        return retVal;
      } else {
        return super.getElements();
      }
    }

    @Override public ClipboardControl clear() {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        final ClipboardControl retVal = proxiedInstance.clear();
        return retVal;
      } else {
        return super.clear();
      }
    }

    @Override public boolean hasElements() {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.hasElements();
        return retVal;
      } else {
        return super.hasElements();
      }
    }

    @Override public String getParent(String uuid) {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getParent(uuid);
        return retVal;
      } else {
        return super.getParent(uuid);
      }
    }

    @Override public List getRollbackCommands() {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getRollbackCommands();
        return retVal;
      } else {
        return super.getRollbackCommands();
      }
    }

    @Override public ClipboardControl setRollbackCommand(Command[] command) {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        final ClipboardControl retVal = proxiedInstance.setRollbackCommand(command);
        return retVal;
      } else {
        return super.setRollbackCommand(command);
      }
    }

    @Override protected void doInit() {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        LocalClipboardControl_doInit(proxiedInstance);
      } else {
        super.doInit();
      }
    }

    @Override protected void doDestroy() {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        LocalClipboardControl_doDestroy(proxiedInstance);
      } else {
        super.doDestroy();
      }
    }

    @Override public Map getEdgeMap() {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        final Map retVal = proxiedInstance.getEdgeMap();
        return retVal;
      } else {
        return super.getEdgeMap();
      }
    }

    @Override public EdgeClipboard buildNewEdgeClipboard(String source, Connection sourceConnection, String target, Connection targetConnection) {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        final EdgeClipboard retVal = proxiedInstance.buildNewEdgeClipboard(source, sourceConnection, target, targetConnection);
        return retVal;
      } else {
        return super.buildNewEdgeClipboard(source, sourceConnection, target, targetConnection);
      }
    }

    @Override public void init(AbstractCanvas context) {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.init(context);
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public void destroy() {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.destroy();
      } else {
        throw new RuntimeException("Cannot invoke public method on proxied interface before constructor completes.");
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LocalClipboardControl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_c_c_c_c_LocalClipboardControl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LocalClipboardControl.class, "Type_factory__o_k_w_c_s_c_c_c_c_c_LocalClipboardControl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LocalClipboardControl.class, AbstractCanvasControl.class, Object.class, CanvasControl.class, ClipboardControl.class });
  }

  public LocalClipboardControl createInstance(final ContextManager contextManager) {
    final LocalClipboardControl instance = new LocalClipboardControl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LocalClipboardControl> proxyImpl = new Type_factory__o_k_w_c_s_c_c_c_c_c_LocalClipboardControl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }

  public native static void LocalClipboardControl_doInit(LocalClipboardControl instance) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.LocalClipboardControl::doInit()();
  }-*/;

  public native static void LocalClipboardControl_doDestroy(LocalClipboardControl instance) /*-{
    instance.@org.kie.workbench.common.stunner.core.client.canvas.controls.clipboard.LocalClipboardControl::doDestroy()();
  }-*/;
}