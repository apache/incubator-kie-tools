/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.client.mvp;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.uberfire.client.annotations.WorkbenchEditor.LockingStrategy.OPTIMISTIC;
import static org.uberfire.client.annotations.WorkbenchEditor.LockingStrategy.PESSIMISTIC;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.client.annotations.WorkbenchEditor.LockingStrategy;
import org.uberfire.mvp.PlaceRequest;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class WorkbenchEditorActivityTest {

    @Mock
    private LockManager lockManager;
    
    @Mock
    private PlaceRequest place;
    
    @Mock
    private PlaceManager placeManager;
    
    private class EditorTestActivity extends AbstractWorkbenchEditorActivity {
        private LockingStrategy strategy;
        
        public EditorTestActivity( LockManager lockManager, PlaceManager placeManager, LockingStrategy strategy  ) {
            super( placeManager );
            this.lockManager = lockManager;
            this.strategy = strategy;
        }

        @Override
        public String getIdentifier() {
            return null;
        }

        @Override
        public Collection<String> getRoles() {
            return null;
        }

        @Override
        public String getSignatureId() {
            return null;
        }

        @Override
        public Collection<String> getTraits() {
            return null;
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public IsWidget getWidget() {
            return null;
        }

        @Override
        protected LockingStrategy getLockingStrategy() {
            return strategy;
        }
        
    }
   
    @Test
    public void optimisticLockingDoesNotAcquireLocks() {
        EditorTestActivity activity = new EditorTestActivity (lockManager, placeManager, OPTIMISTIC);
        
        activity.onStartup( place );
        activity.onOpen();
        
        verify(lockManager, never()).acquireLockOnDemand();
    }
    
    @Test
    public void pessimisticLockingAcquiresLocks() {
        EditorTestActivity activity = new EditorTestActivity (lockManager, placeManager, PESSIMISTIC);
        
        activity.onStartup( place );
        activity.onOpen();
        
        verify(lockManager, times(1)).acquireLockOnDemand();
    }

}