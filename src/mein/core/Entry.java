package mein.core;

public final class Entry<K,V>
{
    public final K key;
    public final V value;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K,V> Entry<K,V> of(K key, V value) {
        return new Entry<K,V>(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof Entry) {
            Entry that = (Entry) o;
            Object k1 = this.key;
            Object k2 = that.key;
            if (k1 == null ? k2 == null : k1.equals(k2)) {
                Object v1 = this.value;
                Object v2 = that.value;
                return v1 == null ? v2 == null : v1.equals(v2);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        Object k = this.key;
        int kHash = k == null ? 0 : k.hashCode();
        
        Object v = this.value;
        int vHash = v == null ? 0 : v.hashCode();
        
        return kHash ^ vHash;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
