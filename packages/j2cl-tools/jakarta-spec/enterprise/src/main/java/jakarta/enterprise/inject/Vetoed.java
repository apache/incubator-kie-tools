/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc., and individual contributors
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Veto the processing of the class. Any beans or observer methods defined by this class will not be
 * installed.
 *
 * <p>When placed on package, all beans in the package are prevented from being installed. If
 * packages are split across jars, non-portable behavior results. An application can prevent
 * packages being split across jars by sealing the package.
 *
 * <p>No container lifecycle events are fired for classes annotated {@link Vetoed}.
 *
 * @author Stuart Douglas
 * @since 1.1
 * @see <a href="https://docs.oracle.com/javase/8/docs/technotes/guides/jar/jar.html#sealing">JAR
 *     File Specification</a>
 */
@Target({ElementType.TYPE, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Vetoed {}
