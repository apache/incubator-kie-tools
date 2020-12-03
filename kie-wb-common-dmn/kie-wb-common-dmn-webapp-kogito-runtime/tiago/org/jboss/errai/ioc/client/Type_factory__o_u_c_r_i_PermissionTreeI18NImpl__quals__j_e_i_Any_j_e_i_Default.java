package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.resources.i18n.PermissionTreeI18NImpl;
import org.uberfire.client.resources.i18n.PermissionTreeI18n;

public class Type_factory__o_u_c_r_i_PermissionTreeI18NImpl__quals__j_e_i_Any_j_e_i_Default extends Factory<PermissionTreeI18NImpl> { private class Type_factory__o_u_c_r_i_PermissionTreeI18NImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PermissionTreeI18NImpl implements Proxy<PermissionTreeI18NImpl> {
    private final ProxyHelper<PermissionTreeI18NImpl> proxyHelper = new ProxyHelperImpl<PermissionTreeI18NImpl>("Type_factory__o_u_c_r_i_PermissionTreeI18NImpl__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PermissionTreeI18NImpl instance) {

    }

    public PermissionTreeI18NImpl asBeanType() {
      return this;
    }

    public void setInstance(final PermissionTreeI18NImpl instance) {
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

    @Override public String perspectivesNodeName() {
      if (proxyHelper != null) {
        final PermissionTreeI18NImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.perspectivesNodeName();
        return retVal;
      } else {
        return super.perspectivesNodeName();
      }
    }

    @Override public String perspectivesNodeHelp() {
      if (proxyHelper != null) {
        final PermissionTreeI18NImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.perspectivesNodeHelp();
        return retVal;
      } else {
        return super.perspectivesNodeHelp();
      }
    }

    @Override public String perspectiveResourceName() {
      if (proxyHelper != null) {
        final PermissionTreeI18NImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.perspectiveResourceName();
        return retVal;
      } else {
        return super.perspectiveResourceName();
      }
    }

    @Override public String perspectiveCreate() {
      if (proxyHelper != null) {
        final PermissionTreeI18NImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.perspectiveCreate();
        return retVal;
      } else {
        return super.perspectiveCreate();
      }
    }

    @Override public String perspectiveRead() {
      if (proxyHelper != null) {
        final PermissionTreeI18NImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.perspectiveRead();
        return retVal;
      } else {
        return super.perspectiveRead();
      }
    }

    @Override public String perspectiveUpdate() {
      if (proxyHelper != null) {
        final PermissionTreeI18NImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.perspectiveUpdate();
        return retVal;
      } else {
        return super.perspectiveUpdate();
      }
    }

    @Override public String perspectiveDelete() {
      if (proxyHelper != null) {
        final PermissionTreeI18NImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.perspectiveDelete();
        return retVal;
      } else {
        return super.perspectiveDelete();
      }
    }

    @Override public String editorsNodeName() {
      if (proxyHelper != null) {
        final PermissionTreeI18NImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.editorsNodeName();
        return retVal;
      } else {
        return super.editorsNodeName();
      }
    }

    @Override public String editorsNodeHelp() {
      if (proxyHelper != null) {
        final PermissionTreeI18NImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.editorsNodeHelp();
        return retVal;
      } else {
        return super.editorsNodeHelp();
      }
    }

    @Override public String editorResourceName() {
      if (proxyHelper != null) {
        final PermissionTreeI18NImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.editorResourceName();
        return retVal;
      } else {
        return super.editorResourceName();
      }
    }

    @Override public String editorRead() {
      if (proxyHelper != null) {
        final PermissionTreeI18NImpl proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.editorRead();
        return retVal;
      } else {
        return super.editorRead();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PermissionTreeI18NImpl proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_r_i_PermissionTreeI18NImpl__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PermissionTreeI18NImpl.class, "Type_factory__o_u_c_r_i_PermissionTreeI18NImpl__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PermissionTreeI18NImpl.class, Object.class, PermissionTreeI18n.class });
  }

  public PermissionTreeI18NImpl createInstance(final ContextManager contextManager) {
    final PermissionTreeI18NImpl instance = new PermissionTreeI18NImpl();
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PermissionTreeI18NImpl> proxyImpl = new Type_factory__o_u_c_r_i_PermissionTreeI18NImpl__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}