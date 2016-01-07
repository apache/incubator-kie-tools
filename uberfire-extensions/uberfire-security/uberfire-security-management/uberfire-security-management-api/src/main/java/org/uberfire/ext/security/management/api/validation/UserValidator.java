/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.api.validation;

import org.jboss.errai.security.shared.api.identity.User;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.metadata.ConstraintDescriptor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * <p>The base validator class for a User entity based on JSR303 Bean Validations.</p>
 * <p>Current validations:</p>
 * <ul>
 *     <li>User identifier (username) is mandatory</li>
 * </ul>
 * <p>This provides validation logic for both backend and client sides, but you have to provide an instantiable class that provides the error message descriptions for each validation error supported.</p>
 * @since 0.8.0
 */
public abstract class UserValidator implements EntityValidator<User> {
    
    public static final String KEY_NAME_NOT_EMPTY = "nameNotEmpty";
    
    @Override
    public Set<ConstraintViolation<User>> validate(final User entity) {
        if (entity == null) return null;
        final String id = entity.getIdentifier();
        Set<ConstraintViolation<User>> result = new HashSet<ConstraintViolation<User>>(1);
        // Validate user name not empty.
        if (id == null || id.trim().length() == 0) {
            final String msg = getMessage(KEY_NAME_NOT_EMPTY);
            result.add(createViolation(entity, "identifier", msg));
        }
        return result;
    }
    
    public abstract String getMessage(final String key);
    
    private ConstraintViolation<User> createViolation(final User user, final String attribute, final String message) {
        if (user == null) return null;
        
        return new ConstraintViolation<User>() {
            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public String getMessageTemplate() {
                return message;
            }

            @Override
            public User getRootBean() {
                return user;
            }

            @Override
            public Class<User> getRootBeanClass() {
                return User.class;
            }

            @Override
            public Object getLeafBean() {
                return null;
            }

            @Override
            public Path getPropertyPath() {
                return new Path() {
                    @Override
                    public Iterator<Node> iterator() {
                        Set<Node> nodes = new HashSet<Node>(1);
                        nodes.add(new Node() {
                            @Override
                            public String getName() {
                                return attribute;
                            }

                            @Override
                            public boolean isInIterable() {
                                return false;
                            }

                            @Override
                            public Integer getIndex() {
                                return 0;
                            }

                            @Override
                            public Object getKey() {
                                return attribute;
                            }
                        });
                        return nodes.iterator();
                    }
                };
            }

            @Override
            public Object getInvalidValue() {
                return null;
            }

            @Override
            public ConstraintDescriptor<?> getConstraintDescriptor() {
                return null;
            }
        };
    }
    
}
