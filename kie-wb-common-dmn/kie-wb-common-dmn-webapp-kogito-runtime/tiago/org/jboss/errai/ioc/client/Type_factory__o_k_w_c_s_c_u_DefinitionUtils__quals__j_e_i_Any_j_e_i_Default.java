package org.jboss.errai.ioc.client;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ApplicationScoped;
import org.jboss.errai.ioc.client.container.Context;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.ioc.client.container.Proxy;
import org.jboss.errai.ioc.client.container.ProxyHelper;
import org.jboss.errai.ioc.client.container.ProxyHelperImpl;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.api.ClientDefinitionManager;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.registry.impl.DefinitionsCacheRegistry;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default extends Factory<DefinitionUtils> { private class Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl extends DefinitionUtils implements Proxy<DefinitionUtils> {
    private final ProxyHelper<DefinitionUtils> proxyHelper = new ProxyHelperImpl<DefinitionUtils>("Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default");
    public void initProxyProperties(final DefinitionUtils instance) {

    }

    public DefinitionUtils asBeanType() {
      return this;
    }

    public void setInstance(final DefinitionUtils instance) {
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

    @Override public String getName(Object definition) {
      if (proxyHelper != null) {
        final DefinitionUtils proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getName(definition);
        return retVal;
      } else {
        return super.getName(definition);
      }
    }

    @Override public String getNameIdentifier(Object definition) {
      if (proxyHelper != null) {
        final DefinitionUtils proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getNameIdentifier(definition);
        return retVal;
      } else {
        return super.getNameIdentifier(definition);
      }
    }

    @Override public String getTitle(String definitionId) {
      if (proxyHelper != null) {
        final DefinitionUtils proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getTitle(definitionId);
        return retVal;
      } else {
        return super.getTitle(definitionId);
      }
    }

    @Override public Bounds buildBounds(Object definition, double x, double y) {
      if (proxyHelper != null) {
        final DefinitionUtils proxiedInstance = proxyHelper.getInstance(this);
        final Bounds retVal = proxiedInstance.buildBounds(definition, x, y);
        return retVal;
      } else {
        return super.buildBounds(definition, x, y);
      }
    }

    @Override public MorphDefinition getMorphDefinition(Object definition) {
      if (proxyHelper != null) {
        final DefinitionUtils proxiedInstance = proxyHelper.getInstance(this);
        final MorphDefinition retVal = proxiedInstance.getMorphDefinition(definition);
        return retVal;
      } else {
        return super.getMorphDefinition(definition);
      }
    }

    @Override public boolean hasMorphTargets(Object definition) {
      if (proxyHelper != null) {
        final DefinitionUtils proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.hasMorphTargets(definition);
        return retVal;
      } else {
        return super.hasMorphTargets(definition);
      }
    }

    @Override public String getDefinitionSetId(Class type) {
      if (proxyHelper != null) {
        final DefinitionUtils proxiedInstance = proxyHelper.getInstance(this);
        final String retVal = proxiedInstance.getDefinitionSetId(type);
        return retVal;
      } else {
        return super.getDefinitionSetId(type);
      }
    }

    @Override public String[] getDefinitionIds(Object definition) {
      if (proxyHelper != null) {
        final DefinitionUtils proxiedInstance = proxyHelper.getInstance(this);
        final String[] retVal = proxiedInstance.getDefinitionIds(definition);
        return retVal;
      } else {
        return super.getDefinitionIds(definition);
      }
    }

    @Override public boolean isAllPolicy(MorphDefinition definition) {
      if (proxyHelper != null) {
        final DefinitionUtils proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isAllPolicy(definition);
        return retVal;
      } else {
        return super.isAllPolicy(definition);
      }
    }

    @Override public boolean isNonePolicy(MorphDefinition definition) {
      if (proxyHelper != null) {
        final DefinitionUtils proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isNonePolicy(definition);
        return retVal;
      } else {
        return super.isNonePolicy(definition);
      }
    }

    @Override public boolean isDefaultPolicy(MorphDefinition definition) {
      if (proxyHelper != null) {
        final DefinitionUtils proxiedInstance = proxyHelper.getInstance(this);
        final boolean retVal = proxiedInstance.isDefaultPolicy(definition);
        return retVal;
      } else {
        return super.isDefaultPolicy(definition);
      }
    }

    @Override public Annotation getQualifier(String defSetId) {
      if (proxyHelper != null) {
        final DefinitionUtils proxiedInstance = proxyHelper.getInstance(this);
        final Annotation retVal = proxiedInstance.getQualifier(defSetId);
        return retVal;
      } else {
        return super.getQualifier(defSetId);
      }
    }

    @Override public DefinitionManager getDefinitionManager() {
      if (proxyHelper != null) {
        final DefinitionUtils proxiedInstance = proxyHelper.getInstance(this);
        final DefinitionManager retVal = proxiedInstance.getDefinitionManager();
        return retVal;
      } else {
        return super.getDefinitionManager();
      }
    }

    @Override public int hashCode() {
      if (proxyHelper != null) {
        final DefinitionUtils proxiedInstance = proxyHelper.getInstance(this);
        final int retVal = proxiedInstance.hashCode();
        return retVal;
      } else {
        return super.hashCode();
      }
    }
  }
  public Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(DefinitionUtils.class, "Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_Default", ApplicationScoped.class, false, null, true));
    handle.setAssignableTypes(new Class[] { DefinitionUtils.class, Object.class });
  }

  public DefinitionUtils createInstance(final ContextManager contextManager) {
    final DefinitionsCacheRegistry _definitionsRegistry_1 = (DefinitionsCacheRegistry) contextManager.getInstance("Producer_factory__o_k_w_c_s_c_r_i_DefinitionsCacheRegistry__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionManager _definitionManager_0 = (ClientDefinitionManager) contextManager.getInstance("Type_factory__o_k_w_c_s_c_c_a_ClientDefinitionManager__quals__j_e_i_Any_j_e_i_Default");
    final DefinitionUtils instance = new DefinitionUtils(_definitionManager_0, _definitionsRegistry_1);
    setIncompleteInstance(instance);
    setIncompleteInstance(null);
    return instance;
  }

  public Proxy createProxy(final Context context) {
    final Proxy<DefinitionUtils> proxyImpl = new Type_factory__o_k_w_c_s_c_u_DefinitionUtils__quals__j_e_i_Any_j_e_i_DefaultProxyImpl();
    proxyImpl.setProxyContext(context);
    return proxyImpl;
  }
}