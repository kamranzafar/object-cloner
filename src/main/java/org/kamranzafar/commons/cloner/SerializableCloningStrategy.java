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

import java.io.*;

/**
 * Created by kamran on 21/02/15.
 */
public class SerializableCloningStrategy<T> implements CloningStrategy<T> {
    @Override
    public T shallowClone(T object) {
        throw new CloningException("shallowClone is not supported");
    }

    @Override
    public T deepClone(T object) {
        if (!(object instanceof Serializable)) {
            throw new CloningException("Object is not serializable. Only Serializable objects are supported.");
        }

        return clone(object);
    }

    protected T clone(T original) {
        T clone;

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(5120);
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(original);
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            clone = (T) in.readObject();

            in.close();
            bos.close();
        } catch (Exception e) {
            throw new CloningException(e.getMessage(), e);
        }

        return clone;
    }
}
