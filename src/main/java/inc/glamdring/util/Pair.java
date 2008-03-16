package inc.glamdring.util;

import java.util.*;

/**
 * A Hackish tuple class.  Acts as Map Entry as well as attempting some level
 * of jit trickery by state as obj[] as     external from-stack.
 */
public class Pair<K, V> implements Map.Entry<K, V> {
    public Object[] kv;

    public Pair(K k, V v) {
        kv = new Object[]{k, v};
    }

    public Pair(Object... two) {
        kv = two;
    }

    public K getFirst() {
        return (K) kv[0];
    }

    public V getSecond() {
        return (V) kv[1];
    }

    /**
     * Returns the key corresponding to this entry.
     *
     * @return the key corresponding to this entry
     * @throws IllegalStateException implementations may, but are not
     *                               required to, throw this exception if the entry has been
     *                               removed from the backing map.
     */
    public K getKey() {
        return (K) kv[0];
    }

    /**
     * Returns the value corresponding to this entry.  If the mapping
     * has been removed from the backing map (by the iterator's
     * <tt>remove</tt> operation), the results of this call are undefined.
     *
     * @return the value corresponding to this entry
     * @throws IllegalStateException implementations may, but are not
     *                               required to, throw this exception if the entry has been
     *                               removed from the backing map.
     */
    public V getValue() {
        return (V) kv[1];  //To change body of implemented methods use File | Settings | File Templates.
    }


    public Object setValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public int hashCode() {
        return getKey().hashCode() << 16 + getValue().hashCode() & 0xffff;

    }

}
