package net.corda.nodeapi.internal.serialization.amqp;

import net.corda.nodeapi.internal.serialization.AllWhitelist;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.NotSerializableException;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

public class JavaPrivatePropertyTests {
    static class C {
        private String a;

        C(final String a) {
            this.a = a; }
    }

    static class C2 {
        private String a;

        C2(final String a) {
            this.a = a; }

        public String getA() {
            return a; }
    }

    static class B {
        private Boolean b;

        B(final Boolean b) {
            this.b = b; }

        public Boolean isB() {
            return this.b;
        }
    }

    static class B2 {
        private Boolean b;

        public Boolean isB() {
            return this.b;
        }

        public void setB(final Boolean b) {
            this.b = b;
        }
    }

    static class B3 {
        private Boolean b;

        // break the BEAN format explicitly (i.e. it's not isB)
        public Boolean isb() {
            return this.b;
        }

        public void setB(final Boolean b) {
            this.b = b;
        }
    }

    static class C3 {
        private Integer a;

        public Integer getA() {
            return this.a;
        }

        public Boolean isA() {
            return this.a > 0;
        }

        public void setA(final Integer a) {
            this.a = a;
        }
    }

    @Test
    public void singlePrivateBooleanWithConstructor() throws NotSerializableException, NoSuchFieldException, IllegalAccessException {
        SerializerFactory factory = new SerializerFactory(AllWhitelist.INSTANCE, ClassLoader.getSystemClassLoader(),
                new EvolutionSerializerGetter(),
                new SerializerFingerPrinter());
        SerializationOutput ser = new SerializationOutput(factory);
        DeserializationInput des = new DeserializationInput(factory);

        B b = new B(true);
        B b2 = des.deserialize(ser.serialize(b), B.class);
        assertEquals(b.b, b2.b);
    }

    @Test
    public void singlePrivateBooleanWithNoConstructor() throws NotSerializableException, NoSuchFieldException, IllegalAccessException {
        SerializerFactory factory = new SerializerFactory(AllWhitelist.INSTANCE, ClassLoader.getSystemClassLoader(),
                new EvolutionSerializerGetter(),
                new SerializerFingerPrinter());

        SerializationOutput ser = new SerializationOutput(factory);
        DeserializationInput des = new DeserializationInput(factory);

        B2 b = new B2();
        b.setB(false);
        B2 b2 = des.deserialize(ser.serialize(b), B2.class);
        assertEquals(b.b, b2.b);
    }

    @Test
    public void testCapitilsationOfIs() throws NotSerializableException, NoSuchFieldException, IllegalAccessException {
        SerializerFactory factory = new SerializerFactory(AllWhitelist.INSTANCE, ClassLoader.getSystemClassLoader(),
                new EvolutionSerializerGetter(),
                new SerializerFingerPrinter());
        SerializationOutput ser = new SerializationOutput(factory);
        DeserializationInput des = new DeserializationInput(factory);

        B3 b = new B3();
        b.setB(false);
        B3 b2 = des.deserialize(ser.serialize(b), B3.class);

        // since we can't find a getter for b (isb != isB) then we won't serialize that parameter
        assertEquals(null, b2.b);
    }

    @Test
    public void singlePrivateIntWithBoolean() throws NotSerializableException, NoSuchFieldException, IllegalAccessException {
        SerializerFactory factory = new SerializerFactory(AllWhitelist.INSTANCE, ClassLoader.getSystemClassLoader(),
                new EvolutionSerializerGetter(),
                new SerializerFingerPrinter());
        SerializationOutput ser = new SerializationOutput(factory);
        DeserializationInput des = new DeserializationInput(factory);

        C3 c = new C3();
        c.setA(12345);
        C3 c2 = des.deserialize(ser.serialize(c), C3.class);

        assertEquals(c.a, c2.a);
    }

    @Test
    public void singlePrivateWithConstructor() throws NotSerializableException, NoSuchFieldException, IllegalAccessException {
        SerializerFactory factory = new SerializerFactory(AllWhitelist.INSTANCE, ClassLoader.getSystemClassLoader(),
                new EvolutionSerializerGetter(),
                new SerializerFingerPrinter());

        SerializationOutput ser = new SerializationOutput(factory);
        DeserializationInput des = new DeserializationInput(factory);

        C c = new C("dripping taps");
        C c2 = des.deserialize(ser.serialize(c), C.class);

        assertEquals(c.a, c2.a);

        //
        // Now ensure we actually got a private property serializer
        //
        Field f = SerializerFactory.class.getDeclaredField("serializersByDescriptor");
        f.setAccessible(true);

        ConcurrentHashMap<Object, AMQPSerializer<Object>> serializersByDescriptor =
                (ConcurrentHashMap<Object, AMQPSerializer<Object>>) f.get(factory);

        assertEquals(1, serializersByDescriptor.size());
        ObjectSerializer cSerializer = ((ObjectSerializer) serializersByDescriptor.values().toArray()[0]);
        assertEquals(1, cSerializer.getPropertySerializers().getSerializationOrder().size());
        Object[] propertyReaders = cSerializer.getPropertySerializers().getSerializationOrder().toArray();
        assertTrue(((PropertyAccessor) propertyReaders[0]).getGetter().getPropertyReader() instanceof PrivatePropertyReader);
    }

    @Test
    public void singlePrivateWithConstructorAndGetter()
            throws NotSerializableException, NoSuchFieldException, IllegalAccessException {
        SerializerFactory factory = new SerializerFactory(AllWhitelist.INSTANCE,
                ClassLoader.getSystemClassLoader(),
                new EvolutionSerializerGetter(),
                new SerializerFingerPrinter());

        SerializationOutput ser = new SerializationOutput(factory);
        DeserializationInput des = new DeserializationInput(factory);

        C2 c = new C2("dripping taps");
        C2 c2 = des.deserialize(ser.serialize(c), C2.class);

        assertEquals(c.a, c2.a);

        //
        // Now ensure we actually got a private property serializer
        //
        Field f = SerializerFactory.class.getDeclaredField("serializersByDescriptor");
        f.setAccessible(true);
        ConcurrentHashMap<Object, AMQPSerializer<Object>> serializersByDescriptor =
                (ConcurrentHashMap<Object, AMQPSerializer<Object>>) f.get(factory);

        assertEquals(1, serializersByDescriptor.size());
        ObjectSerializer cSerializer = ((ObjectSerializer) serializersByDescriptor.values().toArray()[0]);
        assertEquals(1, cSerializer.getPropertySerializers().getSerializationOrder().size());
        Object[] propertyReaders = cSerializer.getPropertySerializers().getSerializationOrder().toArray();
        assertTrue(((PropertyAccessor) propertyReaders[0]).getGetter().getPropertyReader() instanceof PublicPropertyReader);
    }
}
