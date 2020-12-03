package org.jboss.errai.ioc.client;

import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.plugin.type.BasePluginResourceTypeDefinition;
import org.uberfire.ext.plugin.type.DynamicMenuResourceTypeDefinition;
import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.category.Others;
import org.uberfire.workbench.type.ResourceTypeDefinition;

public class Type_factory__o_u_e_p_t_DynamicMenuResourceTypeDefinition__quals__j_e_i_Any_j_e_i_Default extends Factory<DynamicMenuResourceTypeDefinition> { private class Type_factory__o_u_e_p_t_DynamicMenuResourceTypeDefinition__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DynamicMenuResourceTypeDefinition implements Proxy<DynamicMenuResourceTypeDefinition> {
    private final ProxyHelper<DynamicMenuResourceTypeDefinition> proxyHelper = new ProxyHelperImpl<DynamicMenuResourceTypeDefinition>("Type_factory__o_u_e_p_t_DynamicMenuResourceTypeDefinition__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DynamicMenuResourceTypeDefinition instance) {

    }

    public DynamicMenuResourceTypeDefinition asBeanType() {
      return this;
    }

    public void setInstance(final DynamicMenuResourceTypeDefinition instance) {
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

    @Override public String getShortName() {
      if (proxyHelper != null) {
        final DynamicMenuResourceTypeDefinition proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getShortName();
        return retVal;
      } else {
        return super.getShortName();
      }
    }

    @Override public String getDescription() {
      if (proxyHelper != null) {
        final DynamicMenuResourceTypeDefinition proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDescription();
        return retVal;
      } else {
        return super.getDescription();
      }
    }

    @Override public String getSuffix() {
      if (proxyHelper != null) {
        final DynamicMenuResourceTypeDefinition proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getSuffix();
        return retVal;
      } else {
        return super.getSuffix();
      }
    }

    @Override public Category getCategory() {
      if (proxyHelper != null) {
        final DynamicMenuResourceTypeDefinition proxiedInstance = proxyHelper.getInstance(this);
        final Category retVal = proxiedInstance.getCategory();
        return retVal;
      } else {
        return super.getCategory();
      }
    }

    @Override public String getPrefix() {
      if (proxyHelper != null) {
        final DynamicMenuResourceTypeDefinition proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getPrefix();
        return retVal;
      } else {
        return super.getPrefix();
      }
    }

    @Override public int getPriority() {
      if (proxyHelper != null) {
        final DynamicMenuResourceTypeDefinition proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.getPriority();
        return retVal;
      } else {
        return super.getPriority();
      }
    }

    @Override public String getSimpleWildcardPattern() {
      if (proxyHelper != null) {
        final DynamicMenuResourceTypeDefinition proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getSimpleWildcardPattern();
        return retVal;
      } else {
        return super.getSimpleWildcardPattern();
      }
    }

    @Override public boolean accept(Path path) {
      if (proxyHelper != null) {
        final DynamicMenuResourceTypeDefinition proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.accept(path);
        return retVal;
      } else {
        return super.accept(path);
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DynamicMenuResourceTypeDefinition proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_e_p_t_DynamicMenuResourceTypeDefinition__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DynamicMenuResourceTypeDefinition.class, "Type_factory__o_u_e_p_t_DynamicMenuResourceTypeDefinition__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DynamicMenuResourceTypeDefinition.class, BasePluginResourceTypeDefinition.class, Object.class, ResourceTypeDefinition.class });
  }

  public DynamicMenuResourceTypeDefinition createInstance(final ContextManager contextManager) {
    final Others _category_0 = (Others) contextManager.getInstance("Type_factory__o_u_w_c_Others__quals__j_e_i_Any_j_e_i_Default");
    final DynamicMenuResourceTypeDefinition instance = new DynamicMenuResourceTypeDefinition(_category_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DynamicMenuResourceTypeDefinition> proxyImpl = new Type_factory__o_u_e_p_t_DynamicMenuResourceTypeDefinition__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}