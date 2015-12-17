/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package test;

public class Outer
{

    public enum PublicInnerEnum {
        E1, E2
    }

    public static enum PublicStaticInnerEnum {
        E1, E2
    }

    private enum PrivateInnerEnum {
        E1, E2
    }

    private static enum PrivateStaticInnerEnum {
        E1, E2
    }


    public class PublicInnerClass {
        String innerField1;

        public class PublicInnerClassInner {

        }

        public PublicInnerClass()
        {
        }

        public PublicInnerClass(String innerField1) {
            this.innerField1 = innerField1;
        }
    }

    public static class PublicStaticInnerClass {
        String innerField1;

        public PublicStaticInnerClass()
        {
        }

        public PublicStaticInnerClass(String innerField1) {
            this.innerField1 = innerField1;
        }
    }

    private class PrivateInnerClass {
        String innerField1;

        public PrivateInnerClass()
        {
        }

        public PrivateInnerClass(String innerField1) {
            this.innerField1 = innerField1;
        }
    }

    private static class PrivateStaticInnerClass {
        String innerField1;

        public PrivateStaticInnerClass()
        {
        }

        public PrivateStaticInnerClass(String innerField1) {
            this.innerField1 = innerField1;
        }
    }


    //enum fields usages
    PublicInnerEnum publicInnerEnumQualified;

    PublicInnerEnum publicInnerEnum;

    PublicStaticInnerEnum publicStaticInnerEnum;

    PublicStaticInnerEnum publicStaticInnerEnumQualified;

    PrivateInnerEnum privateInnerEnum;

    PrivateInnerEnum privateInnerEnumQualified;

    PrivateStaticInnerEnum privateStaticInnerEnum;

    PrivateStaticInnerEnum privateStaticInnerEnumQualified;

    //inner class usages
    PublicInnerClass publicInnerClassQualified;

    PublicInnerClass publicInnerClass;

    PublicStaticInnerClass publicStaticInnerClassQualified;

    PublicStaticInnerClass publicStaticInnerClass;

    PrivateInnerClass privateInnerClassQualified;

    PrivateInnerClass privateInnerClass;

    PrivateStaticInnerClass privateStaticInnerClassQualified;

    PrivateStaticInnerClass privateStaticInnerClass;

    public Outer() { }

}
