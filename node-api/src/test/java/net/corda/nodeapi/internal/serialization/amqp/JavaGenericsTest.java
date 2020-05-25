package net.corda.nodeapi.internal.serialization.amqp;

import net.corda.core.serialization.SerializedBytes;
import net.corda.nodeapi.internal.serialization.AllWhitelist;
import org.junit.Test;
import java.io.NotSerializableException;

import static org.jgroups.util.Util.assertEquals;

public class JavaGenericsTest {
    private static class Inner {
        private final Integer v;

        private Inner(final Integer v) {
            this.v = v; }
        public Integer getV() {
            return v; }
    }

    private static class A<T> {
        private final T t;

        private A(final T t) {
            this.t = t; }
        public T getT() {
            return t; }
    }

    @Test
    public void basicGeneric() throws NotSerializableException {
        A a1 = new A(1);

        SerializerFactory factory = new SerializerFactory(
                AllWhitelist.INSTANCE,
                ClassLoader.getSystemClassLoader(),
                new EvolutionSerializerGetter(),
                new SerializerFingerPrinter());

        SerializationOutput ser = new SerializationOutput(factory);
        SerializedBytes<?> bytes = ser.serialize(a1);

        DeserializationInput des = new DeserializationInput(factory);
        A a2 = des.deserialize(bytes, A.class);

        assertEquals(1, a2.getT());
    }

    private SerializedBytes<?> forceWildcardSerialize(final A<?> a) throws NotSerializableException {
        SerializerFactory factory = new SerializerFactory(
                AllWhitelist.INSTANCE,
                ClassLoader.getSystemClassLoader(),
                new EvolutionSerializerGetter(),
                new SerializerFingerPrinter());

       return (new SerializationOutput(factory)).serialize(a);
    }

    private SerializedBytes<?> forceWildcardSerializeFactory(
            final A<?> a,
            final SerializerFactory factory) throws NotSerializableException {
        return (new SerializationOutput(factory)).serialize(a);
    }

    private A<?> forceWildcardDeserialize(final SerializedBytes<?> bytes) throws NotSerializableException {
        SerializerFactory factory = new SerializerFactory(
                AllWhitelist.INSTANCE,
                ClassLoader.getSystemClassLoader(),
                new EvolutionSerializerGetter(),
                new SerializerFingerPrinter());

        DeserializationInput des = new DeserializationInput(factory);
        return des.deserialize(bytes, A.class);
    }

    private A<?> forceWildcardDeserializeFactory(
            final SerializedBytes<?> bytes,
            final SerializerFactory factory) throws NotSerializableException {
        return (new DeserializationInput(factory)).deserialize(bytes, A.class);
    }

    @Test
    public void forceWildcard() throws NotSerializableException {
        SerializedBytes<?> bytes = forceWildcardSerialize(new A(new Inner(29)));
        Inner i = (Inner) forceWildcardDeserialize(bytes).getT();
        assertEquals(29, i.getV());
    }

    @Test
    public void forceWildcardSharedFactory() throws NotSerializableException {
        SerializerFactory factory = new SerializerFactory(
                AllWhitelist.INSTANCE,
                ClassLoader.getSystemClassLoader(),
                new EvolutionSerializerGetter(),
                new SerializerFingerPrinter());

        SerializedBytes<?> bytes = forceWildcardSerializeFactory(new A(new Inner(29)), factory);
        Inner i = (Inner) forceWildcardDeserializeFactory(bytes, factory).getT();

        assertEquals(29, i.getV());
    }
}
