/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.security.client;

import com.google.inject.Inject;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.IOCBeanManager;
import org.uberfire.security.authz.AccessDecisionManager;
import org.uberfire.security.Identity;
import org.uberfire.security.Principal;
import org.uberfire.security.impl.Anonymous;
import org.uberfire.security.impl.DefaultAccessDecisionManagerImpl;

@EntryPoint
public class SecurityEntryPoint {

    @Inject
    private IOCBeanManager iocManager;

    @AfterInitialization
    public void checkSecurity() {
        Principal principal = null;
        try {
            final IOCBeanDef<Principal> principalBean = iocManager.lookupBean(Principal.class);
            if (principalBean == null) {
                principal = registerAnonymous();
            } else {
                principal = principalBean.getInstance();
            }
        } catch (Exception ex) {
            principal = registerAnonymous();
        }

        try {
            final IOCBeanDef<AccessDecisionManager> accessDecisionManagerBean = iocManager.lookupBean(AccessDecisionManager.class);
            if (accessDecisionManagerBean == null) {
                registerDefault(principal);
            }
        } catch (Exception ex) {
            registerDefault(principal);
        }
    }

    private Principal registerAnonymous() {
        final Anonymous anonymous = new Anonymous();

        iocManager.addBean(Object.class, Principal.class, null, anonymous, null);
        iocManager.addBean(Object.class, Identity.class, null, anonymous, null);

        return anonymous;
    }

    private void registerDefault(final Principal principal) {
        iocManager.addBean(Object.class, AccessDecisionManager.class, null, new DefaultAccessDecisionManagerImpl(principal), null);
    }

}
