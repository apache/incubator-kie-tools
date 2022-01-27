package org.uberfire.mocks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.uberfire.mocks.ParametrizedCommandMock.executeParametrizedCommandWith;

@RunWith(MockitoJUnitRunner.class)
public class ParametrizedCommandMockTest {
    
    
    private static final String TEST_STR = "TEST";
    private static final String NOT_A_TEST_STR = "NOT A TEST STR";
    
    @Mock
    UsesParametrizedCommand usesParametrizedCommand;
    
    @Before
    public void setup() {
        executeParametrizedCommandWith(0, new Foo(TEST_STR))
            .when(usesParametrizedCommand)
            .theKindOfMethodYouWantToMock(any(ParameterizedCommand.class));
    }
    
    @Test
    public void testParametrizedCommandMockTest() {
        usesParametrizedCommand
                .theKindOfMethodYouWantToMock(foo -> assertEquals(TEST_STR, foo.getStr()));
    }
    
    public static class Foo {
        private String str;

        public Foo(String str) {
            super();
            this.str = str;
        }
        
        public String getStr() {
            return str;
        }
    }
    
    public static class UsesParametrizedCommand {

        public void theKindOfMethodYouWantToMock(ParameterizedCommand<Foo> cmd) {
            cmd.execute(new Foo(NOT_A_TEST_STR));
        }
    }

}
