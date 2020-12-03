package org.jboss.errai.ioc.client;

import com.google.gwt.user.client.ui.Widget;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.workbench.panels.support.PartManager;
import org.uberfire.commons.data.Pair;
import org.uberfire.workbench.model.PartDefinition;

public class Type_factory__o_u_c_w_p_s_PartManager__quals__j_e_i_Any_j_e_i_Default extends Factory<PartManager> { private class Type_factory__o_u_c_w_p_s_PartManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PartManager implements Proxy<PartManager> {
    private final ProxyHelper<PartManager> proxyHelper = new ProxyHelperImpl<PartManager>("Type_factory__o_u_c_w_p_s_PartManager__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PartManager instance) {

    }

    public PartManager asBeanType() {
      return this;
    }

    public void setInstance(final PartManager instance) {
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

    @Override public Pair getActivePart() {
      if (proxyHelper != null) {
        final PartManager proxiedInstance = proxyHelper.getInstance(this);
        final Pair retVal = proxiedInstance.getActivePart();
        return retVal;
      } else {
        return super.getActivePart();
      }
    }

    @Override public boolean hasActivePart() {
      if (proxyHelper != null) {
        final PartManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.hasActivePart();
        return retVal;
      } else {
        return super.hasActivePart();
      }
    }

    @Override public void registerPart(PartDefinition partDef, Widget w) {
      if (proxyHelper != null) {
        final PartManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerPart(partDef, w);
      } else {
        super.registerPart(partDef, w);
      }
    }

    @Override public void removePart(PartDefinition partDef) {
      if (proxyHelper != null) {
        final PartManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.removePart(partDef);
      } else {
        super.removePart(partDef);
      }
    }

    @Override public void clearParts() {
      if (proxyHelper != null) {
        final PartManager proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.clearParts();
      } else {
        super.clearParts();
      }
    }

    @Override public Collection getParts() {
      if (proxyHelper != null) {
        final PartManager proxiedInstance = proxyHelper.getInstance(this);
        final Collection retVal = proxiedInstance.getParts();
        return retVal;
      } else {
        return super.getParts();
      }
    }

    @Override public boolean hasPart(PartDefinition partDef) {
      if (proxyHelper != null) {
        final PartManager proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.hasPart(partDef);
        return retVal;
      } else {
        return super.hasPart(partDef);
      }
    }

    @Override public Widget selectPart(PartDefinition partDef) {
      if (proxyHelper != null) {
        final PartManager proxiedInstance = proxyHelper.getInstance(this);
        final Widget retVal = proxiedInstance.selectPart(partDef);
        return retVal;
      } else {
        return super.selectPart(partDef);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PartManager proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_w_p_s_PartManager__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PartManager.class, "Type_factory__o_u_c_w_p_s_PartManager__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PartManager.class, Object.class });
  }

  public PartManager createInstance(final ContextManager contextManager) {
    final PartManager instance = new PartManager();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PartManager> proxyImpl = new Type_factory__o_u_c_w_p_s_PartManager__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}