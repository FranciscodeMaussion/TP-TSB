package com.tsb.soporte;

import java.util.*;

public class OAHashtable<K, V> implements Map<K, V> {

    private static final float DEFAULT_LOAD_FACTOR = 0.5f;
    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    private final Map.Entry<K, V> tomb = new Entry<>();

    private Map.Entry<?, ?>[] internalTable;
    private int initialCapacity;
    private int count;
    private float loadFactor;
    private transient int modCount;
    private int capacity;

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

        this.initialCapacity = nextPrime(initialCapacity);
        capacity = this.initialCapacity;
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
        int index = originalIndex;
        int counter = 0;
        V old = null;

        do {
            if (entry == null) {
                fountId = index;
                break;
            } else if (tomb == entry) {
                fountId = index;
            } else if (value.equals(entry.getValue())) {
                fountId = index;
                old = entry.getValue();
                break;
            }

            counter++;
            index += (int) Math.pow(counter, 2);
            if (index >= internalTable.length) {
                index = 0;
            }
            entry = (Map.Entry<K, V>) this.internalTable[index];
        } while (index != originalIndex);

        if (needsRehashing()) {
            rehash();
        }
        if (fountId == -1) {
            System.out.println("tu hermana");
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
        int index = originalIndex;

        Map.Entry<K, V> entry = (Map.Entry<K, V>) internalTable[originalIndex];

        do {
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
        } while (index != originalIndex);
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
        int newLength = nextPrime((int) (oldLength * 1.5f));

        internalTable = new Map.Entry<?, ?>[newLength];
        for (int i = 0; i < oldLength; i++) {
            Map.Entry<K, V> entry = (Map.Entry<K, V>) oldTable[i];
            if (entry != null) {
                this.put(entry.getKey(), entry.getValue());
            }
        }
        capacity = newLength;
    }

    private boolean contains(Object value) {
        if (value == null) throw new NullPointerException("value cannot be null");

        for (Map.Entry<?, ?> item : internalTable) {
            if (item != null && value.equals(item.getValue())) {
                return true;
            }
        }
        return false;
    }

    private boolean needsRehashing() {
        return count >= internalTable.length * DEFAULT_LOAD_FACTOR;
    }

    //Inner Class Entry

    public int getCapacity() {
        return capacity;
    }

    //Inner class KeySet

    private boolean isPrime(int n) {
        // negativos no admitidos en este contexto...
        if (n < 0) return false;

        if (n == 1) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;

        int raiz = (int) Math.pow(n, 0.5);
        for (int div = 3; div <= raiz; div += 2) {
            if (n % div == 0) return false;
        }

        return true;
    }

    private int nextPrime(int n) {
        if (n % 2 == 0) n++;
        for (; !isPrime(n); n += 2) ;
        return n;
    }

    private class Entry<K, V> implements Map.Entry<K, V> {

        private K key;
        private V value;

        Entry() {

        }

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
        public boolean containsAll(Collection<?> c) {
            for (K key : (Collection<K>) c) {
                if (key != null && contains(key)) {
                    return true;
                }
            }
            return false;
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

            int traversed;
            int index;
            boolean next;
            int originalModCount;

            private KeySetIterator() {
                traversed = 0;
                index = -1;
                next = false;
                originalModCount = OAHashtable.this.modCount;
            }

            @Override
            public boolean hasNext() {
                return traversed < OAHashtable.this.count;
            }

            @Override
            public K next() {
                if (OAHashtable.this.modCount != originalModCount) {
                    throw new ConcurrentModificationException("Concurrent modification detected");
                }
                index++;
                Map.Entry<K, V> current = (Map.Entry<K, V>) internalTable[index];
                while (current == null || current == tomb) {
                    index++;
                    current = (Map.Entry<K, V>) internalTable[index];
                }
                traversed++;
                next = true;

                return current.getKey();
            }

            @Override
            public void remove() {
                if (OAHashtable.this.modCount != originalModCount) {
                    throw new ConcurrentModificationException("Concurrent modification detected");
                }
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                internalTable[index] = tomb;
                originalModCount++;
                modCount++;
                OAHashtable.this.count--;
                next = false;
            }
        }
    }

    private class ValueCollection extends AbstractCollection<V> {

        @Override
        public Iterator<V> iterator() {
            return new ValueCollectionIterator();
        }

        @Override
        public int size() {
            return OAHashtable.this.size();
        }

        private class ValueCollectionIterator implements Iterator<V> {

            int traversed;
            int index;
            boolean next;
            int originalModCount;

            private ValueCollectionIterator() {
                traversed = 0;
                index = -1;
                next = false;
                originalModCount = OAHashtable.this.modCount;
            }

            @Override
            public boolean hasNext() {
                return traversed < OAHashtable.this.count;
            }

            @Override
            public V next() {
                if (OAHashtable.this.modCount != originalModCount) {
                    throw new ConcurrentModificationException("Concurrent modification detected");
                }
                index++;
                Map.Entry<K, V> current = (Map.Entry<K, V>) internalTable[index];
                while (current == null || current == tomb) {
                    index++;
                    current = (Map.Entry<K, V>) internalTable[index];
                }
                traversed++;
                next = true;

                return current.getValue();
            }

            @Override
            public void remove() {
                if (OAHashtable.this.modCount != originalModCount) {
                    throw new ConcurrentModificationException("Concurrent modification detected");
                }
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                internalTable[index] = tomb;
                originalModCount++;
                modCount++;
                OAHashtable.this.count--;
                next = false;
            }
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {


        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new OAHashtable.EntrySet.EntrySetIterator();
        }

        @Override
        public int size() {
            return 0;
        }

        private class EntrySetIterator implements Iterator<Map.Entry<K, V>> {

            int traversed;
            int index;
            boolean next;
            int originalModCount;

            private EntrySetIterator() {
                traversed = 0;
                index = -1;
                next = false;
                originalModCount = OAHashtable.this.modCount;
            }

            @Override
            public boolean hasNext() {
                return traversed < OAHashtable.this.count;
            }

            @Override
            public Map.Entry<K, V> next() {
                if (OAHashtable.this.modCount != originalModCount) {
                    throw new ConcurrentModificationException("Concurrent modification detected");
                }
                index++;
                Map.Entry<K, V> current = (Map.Entry<K, V>) internalTable[index];
                while (current == null || current == tomb) {
                    index++;
                    current = (Map.Entry<K, V>) internalTable[index];
                }
                traversed++;
                next = true;

                return current;
            }

            @Override
            public void remove() {
                if (OAHashtable.this.modCount != originalModCount) {
                    throw new ConcurrentModificationException("Concurrent modification detected");
                }
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                internalTable[index] = tomb;
                originalModCount++;
                modCount++;
                OAHashtable.this.count--;
                next = false;
            }
        }
    }
}
