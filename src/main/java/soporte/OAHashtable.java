package soporte;

import java.util.*;

public class OAHashtable<K, V> implements Map<K, V> {

    private static final float DEFAULT_LOAD_FACTOR = 0.5f;
    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    private final Map.Entry<K, V> tomb = new Entry<>(null, null);

    private Map.Entry<?, ?>[] internalTable;
    private int initialCapacity;
    private int count;
    private float loadFactor;
    private transient int modCount;

    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K, V>> entrySet = null;
    private transient Collection<V> values = null;

    public OAHashtable() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    private OAHashtable(int initialCapacity) {
        if (initialCapacity <= 0) {
            initialCapacity = 11;
        }

        this.internalTable = new Map.Entry<?, ?>[initialCapacity];

        this.initialCapacity = initialCapacity;
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.count = 0;
        modCount = 0;
    }

    public OAHashtable(Map<? extends K, ? extends V> t) {
        this(DEFAULT_INITIAL_CAPACITY);
        this.putAll(t);
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        return contains(value);
    }

    @Override
    public V get(Object key) {
        if (key == null) throw new NullPointerException("key cannot be null");

        int index = findIndexOf(key);

        if (index != -1) {
            return (V) internalTable[index].getValue();
        } else {
            return null;
        }
    }

    @Override
    public V put(K key, V value) {
        if (key == null) throw new NullPointerException("key cannot be null");
        if (value == null) throw new NullPointerException("value cannot be null");

        int originalIndex = convertToIndex(key);
        Map.Entry<K, V> entry = (Map.Entry<K, V>) internalTable[originalIndex];
        int fountId = -1;
        int index = originalIndex + 1;
        V old = null;

        while (index != originalIndex) {

            if ((entry == null) || (tomb == entry)) {
                fountId = index;
            }

            if (entry != null && value.equals(entry.getValue())) {
                fountId = index;
                old = entry.getValue();
                break;
            }

            if (entry == null) {
                break;
            }
            index++;
            if (index == internalTable.length) {
                index = 0;
            }
            entry = (Map.Entry<K, V>) this.internalTable[index];
        }

        if (needsRehashing()) {
            rehash();
        }
        Map.Entry<K, V> newEntry = new Entry<>(key, value);
        internalTable[fountId] = newEntry;
        count++;
        this.modCount++;

        return old;
    }

    @Override
    public V remove(Object key) {
        if (key == null) throw new NullPointerException("key cannot be null");

        int index = findIndexOf(key);
        if (index != -1) {
            V old = (V) internalTable[index].getValue();
            internalTable[index] = tomb;
            this.count--;
            this.modCount++;
            return old;
        } else {
            return null;
        }
    }

    private int findIndexOf(Object key) {
        assert (key != null);
        int trueHash = key.hashCode();
        int originalIndex = convertToIndex(key.hashCode());
        int index = originalIndex + 1;

        Map.Entry<K, V> entry = (Map.Entry<K, V>) internalTable[originalIndex];

        while (index != originalIndex) {
            if (entry == null) {
                return -1;
            } else if (entry.getKey() != null && trueHash == entry.getKey().hashCode()) {
                return index;
            }

            index++;
            if (index == internalTable.length) {
                index = 0;
            }
            entry = (Map.Entry<K, V>) internalTable[index];
        }
        return -1;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        this.count = 0;
        this.modCount++;
        for (int i = 0; i < internalTable.length; i++) {
            internalTable[i] = null;
        }
    }

    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new KeySet();
        }
        return keySet;
    }

    @Override
    public Collection<V> values() {
        if (values == null) {
            values = new ValueCollection();
        }
        return values;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet();
        }
        return entrySet;
    }

    private int convertToIndex(int k) {
        return convertToIndex(k, this.internalTable.length);
    }

    private int convertToIndex(K key) {
        return convertToIndex(key.hashCode(), internalTable.length);
    }

    private int convertToIndex(K key, int t) {
        return convertToIndex(key.hashCode(), t);
    }

    private int convertToIndex(int k, int t) {
        if (k < 0) k *= -1;
        return k % t;
    }

    private void rehash() {
        this.count = 0;
        this.modCount++;
        int oldLength = internalTable.length;
        Map.Entry<?, ?>[] oldTable = internalTable;
        int newLength = oldLength * 2 + 1;
        internalTable = new Map.Entry<?, ?>[newLength];
        for (int i = 0; i < oldLength; i++) {
            Map.Entry<K, V> entry = (Map.Entry<K, V>) oldTable[i];
            this.put(entry.getKey(), entry.getValue());
        }
    }

    private boolean contains(Object value) {
        if (value == null) throw new NullPointerException("value cannot be null");
        for (int i = 0; i < internalTable.length; i++) {
            return value.equals(internalTable[i].getValue());
        }
        return false;
    }

    private boolean needsRehashing() {
        return count >= internalTable.length * DEFAULT_LOAD_FACTOR;
    }

    //Inner Class Entry

    private class Entry<K, V> implements Map.Entry<K, V> {

        private K key;
        private V value;

        Entry(K key, V value) {
            if (key == null) throw new IllegalArgumentException("key cannot be null");
            if (value == null) throw new IllegalArgumentException("value cannot be null");
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            if (value == null) throw new IllegalArgumentException("value cannot be null");

            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public int hashCode() {
            return Objects.hash(getKey(), getValue());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Entry)) return false;
            Entry<?, ?> entry = (Entry<?, ?>) o;
            return Objects.equals(getKey(), entry.getKey()) &&
                    Objects.equals(getValue(), entry.getValue());
        }

        @Override
        public String toString() {
            return "(" + key.toString() + ", " + value.toString() + ")";
        }
    }

    //Inner class KeySet

    private class KeySet extends AbstractSet<K> {

        @Override
        public Iterator<K> iterator() {
            return new OAHashtable.KeySet.KeySetIterator();
        }

        @Override
        public int size() {
            return OAHashtable.this.count;
        }

        @Override
        public boolean contains(Object o) {
            return OAHashtable.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return (OAHashtable.this.remove(o) != null);
        }

        @Override
        public void clear() {
            OAHashtable.this.clear();
        }

        private class KeySetIterator implements Iterator<K> {

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public K next() {
                return null;
            }

            @Override
            public void remove() {

            }
        }
    }

    private class ValueCollection implements Collection<V> {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<V> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(V v) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends V> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }
    }

    private class EntrySet implements Set<Map.Entry<K, V>> {
        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return null;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return null;
        }

        @Override
        public boolean add(Map.Entry<K, V> kvEntry) {
            return false;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Map.Entry<K, V>> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {

        }
    }
}
