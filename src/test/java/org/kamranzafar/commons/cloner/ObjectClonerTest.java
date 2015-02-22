/**
 *
 * Copyright 2015 Kamran Zafar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kamranzafar.commons.cloner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kamran on 21/02/15.
 */
@RunWith(JUnit4.class)
public class ObjectClonerTest {
    @Test
    public void shallowClone(){
        TestObject testObject = newTestObject("object1");
        TestObject testObject1 = newTestObject("object2");
        TestObject testObject2 = newTestObject("object3");

        testObject.setTestObject(testObject1);
        testObject1.setTestObject(testObject2);

        TestObject clone = new ObjectCloner<TestObject>().shallowClone(testObject);

        Assert.assertTrue(testObject.getName().equals(clone.getName()));
    }

    @Test
    public void deepClone(){
        TestObject testObject = newTestObject("object1");
        TestObject testObject1 = newTestObject("object2");
        TestObject testObject2 = newTestObject("object3");

        testObject.setTestObject(testObject1);
        testObject1.setTestObject(testObject2);

        TestObject clone = new ObjectCloner<TestObject>().deepClone(testObject);

        Assert.assertTrue(testObject.getName().equals(clone.getName()));
        Assert.assertNotNull(clone.getTestObject());
        Assert.assertTrue(testObject.getTestObject().getName().equals(testObject1.getName()));
    }

    private TestObject newTestObject(String name){
        TestObject testObject = new TestObject();
        testObject.setName(name);

        Map<String, String> props = new HashMap<String, String>();
        props.put("key1", "val1");
        props.put("key2", "val2");

        testObject.setProps(props);

        return testObject;
    }

    class TestObject {
        private String name;
        private Map<String, String> props;
        private TestObject testObject;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, String> getProps() {
            return props;
        }

        public void setProps(Map<String, String> props) {
            this.props = props;
        }

        public TestObject getTestObject() {
            return testObject;
        }

        public void setTestObject(TestObject testObject) {
            this.testObject = testObject;
        }
    }
}
