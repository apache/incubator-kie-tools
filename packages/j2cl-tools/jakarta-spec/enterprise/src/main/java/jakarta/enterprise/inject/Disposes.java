/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jakarta.enterprise.inject;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Identifies the disposed parameter of a disposer method. May be applied to a parameter of a method
 * of a bean class.
 *
 * <pre>
 * public class UserDatabaseEntityManager {
 *
 *     &#064;Produces
 *     &#064;ConversationScoped
 *     &#064;UserDatabase
 *     public EntityManager create(EntityManagerFactory emf) {
 *         return emf.createEntityManager();
 *     }
 *
 *     public void close(@Disposes @UserDatabase EntityManager em) {
 *         em.close();
 *     }
 *
 * }
 * </pre>
 *
 * <pre>
 * public class Resources {
 *
 *     &#064;PersistenceContext
 *     &#064;Produces
 *     &#064;UserDatabase
 *     private EntityManager em;
 *
 *     public void close(@Disposes @UserDatabase EntityManager em) {
 *         em.close();
 *     }
 *
 * }
 * </pre>
 *
 * <p>A disposer method allows the application to perform customized cleanup of an object returned
 * by a {@linkplain Produces producer method or producer field}.
 *
 * <p>A disposer method must be a non-abstract method of a managed bean class or session bean class.
 * A disposer method may be either static or non-static. If the bean is a session bean, the disposer
 * method must be a business method of the EJB or a static method of the bean class.
 *
 * <p>A bean may declare multiple disposer methods.
 *
 * <p>Each disposer method must have exactly one disposed parameter, of the same type as the
 * corresponding producer method or producer field return type. When searching for disposer methods
 * for a producer method or producer field, the container considers the type and qualifiers of the
 * disposed parameter. If a disposed parameter resolves to a producer method or producer field
 * declared by the same bean class, the container must call this method when destroying any instance
 * returned by that producer method or producer field.
 *
 * <p>In addition to the disposed parameter, a disposer method may declare additional parameters,
 * which may also specify qualifiers. These additional parameters are injection points.
 *
 * <pre>
 * public void close(&#064;Disposes &#064;UserDatabase EntityManager em, Logger log) { ... }
 * </pre>
 *
 * <p>A disposer method may resolve to multiple producer methods or producer fields declared by the
 * bean class, in which case the container must call it when destroying any instance returned by any
 * of these producer methods or producer fields.
 *
 * <p>Disposer methods are not inherited by bean subclasses.
 *
 * <p>Interceptors and decorators may not declare disposer methods.
 *
 * @see Produces &#064;Produces
 * @author Gavin King
 * @author Pete Muir
 */
@Target(PARAMETER)
@Retention(RUNTIME)
@Documented
public @interface Disposes {}
