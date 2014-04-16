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

package org.uberfire.security.authz;

public interface AuthorizationResult {

    final AuthorizationResult ACCESS_GRANTED = new AuthorizationResult() {
        @Override
        public int result() {
            return 1;
        }

        @Override
        public AuthorizationResult invert() {
            return ACCESS_DENIED;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof AuthorizationResult)) {
                return false;
            }
            return result() == ((AuthorizationResult) obj).result();
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public String toString() {
            return "ACCESS_GRANTED";
        }
    };

    final AuthorizationResult ACCESS_ABSTAIN = new AuthorizationResult() {
        @Override
        public int result() {
            return 0;
        }

        @Override
        public AuthorizationResult invert() {
            return this;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof AuthorizationResult)) {
                return false;
            }
            return result() == ((AuthorizationResult) obj).result();
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return "ACCESS_ABSTAIN";
        }
    };

    final AuthorizationResult ACCESS_DENIED = new AuthorizationResult() {
        @Override
        public int result() {
            return -1;
        }

        @Override
        public AuthorizationResult invert() {
            return ACCESS_GRANTED;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof AuthorizationResult)) {
                return false;
            }
            return result() == ((AuthorizationResult) obj).result();
        }

        @Override
        public int hashCode() {
            return -1;
        }

        @Override
        public String toString() {
            return "ACCESS_DENIED";
        }
    };

    int result();

    AuthorizationResult invert();
}
