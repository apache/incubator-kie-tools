/*
 *
 *    Copyright (c) 2014,2015,2016 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *  
 */

package com.ait.lienzo.test.stub.custom;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.ait.lienzo.test.annotation.Stubs;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Please before reading this, take a look at:
 * 
 * @See com.ait.lienzo.test.PointsTest
 * @See com.ait.lienzo.test.PointsMockTest
 *
 * This example provides a custom stub implementation for <code>toString</code> and <code>add</code> methods, as an example 
 * of an alternative for mocking it. 
 * 
 * Note that this unit test class is annotated with the <code>@Stubs</code> annotation, so here you're replacing the built-in stub 
 * by the custom <code>com.ait.lienzo.test.stub.custom.Point2D</code>.
 * 
 * @author Roger Martinez
 * @since 1.0
 *
 */
@RunWith( LienzoMockitoTestRunner.class )
@Stubs({ com.ait.lienzo.test.stub.custom.Point2D.class })
public class StubPointsTest {

    public class MyLienzo {

        private Point2D p;

        public MyLienzo(Point2D p) {
            this.p = p;
        }

        public Point2D test(Point2D p) {
            return this.p.add( p );
        }

    }

    private MyLienzo myLienzo;

    @Before
    public void setup() {
        myLienzo = new MyLienzo( new Point2D( 1, 5 ) );
    }

    @Test
    public void test() {
        
        Point2D p  = myLienzo.test( new Point2D( 2, 3 ) );
        
        Assert.assertEquals( 4, p.getX(), 0 );
        
        Assert.assertEquals( 7, p.getY(), 0 );
        
    }

}
