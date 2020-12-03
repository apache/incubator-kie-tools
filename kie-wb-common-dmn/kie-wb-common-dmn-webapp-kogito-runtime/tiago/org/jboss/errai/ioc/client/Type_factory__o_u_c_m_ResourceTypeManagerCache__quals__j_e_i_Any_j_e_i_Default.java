package org.jboss.errai.ioc.client;

import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.uberfire.client.mvp.ActivityAndMetaInfo;
import org.uberfire.client.mvp.CategoriesManagerCache;
import org.uberfire.client.mvp.ResourceTypeManagerCache;
import org.uberfire.workbench.category.Category;

public class Type_factory__o_u_c_m_ResourceTypeManagerCache__quals__j_e_i_Any_j_e_i_Default extends Factory<ResourceTypeManagerCache> { private class Type_factory__o_u_c_m_ResourceTypeManagerCache__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends ResourceTypeManagerCache implements Proxy<ResourceTypeManagerCache> {
    private final ProxyHelper<ResourceTypeManagerCache> proxyHelper = new ProxyHelperImpl<ResourceTypeManagerCache>("Type_factory__o_u_c_m_ResourceTypeManagerCache__quals__j_e_i_Any_j_e_i_Default");
    public Type_factory__o_u_c_m_ResourceTypeManagerCache__quals__j_e_i_Any_j_e_i_DefaultProxyImpl() {
      super(null);
    }

    public void initProxyProperties(final ResourceTypeManagerCache instance) {

    }

    public ResourceTypeManagerCache asBeanType() {
      return this;
    }

    public void setInstance(final ResourceTypeManagerCache instance) {
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

    @Override public void addAll(List resourceTypeDefinitions) {
      if (proxyHelper != null) {
        final ResourceTypeManagerCache proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addAll(resourceTypeDefinitions);
      } else {
        super.addAll(resourceTypeDefinitions);
      }
    }

    @Override public Set getResourceTypeDefinitions() {
      if (proxyHelper != null) {
        final ResourceTypeManagerCache proxiedInstance = proxyHelper.getInstance(this);
        final Set retVal = proxiedInstance.getResourceTypeDefinitions();
        return retVal;
      } else {
        return super.getResourceTypeDefinitions();
      }
    }

    @Override public List getResourceActivities() {
      if (proxyHelper != null) {
        final ResourceTypeManagerCache proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getResourceActivities();
        return retVal;
      } else {
        return super.getResourceActivities();
      }
    }

    @Override public void addResourceActivity(ActivityAndMetaInfo activityAndMetaInfo) {
      if (proxyHelper != null) {
        final ResourceTypeManagerCache proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.addResourceActivity(activityAndMetaInfo);
      } else {
        super.addResourceActivity(activityAndMetaInfo);
      }
    }

    @Override public List getResourceTypeDefinitionsByCategory(Category category) {
      if (proxyHelper != null) {
        final ResourceTypeManagerCache proxiedInstance = proxyHelper.getInstance(this);
        final List retVal = proxiedInstance.getResourceTypeDefinitionsByCategory(category);
        return retVal;
      } else {
        return super.getResourceTypeDefinitionsByCategory(category);
      }
    }

    @Override public void sortResourceActivitiesByPriority() {
      if (proxyHelper != null) {
        final ResourceTypeManagerCache proxiedInstance = proxyHelper.getInstance(this);
        proxiedInstance.sortResourceActivitiesByPriority();
      } else {
        super.sortResourceActivitiesByPriority();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final ResourceTypeManagerCache proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_u_c_m_ResourceTypeManagerCache__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(ResourceTypeManagerCache.class, "Type_factory__o_u_c_m_ResourceTypeManagerCache__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { ResourceTypeManagerCache.class, Object.class });
  }

  public ResourceTypeManagerCache createInstance(final ContextManager contextManager) {
    final CategoriesManagerCache _categoriesManagerCache_0 = (CategoriesManagerCache) contextManager.getInstance("Type_factory__o_u_c_m_CategoriesManagerCache__quals__j_e_i_Any_j_e_i_Default");
    final ResourceTypeManagerCache instance = new ResourceTypeManagerCache(_categoriesManagerCache_0);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  private Proxy createProxyWithErrorMessage() {
    try {
      return new Type_factory__o_u_c_m_ResourceTypeManagerCache__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    } catch (Throwable t) {
      throw new RuntimeException("While creating a proxy for org.uberfire.client.mvp.ResourceTypeManagerCache an exception was thrown from this constructor: @javax.inject.Inject()  public org.uberfire.client.mvp.ResourceTypeManagerCache ([org.uberfire.client.mvp.CategoriesManagerCache])\nTo fix this problem, add a no-argument public or protected constructor for use in proxying.", t);
    }
  }

  public Proxy createProxy(final Context context) {
    final Proxy<ResourceTypeManagerCache> proxyImpl = createProxyWithErrorMessage();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}