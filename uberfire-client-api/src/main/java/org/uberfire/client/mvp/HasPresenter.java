/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.uberfire.client.mvp;


/**
 * Interface to inject a Presenter into a View for MVP-based Element
 * implementations. Due to limitations with CDI it is not possible to {@code @Inject}
 * the correct instance of a Presenter into a View.
 * <p>
 * Developers wishing to implement MVP-based UIs are encouraged to have
 * their View implement this interface if they require access to the appropriate
 * Presenter.
 * @param <T> The Presenter type
 */
public interface HasPresenter<T> {

    void init(T presenter);

}
