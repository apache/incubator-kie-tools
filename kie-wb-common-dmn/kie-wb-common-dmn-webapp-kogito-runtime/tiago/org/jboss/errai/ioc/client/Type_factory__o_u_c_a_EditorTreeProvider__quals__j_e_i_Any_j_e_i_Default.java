package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.authz.EditorTreeProvider;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.resources.i18n.PermissionTreeI18NImpl;
import org.uberfire.client.resources.i18n.PermissionTreeI18n;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.LoadCallback;
import org.uberfire.security.client.authz.tree.LoadOptions;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;
import org.uberfire.security.impl.authz.DefaultPermissionManager;

public class Type_factory__o_u_c_a_EditorTreeProvider__quals__j_e_i_Any_j_e_i_Default extends Factory<EditorTreeProvider> { private class Type_factory__o_u_c_a_EditorTreeProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends EditorTreeProvider implements Proxy<EditorTreeProvider> {
    private final ProxyHelper<EditorTreeProvider> proxyHelper = new ProxyHelperImpl<EditorTreeProvider>("Type_factory__o_u_c_a_EditorTreeProvider__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final EditorTreeProvider instance) {

    }

    public EditorTreeProvider asBeanType() {
      return this;
    }

    public void setInstance(final EditorTreeProvider instance) {
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

    @Override public PermissionNode buildRootNode() {
      if (proxyHelper != null) {
        final EditorTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        final PermissionNode retVal = proxiedInstance.buildRootNode();
        return retVal;
      } else {
        return super.buildRootNode();
      }
    }

    @Override public void loadChildren(PermissionNode parent, LoadOptions options, LoadCallback callback) {
      if (proxyHelper != null) {
        final EditorTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.loadChildren(parent, options, callback);
      } else {
        super.loadChildren(parent, options, callback);
      }
    }

    @Override public int getRootNodePosition() {
      if (proxyHelper != null) {
        final EditorTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getRootNodePosition();
        return retVal;
      } else {
        return super.getRootNodePosition();
      }
    }

    @Override public void setRootNodePosition(int rootNodePosition) {
      if (proxyHelper != null) {
        final EditorTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.setRootNodePosition(rootNodePosition);
      } else {
        super.setRootNodePosition(rootNodePosition);
      }
    }

    @Override public void registerEditor(String editorId, String editorName) {
      if (proxyHelper != null) {
        final EditorTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.registerEditor(editorId, editorName);
      } else {
        super.registerEditor(editorId, editorName);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final EditorTreeProvider proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_a_EditorTreeProvider__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(EditorTreeProvider.class, "Type_factory__o_u_c_a_EditorTreeProvider__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { EditorTreeProvider.class, Object.class, PermissionTreeProvider.class });
  }

  public EditorTreeProvider createInstance(final ContextManager contextManager) {
    final PermissionManager _permissionManager_2 = (DefaultPermissionManager) contextManager.getInstance("Type_factory__o_u_s_i_a_DefaultPermissionManager__quals__j_e_i_Any_j_e_i_Default");
    final ActivityBeansCache _activityBeansCache_0 = (ActivityBeansCache) contextManager.getInstance("Type_factory__o_u_c_m_ActivityBeansCache__quals__j_e_i_Any_j_e_i_Default");
    final SyncBeanManager _iocManager_1 = (SyncBeanManager) contextManager.getInstance("Producer_factory__o_j_e_i_c_c_SyncBeanManager__quals__j_e_i_Any_j_e_i_Default");
    final PermissionTreeI18n _i18n_3 = (PermissionTreeI18NImpl) contextManager.getInstance("Type_factory__o_u_c_r_i_PermissionTreeI18NImpl__quals__j_e_i_Any_j_e_i_Default");
    final EditorTreeProvider instance = new EditorTreeProvider(_activityBeansCache_0, _iocManager_1, _permissionManager_2, _i18n_3);
    registerDependentScopedReference(instance, _iocManager_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<EditorTreeProvider> proxyImpl = new Type_factory__o_u_c_a_EditorTreeProvider__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}