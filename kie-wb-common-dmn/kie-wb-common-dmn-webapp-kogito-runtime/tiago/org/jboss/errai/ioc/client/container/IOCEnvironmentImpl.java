package org.jboss.errai.ioc.client.container;


public class IOCEnvironmentImpl implements IOCEnvironment { public boolean isAsync() {
    return false;
  }

  public ClientBeanManager getNewBeanManager() {
    return new SyncBeanManagerImpl();
  }
}