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

import jakarta.enterprise.context.Dependent;

/**
 * Indicates that a producer method returned a null value or a producer field contained a null
 * value, and the scope of the producer method or field was not {@link Dependent}.
 */
public class IllegalProductException extends InjectionException {

  private static final long serialVersionUID = -6280627846071966243L;

  public IllegalProductException() {
    super();
  }

  public IllegalProductException(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalProductException(String message) {
    super(message);
  }

  public IllegalProductException(Throwable cause) {
    super(cause);
  }
}
