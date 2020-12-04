package org.jboss.errai.ioc.client;

import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.authz.PerspectiveTreeProvider;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.resources.i18n.PermissionTreeI18NImpl;
import org.uberfire.client.resources.i18n.PermissionTreeI18n;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.LoadCallback;
import org.uberfire.security.client.authz.tree.LoadOptions;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;
import org.uberfire.security.impl.authz.DefaultPermissionManager;

public class Type_factory__o_u_c_a_PerspectiveTreeProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<PerspectiveTreeProvider> { private class Type_factory__o_u_c_a_PerspectiveTreeProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends PerspectiveTreeProvider implements Proxy<PerspectiveTreeProvider> {
    private final ProxyHelper<PerspectiveTreeProvider> proxyHelper = new ProxyHelperImpl<PerspectiveTreeProvider>("Type_factory__o_u_c_a_PerspectiveTreeProvider__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final PerspectiveTreeProvider instance) {

    }

    public PerspectiveTreeProvider asBeanType() {
      return this;
    }

    public void setInstance(final PerspectiveTreeProvider instance) {
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

    @Override public boolean isActive() {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isActive();
        return retVal;
      } else {
        return super.isActive();
      }
    }

    @Override public void setActive(boolean active) {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setActive(active);
      } else {
        super.setActive(active);
      }
    }

    @Override public String getResourceName() {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getResourceName();
        return retVal;
      } else {
        return super.getResourceName();
      }
    }

    @Override public void setResourceName(String resourceName) {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setResourceName(resourceName);
      } else {
        super.setResourceName(resourceName);
      }
    }

    @Override public String getRootNodeName() {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getRootNodeName();
        return retVal;
      } else {
        return super.getRootNodeName();
      }
    }

    @Override public void setRootNodeName(String rootNodeName) {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setRootNodeName(rootNodeName);
      } else {
        super.setRootNodeName(rootNodeName);
      }
    }

    @Override public int getRootNodePosition() {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getRootNodePosition();
        return retVal;
      } else {
        return super.getRootNodePosition();
      }
    }

    @Override public void setRootNodePosition(int rootNodePosition) {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setRootNodePosition(rootNodePosition);
      } else {
        super.setRootNodePosition(rootNodePosition);
      }
    }

    @Override public void excludePerspectiveId(String perspectiveId) {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.excludePerspectiveId(perspectiveId);
      } else {
        super.excludePerspectiveId(perspectiveId);
      }
    }

    @Override public Set getPerspectiveIdsExcluded() {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        final Set retVal = proxiedInstance.getPerspectiveIdsExcluded();
        return retVal;
      } else {
        return super.getPerspectiveIdsExcluded();
      }
    }

    @Override public PermissionNode buildRootNode() {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        final PermissionNode retVal = proxiedInstance.buildRootNode();
        return retVal;
      } else {
        return super.buildRootNode();
      }
    }

    @Override public void loadChildren(PermissionNode parent, LoadOptions options, LoadCallback callback) {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.loadChildren(parent, options, callback);
      } else {
        super.loadChildren(parent, options, callback);
      }
    }

    @Override public String getPerspectiveName(String perspectiveId) {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getPerspectiveName(perspectiveId);
        return retVal;
      } else {
        return super.getPerspectiveName(perspectiveId);
      }
    }

    @Override public void setPerspectiveName(String perspectiveId, String name) {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setPerspectiveName(perspectiveId, name);
      } else {
        super.setPerspectiveName(perspectiveId, name);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final PerspectiveTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_a_PerspectiveTreeProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(PerspectiveTreeProvider.class, "Type_factory__o_u_c_a_PerspectiveTreeProvider__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { PerspectiveTreeProvider.class, Object.class, PermissionTreeProvider.class });
  }

  public PerspectiveTreeProvider createInstance(final ContextManager contextManager) {
    final PermissionTreeI18n _i18n_2 = (PermissionTreeI18NImpl) contextManager.getInstance("Type_factory__o_u_c_r_i_PermissionTreeI18NImpl__quals__j_e_i_Any_j_e_i_Default");
    final ActivityBeansCache _activityBeansCache_0 = (ActivityBeansCache) contextManager.getInstance("Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default");
    final PermissionManager _permissionManager_1 = (DefaultPermissionManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultPermissionManager__quals__j_e_i_Any_j_e_i_Default");
    final PerspectiveTreeProvider instance = new PerspectiveTreeProvider(_activityBeansCache_0, _permissionManager_1, _i18n_2);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<PerspectiveTreeProvider> proxyImpl = new Type_factory__o_u_c_a_PerspectiveTreeProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}