package org.jboss.errai.ioc.client;

import java.io.Serializable;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ioc.client.container.ContextManager;
import org.jboss.errai.ioc.client.container.Factory;
import org.jboss.errai.ioc.client.container.FactoryHandleImpl;
import org.jboss.errai.security.shared.api.identity.BasicUserCache;
import org.jboss.errai.security.shared.api.identity.User;

public class Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default extends Factory<User> { public Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default() {
    super(new FactoryHandleImpl(User.class, "Producer_factory__o_j_e_s_s_a_i_User__quals__j_e_i_Any_j_e_i_Default", Dependent.class, false, null, true));
    handle.setAssignableTypes(new Class[] { User.class, Serializable.class });
  }

  public User createInstance(final ContextManager contextManager) {
    BasicUserCache producerInstance = contextManager.getInstance("Type_factory__o_j_e_s_s_a_i_BasicUserCache__quals__j_e_i_Any_j_e_i_Default");
    producerInstance = Factory.maybeUnwrapProxy(producerInstance);
    final User instance = BasicUserCache_produceActiveUser(producerInstance);
    thisInstance.setReference(instance, "producerInstance", producerInstance);
    return instance;
  }

  public native static User BasicUserCache_produceActiveUser(BasicUserCache instance) /*-{
    return instance.@org.jboss.errai.security.shared.api.identity.BasicUserCache::produceActiveUser()();
  }-*/;
}