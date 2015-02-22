# Overview
Object cloning library. Supports extensible shallow and deep object cloning strategies.

## Usage
Below is an example on how to use the library

<pre><code> // Example
 TestObject clone = new ObjectCloner<TestObject>().deepClone(originalTestObject);
</code></pre>

See [ObjectClonerTest](https://github.com/kamranzafar/object-cloner/blob/master/src/test/java/org/kamranzafar/commons/cloner/ObjectClonerTest.java) junit test case for more examples.

## Maven
Add the following dependency to the maven project

<pre><code> &lt;dependency&gt;
  &lt;groupId&gt;org.kamranzafar.commons&lt;/groupId&gt;
  &lt;artifactId&gt;object-cloner&lt;/artifactId&gt;
  &lt;version&gt;0.1&lt;/version&gt;
 &lt;/dependency&gt;
</code></pre>