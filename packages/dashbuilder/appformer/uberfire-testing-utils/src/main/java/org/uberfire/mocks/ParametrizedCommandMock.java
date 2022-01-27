package org.uberfire.mocks;

import org.mockito.stubbing.Stubber;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.Mockito.doAnswer;

public class ParametrizedCommandMock {

    
    public static <T> Stubber executeParametrizedCommandWith(int paramIndex, T value) {
        return doAnswer(ans -> {
            ParameterizedCommand<T> callback = (ParameterizedCommand<T>) ans.getArguments()[paramIndex];
            callback.execute(value);
            return null;
        });   
    }
}
