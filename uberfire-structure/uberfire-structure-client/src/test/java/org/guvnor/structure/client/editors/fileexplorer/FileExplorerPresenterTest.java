/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.editors.fileexplorer;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.guvnor.structure.client.editors.context.GuvnorStructureContext;
import org.guvnor.structure.client.editors.context.GuvnorStructureContextBranchChangeHandler;
import org.guvnor.structure.client.editors.context.GuvnorStructureContextChangeHandler;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;

import static org.guvnor.structure.client.editors.TestUtil.makeRepository;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class FileExplorerPresenterTest {

    @Mock
    private FileExplorerView view;

    private GuvnorStructureContext context;

    private FileExplorerPresenter presenter;
    private Collection<Repository> repositories;
    private Repository myRepo;
    private String myRepoBranch = "master";
    private Repository yourRepo;
    private String yourRepoBranch = "master";

    private GuvnorStructureContextChangeHandler.HandlerRegistration changeHandlerRegistration;
    private GuvnorStructureContextBranchChangeHandler.HandlerRegistration branchChangeHandlerRegistration;

    @Before
    public void setUp() throws Exception {
        repositories = new ArrayList<>();

        myRepo = makeRepository("my-repo",
                                "master",
                                "dev");
        repositories.add(myRepo);
        yourRepo = makeRepository("your-repo",
                                  "master",
                                  "release");
        repositories.add(yourRepo);

        context = spy(new GuvnorStructureContext() {
            @Override
            public void getRepositories(final Callback<Collection<Repository>> callback) {
                callback.callback(repositories);
            }

            @Override
            public GuvnorStructureContextChangeHandler.HandlerRegistration addGuvnorStructureContextChangeHandler(final GuvnorStructureContextChangeHandler handler) {
                return changeHandlerRegistration;
            }

            @Override
            public GuvnorStructureContextBranchChangeHandler.HandlerRegistration addGuvnorStructureContextBranchChangeHandler(final GuvnorStructureContextBranchChangeHandler handler) {
                return branchChangeHandlerRegistration;
            }

            @Override
            public String getCurrentBranch(final String alias) {
                if (alias.equals("my-repo")) {
                    return myRepoBranch;
                } else if (alias.equals("your-repo")) {
                    return yourRepoBranch;
                } else {
                    return null;
                }
            }
        });

        presenter = new FileExplorerPresenter(view,
                                              context);
    }

    @Test
    public void testSetPresenter() throws Exception {
        verify(view).init(presenter);
    }

    @Test
    public void testAddHandlers() throws Exception {
        verify(context).addGuvnorStructureContextChangeHandler(presenter);
        verify(context).addGuvnorStructureContextBranchChangeHandler(presenter);
    }

    @Test
    public void testRemoveHandlers() throws Exception {

        verify(context,
               never()).removeHandler(any(GuvnorStructureContextBranchChangeHandler.HandlerRegistration.class));
        verify(context,
               never()).removeHandler(any(GuvnorStructureContextChangeHandler.HandlerRegistration.class));

        presenter.onShutdown();

        verify(context).removeHandler(branchChangeHandlerRegistration);
        verify(context).removeHandler(changeHandlerRegistration);
    }

    @Test
    public void testOnStartUp() throws Exception {

        // my repo is not in the default master branch
        myRepoBranch = "dev";

        presenter.reset();

        verify(view,
               times(2)).addNewRepository(any(Repository.class),
                                          anyString());
        verify(view).addNewRepository(myRepo,
                                      "dev");
        verify(view).addNewRepository(yourRepo,
                                      "master");
    }
}
