package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.client.lienzo.canvas.export.LienzoCanvasExport;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExport;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasExportSettings;
import org.kie.workbench.common.stunner.core.client.canvas.export.CanvasURLExportSettings;
import org.uberfire.ext.editor.commons.client.file.exports.svg.IContext2D;

public class Type_factory__o_k_w_c_s_c_l_c_e_LienzoCanvasExport__quals__j_e_i_Any_j_e_i_Default extends Factory<LienzoCanvasExport> { private class Type_factory__o_k_w_c_s_c_l_c_e_LienzoCanvasExport__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends LienzoCanvasExport implements Proxy<LienzoCanvasExport> {
    private final ProxyHelper<LienzoCanvasExport> proxyHelper = new ProxyHelperImpl<LienzoCanvasExport>("Type_factory__o_k_w_c_s_c_l_c_e_LienzoCanvasExport__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final LienzoCanvasExport instance) {

    }

    public LienzoCanvasExport asBeanType() {
      return this;
    }

    public void setInstance(final LienzoCanvasExport instance) {
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

    @Override public IContext2D toContext2D(AbstractCanvasHandler canvasHandler, CanvasExportSettings settings) {
      if (proxyHelper != null) {
        final LienzoCanvasExport proxiedInstance = proxyHelper.getInstance(this);
        final IContext2D retVal = proxiedInstance.toContext2D(canvasHandler, settings);
        return retVal;
      } else {
        return super.toContext2D(canvasHandler, settings);
      }
    }

    @Override public String toImageData(AbstractCanvasHandler canvasHandler, CanvasURLExportSettings settings) {
      if (proxyHelper != null) {
        final LienzoCanvasExport proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.toImageData(canvasHandler, settings);
        return retVal;
      } else {
        return super.toImageData(canvasHandler, settings);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final LienzoCanvasExport proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_l_c_e_LienzoCanvasExport__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(LienzoCanvasExport.class, "Type_factory__o_k_w_c_s_c_l_c_e_LienzoCanvasExport__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { LienzoCanvasExport.class, Object.class, CanvasExport.class });
  }

  public LienzoCanvasExport createInstance(final ContextManager contextManager) {
    final LienzoCanvasExport instance = new LienzoCanvasExport();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<LienzoCanvasExport> proxyImpl = new Type_factory__o_k_w_c_s_c_l_c_e_LienzoCanvasExport__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}