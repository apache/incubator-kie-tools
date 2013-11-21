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

package org.uberfire.security.server;

public interface SecurityConstants {

    final String FORM = "FORM";

    final String AUTH_MANAGER_KEY = "org.uberfire.auth.manager";

    final String AUTHZ_MANAGER_KEY = "org.uberfire.authz.manager";

    final String URL_ACCESS_DECISION_MANAGER_KEY = "org.uberfire.authz.access.url";

    final String ROLE_PROVIDER_KEY = "org.uberfire.auth.provider.role";

    final String SUBJECT_PROPERTIES_PROVIDER_KEY = "org.uberfire.auth.provider.subject.properties";

    final String ROLE_DECISION_MANAGER_KEY = "org.uberfire.authz.role";

    final String URL_VOTING_MANAGER_KEY = "org.uberfire.authz.voting.url";

    final String AUTH_SCHEME_KEY = "org.uberfire.auth.scheme";

    final String AUTH_REMEMBER_ME_SCHEME_KEY = "org.uberfire.auth.rememberme";

    final String AUTH_FORCE_URL = "org.uberfire.auth.force.url";

    final String RESOURCE_MANAGER_KEY = "org.uberfire.resource.manager";

    final String RESOURCE_MANAGER_CONFIG_KEY = "org.uberfire.resource.manager.config";

    final String COOKIE_NAME_KEY = "org.uberfire.cookie.id";

    final String AUTH_PROVIDER_KEY = "org.uberfire.auth.provider";

    final String HTTP_FORM_J_SECURITY_CHECK = "/j_security_check";

    final String HTTP_FORM_J_USERNAME = "j_username";

    final String HTTP_FORM_J_PASSWORD = "j_password";

    final String CONFIG_USERS_PROPERTIES = "users.properties";

    final String URL_FILTER_CONFIG_YAML = "url_filter.yaml";

    final String FORM_AUTH_PAGE = "/login.jsp";

    final String LOGOUT_URI = "/uf_logout";

    final String SUBJECT_ON_SESSION_KEY = "org.uf.subject";

    final String ROLES_IN_CONTEXT_KEY = "org.uf.context.roles";

    final String AUTH_DOMAIN_KEY = "org.uberfire.domain";

}
