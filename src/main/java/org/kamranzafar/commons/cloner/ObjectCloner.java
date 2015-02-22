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

/**
 * Created by kamran on 21/02/15.
 */
public class ObjectCloner<T> {
    private CloningStrategy<T> cloningStrategy;

    public ObjectCloner(){
        cloningStrategy = new ReflectionCloningStrategy<T>();
    }

    public ObjectCloner(CloningStrategy cloningStrategy){
        this.cloningStrategy = cloningStrategy;
    }

    public T shallowClone(T object){
        return cloningStrategy.shallowClone(object);
    }

    public T deepClone(T object){
        return cloningStrategy.deepClone(object);
    }
}
