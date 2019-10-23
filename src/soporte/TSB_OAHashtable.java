package soporte;

import java.io.Serializable;
import java.util.*;

/**
 * Clase para emular la funcionalidad de la clase java.util.Hashtable provista
 * en forma nativa por Java. Una TSB_OAHashtable usa un arreglo de listas de la
 * clase TSBArrayList a modo de buckets (o listas de desborde) para resolver las
 * colisiones que pudieran presentarse.
 * <p>
 * Se almacenan en la tabla pares de objetos (key, value), en donde el objeto
 * key actúa como clave para identificar al objeto value. La tabla no admite
 * repetición de claves (no se almacenarán dos pares de objetos con la misma
 * clave). Tampoco acepta referencias nulas (tanto para las key como para los
 * values): no será insertado un par (key, value) si alguno de ambos objetos es
 * null.
 * <p>
 * Se ha emulado tanto como ha sido posible el comportamiento de la clase ya
 * indicada java.util.Hashtable. En esa clase, el parámetro loadFactor se usa
 * para determinar qué tan llena está la tabla antes de lanzar un proceso de
 * rehash: si loadFactor es 0.75, entonces se hará un rehash cuando la cantidad
 * de casillas ocupadas en el arreglo de soporte sea un 75% del tamaño de ese
 * arreglo. En nuestra clase TSB_OAHashtable, mantuvimos el concepto de loadFactor
 * (ahora llamado loadFactor) pero con una interpretación distinta: en nuestro
 * modelo, se lanza un rehash si la cantidad promedio de valores por lista es
 * mayor a cierto número constante y pequeño, que asociamos al loadFactor para
 * mantener el espíritu de la implementación nativa. En nuestro caso, si el
 * valor loadFactor es 0.8 entonces se lanzará un rehash si la cantidad
 * promedio de valores por lista es mayor a 0.8 * 10 = 8 elementos por lista.
 *
 * @param <K> el tipo de los objetos que serán usados como clave en la tabla.
 * @param <V> el tipo de los objetos que serán los valores de la tabla.
 * @author Ing. Valerio Frittelli.
 * @version Septiembre de 2017.
 */
public class TSB_OAHashtable<K, V> implements Map<K, V>, Cloneable, Serializable {
    //************************ Constantes (privadas o públicas).    

    // el tamaño máximo que podrá tener el arreglo de soprte...
    private final static int MAX_SIZE = Integer.MAX_VALUE;


    //************************ Atributos privados (estructurales).

    // la tabla hash: el arreglo que contiene las listas de desborde...
    private Map.Entry<?, ?>[] internalTable;

    // el tamaño inicial de la tabla (tamaño con el que fue creada)...
    private int initialCapacity;

    // la cantidad de objetos que contiene la tabla en TODAS sus listas...
    private int count;

    // el factor de carga para calcular si hace falta un rehashing...
    private float loadFactor;


    //************************ Atributos privados (para gestionar las vistas).

    /*
     * (Tal cual están definidos en la clase java.util.Hashtable)
     * Cada uno de estos campos se inicializa para contener una instancia de la
     * vista que sea más apropiada, la primera vez que esa vista es requerida.
     * La vista son objetos stateless (no se requiere que almacenen datos, sino
     * que sólo soportan operaciones), y por lo tanto no es necesario crear más
     * de una de cada una.
     */
    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K, V>> entrySet = null;
    private transient Collection<V> values = null;


    //************************ Atributos protegidos (control de iteración).

    // conteo de operaciones de cambio de tamaño (fail-fast iterator).
    private transient int modCount;


    //************************ Constructores.

    /**
     * Crea una tabla vacía, con la capacidad inicial igual a 11 y con factor
     * de carga igual a 0.8f.
     */
    public TSB_OAHashtable() {
        this(5, 0.8f);
    }

    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con factor
     * de carga igual a 0.8f.
     *
     * @param initial_capacity la capacidad inicial de la tabla.
     */
    public TSB_OAHashtable(int initial_capacity) {
        this(initial_capacity, 0.8f);
    }

    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con el factor
     * de carga indicado. Si la capacidad inicial indicada por initialCapacity
     * es menor o igual a 0, la tabla será creada de tamaño 11. Si el factor de
     * carga indicado es negativo o cero, se ajustará a 0.8f.
     *
     * @param initialCapacity la capacidad inicial de la tabla.
     * @param loadFactor      el factor de carga de la tabla.
     */
    private TSB_OAHashtable(int initialCapacity, float loadFactor) {
        if (loadFactor <= 0) {
            loadFactor = 0.8f;
        }
        if (initialCapacity <= 0) {
            initialCapacity = 11;
        }

        this.internalTable = new Map.Entry<?, ?>[initialCapacity];

        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.count = 0;
        this.modCount = 0;
    }

    /**
     * Crea una tabla a partir del contenido del Map especificado.
     *
     * @param t el Map a partir del cual se creará la tabla.
     */
    public TSB_OAHashtable(Map<? extends K, ? extends V> t) {
        this(11, 0.8f);
        this.putAll(t);
    }


    //************************ Implementación de métodos especificados por Map.

    /**
     * Retorna la cantidad de elementos contenidos en la tabla.
     *
     * @return la cantidad de elementos de la tabla.
     */
    @Override
    public int size() {
        return this.count;
    }

    /**
     * Determina si la tabla está vacía (no contiene ningún elemento).
     *
     * @return true si la tabla está vacía.
     */
    @Override
    public boolean isEmpty() {
        return (this.count == 0);
    }

    /**
     * Determina si la clave key está en la tabla.
     *
     * @param key la clave a verificar.
     * @return true si la clave está en la tabla.
     * @throws NullPointerException si la clave es null.
     */
    @Override
    public boolean containsKey(Object key) {
        return (this.get(key) != null);
    }

    /**
     * Determina si alguna clave de la tabla está asociada al objeto value que
     * entra como parámetro. Equivale a contains().
     *
     * @param value el objeto a buscar en la tabla.
     * @return true si alguna clave está asociada efectivamente a ese value.
     */
    @Override
    public boolean containsValue(Object value) {
        return this.contains(value);
    }

    /**
     * Retorna el objeto al cual está asociada la clave key en la tabla, o null
     * si la tabla no contiene ningún objeto asociado a esa clave.
     *
     * @param key la clave que será buscada en la tabla.
     * @return el objeto asociado a la clave especificada (si existe la clave) o
     * null (si no existe la clave en esta tabla).
     * @throws NullPointerException si key es null.
     * @throws ClassCastException   si la clase de key no es compatible con la
     *                              tabla.
     */
    @Override
    public V get(Object key) {
        if (key == null) throw new NullPointerException("get(): parámetro null");

        int ib = this.hashIt(key.hashCode());
        Map.Entry<K, V> entry = (Map.Entry<K, V>) this.internalTable[ib];

        return (entry != null) ? entry.getValue() : null;
    }

    /**
     * Asocia el valor (value) especificado, con la clave (key) especificada en
     * esta tabla. Si la tabla contenía previamente un valor asociado para la
     * clave, entonces el valor anterior es reemplazado por el nuevo (y en este
     * caso el tamaño de la tabla no cambia).
     *
     * @param key   la clave del objeto que se quiere agregar a la tabla.
     * @param value el objeto que se quiere agregar a la tabla.
     * @return el objeto anteriormente asociado a la clave si la clave ya
     * estaba asociada con alguno, o null si la clave no estaba antes
     * asociada a ningún objeto.
     * @throws NullPointerException si key es null o value es null.
     */
    @Override
    public V put(K key, V value) {
        if (key == null || value == null) throw new NullPointerException("put(): parámetro null");

        int ib = this.hashIt(key);
        Map.Entry<K, V> entry = (Map.Entry<K, V>) this.internalTable[ib];
        int fountId = -1;
        int index = ib + 1;
        V old = null;

        while (index != ib) {
            if (index == internalTable.length) {
                index = 0;
            }

            if ((entry == null) || (entry.getValue() instanceof Tomb)) {
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
            entry = (Map.Entry<K, V>) this.internalTable[index];
        }

        if (this.averageLength() >= this.loadFactor * 10) {
            this.rehash();
        }
        Map.Entry<K, V> newEntry = new Entry<>(key, value);
        this.internalTable[fountId] = newEntry;
        this.count++;
        this.modCount++;

        return old;
    }

    /**
     * Elimina de la tabla la clave key (y su correspondiente valor asociado).
     * El método no hace nada si la clave no está en la tabla.
     *
     * @param key la clave a eliminar.
     * @return El objeto al cual la clave estaba asociada, o null si la clave no
     * estaba en la tabla.
     * @throws NullPointerException - if the key is null.
     */
    @Override
    public V remove(Object key) {
        if (key == null) throw new NullPointerException("remove(): parámetro null");

        int ib = this.hashIt(key.hashCode());
        TSBArrayList<Map.Entry<K, V>> bucket = this.internalTable[ib];

        int ik = this.searchForIndex((K) key, bucket);

        V old = null;
        if (ik != -1) {
            old = bucket.remove(ik).getValue();
            this.count--;
            this.modCount++;
        }

        return old;
    }

    /**
     * Copia en esta tabla, todos los objetos contenidos en el map especificado.
     * Los nuevos objetos reemplazarán a los que ya existan en la tabla
     * asociados a las mismas claves (si se repitiese alguna).
     *
     * @param m el map cuyos objetos serán copiados en esta tabla.
     * @throws NullPointerException si m es null.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Elimina todo el contenido de la tabla, de forma de dejarla vacía. En esta
     * implementación además, el arreglo de soporte vuelve a tener el tamaño que
     * inicialmente tuvo al ser creado el objeto.
     */
    @Override
    public void clear() {
        this.internalTable = new TSBArrayList[this.initialCapacity];
        for (int i = 0; i < this.internalTable.length; i++) {
            this.internalTable[i] = new TSBArrayList<>();
        }
        this.count = 0;
        this.modCount++;
    }

    /**
     * Retorna un Set (conjunto) a modo de vista de todas las claves (key)
     * contenidas en la tabla. El conjunto está respaldado por la tabla, por lo
     * que los cambios realizados en la tabla serán reflejados en el conjunto, y
     * viceversa. Si la tabla es modificada mientras un iterador está actuando
     * sobre el conjunto vista, el resultado de la iteración será indefinido
     * (salvo que la modificación sea realizada por la operación remove() propia
     * del iterador, o por la operación setValue() realizada sobre una entrada
     * de la tabla que haya sido retornada por el iterador). El conjunto vista
     * provee métodos para eliminar elementos, y esos métodos a su vez
     * eliminan el correspondiente par (key, value) de la tabla (a través de las
     * operaciones Iterator.remove(), Set.remove(), removeAll(), retainAll()
     * y clear()). El conjunto vista no soporta las operaciones add() y
     * addAll() (si se las invoca, se lanzará una UnsuportedOperationException).
     *
     * @return un conjunto (un Set) a modo de vista de todas las claves
     * mapeadas en la tabla.
     */
    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new KeySet();
        }
        return keySet;
    }

    /**
     * Retorna una Collection (colección) a modo de vista de todos los valores
     * (values) contenidos en la tabla. La colección está respaldada por la
     * tabla, por lo que los cambios realizados en la tabla serán reflejados en
     * la colección, y viceversa. Si la tabla es modificada mientras un iterador
     * está actuando sobre la colección vista, el resultado de la iteración será
     * indefinido (salvo que la modificación sea realizada por la operación
     * remove() propia del iterador, o por la operación setValue() realizada
     * sobre una entrada de la tabla que haya sido retornada por el iterador).
     * La colección vista provee métodos para eliminar elementos, y esos métodos
     * a su vez eliminan el correspondiente par (key, value) de la tabla (a
     * través de las operaciones Iterator.remove(), Collection.remove(),
     * removeAll(), removeAll(), retainAll() y clear()). La colección vista no
     * soporta las operaciones add() y addAll() (si se las invoca, se lanzará
     * una UnsuportedOperationException).
     *
     * @return una colección (un Collection) a modo de vista de todas los
     * valores mapeados en la tabla.
     */
    @Override
    public Collection<V> values() {
        if (values == null) {
            values = new ValueCollection();
        }
        return values;
    }

    /**
     * Retorna un Set (conjunto) a modo de vista de todos los pares (key, value)
     * contenidos en la tabla. El conjunto está respaldado por la tabla, por lo
     * que los cambios realizados en la tabla serán reflejados en el conjunto, y
     * viceversa. Si la tabla es modificada mientras un iterador está actuando
     * sobre el conjunto vista, el resultado de la iteración será indefinido
     * (salvo que la modificación sea realizada por la operación remove() propia
     * del iterador, o por la operación setValue() realizada sobre una entrada
     * de la tabla que haya sido retornada por el iterador). El conjunto vista
     * provee métodos para eliminar elementos, y esos métodos a su vez
     * eliminan el correspondiente par (key, value) de la tabla (a través de las
     * operaciones Iterator.remove(), Set.remove(), removeAll(), retainAll()
     * and clear()). El conjunto vista no soporta las operaciones add() y
     * addAll() (si se las invoca, se lanzará una UnsuportedOperationException).
     *
     * @return un conjunto (un Set) a modo de vista de todos los objetos
     * mapeados en la tabla.
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet();
        }
        return entrySet;
    }


    //************************ Redefinición de métodos heredados desde Object.

    /**
     * Retorna una copia superficial de la tabla. Las listas de desborde o
     * buckets que conforman la tabla se clonan ellas mismas, pero no se clonan
     * los objetos que esas listas contienen: en cada bucket de la tabla se
     * almacenan las direcciones de los mismos objetos que contiene la original.
     *
     * @return una copia superficial de la tabla.
     * @throws java.lang.CloneNotSupportedException si la clase no implementa la
     *                                              interface Cloneable.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        TSB_OAHashtable<K, V> t = (TSB_OAHashtable<K, V>) super.clone();
        t.internalTable = new TSBArrayList[internalTable.length];
        for (int i = internalTable.length; i-- > 0; ) {
            t.internalTable[i] = (TSBArrayList<Map.Entry<K, V>>) internalTable[i].clone();
        }
        t.keySet = null;
        t.entrySet = null;
        t.values = null;
        t.modCount = 0;
        return t;
    }

    /**
     * Determina si esta tabla es igual al objeto espeficicado.
     *
     * @param obj el objeto a comparar con esta tabla.
     * @return true si los objetos son iguales.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Map)) {
            return false;
        }

        Map<K, V> t = (Map<K, V>) obj;
        if (t.size() != this.size()) {
            return false;
        }

        try {
            for (Map.Entry<K, V> e : this.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                if (t.get(key) == null) {
                    return false;
                } else {
                    if (!value.equals(t.get(key))) {
                        return false;
                    }
                }
            }
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }

        return true;
    }

    /**
     * Retorna un hash code para la tabla completa.
     *
     * @return un hash code para la tabla.
     */
    @Override
    public int hashCode() {
        if (this.isEmpty()) {
            return 0;
        }

        int hc = 0;
        for (Map.Entry<K, V> entry : this.entrySet()) {
            hc += entry.hashCode();
        }

        return hc;
    }

    /**
     * Devuelve el contenido de la tabla en forma de String. Sólo por razones de
     * didáctica, se hace referencia explícita en esa cadena al contenido de
     * cada una de las listas de desborde o buckets de la tabla.
     *
     * @return una cadena con el contenido completo de la tabla.
     */
    @Override
    public String toString() {
        StringBuilder cad = new StringBuilder("");
        for (int i = 0; i < this.internalTable.length; i++) {
            cad.append("\nLista ").append(i).append(":\n\t").append(this.internalTable[i].toString());
        }
        return cad.toString();
    }


    //************************ Métodos específicos de la clase.

    /**
     * Determina si alguna clave de la tabla está asociada al objeto value que
     * entra como parámetro. Equivale a containsValue().
     *
     * @param value el objeto a buscar en la tabla.
     * @return true si alguna clave está asociada efectivamente a ese value.
     */
    private boolean contains(Object value) {
        if (value == null) return false;

        for (TSBArrayList<Map.Entry<K, V>> bucket : this.internalTable) {
            for (Map.Entry<K, V> entry : bucket) {
                if (value.equals(entry.getValue())) return true;
            }
        }
        return false;
    }

    /**
     * Incrementa el tamaño de la tabla y reorganiza su contenido. Se invoca
     * automaticamente cuando se detecta que la cantidad promedio de nodos por
     * lista supera a cierto el valor critico dado por (10 * loadFactor). Si el
     * valor de loadFactor es 0.8, esto implica que el límite antes de invocar
     * rehash es de 8 nodos por lista en promedio, aunque seria aceptable hasta
     * unos 10 nodos por lista.
     */
    private void rehash() {
        int oldLength = this.internalTable.length;

        // nuevo tamaño: doble del anterior, más uno para llevarlo a impar...
        int newLength = oldLength * 2 + 1;

        // crear el nuevo arreglo con newLength listas vacías...
        TSBArrayList<Map.Entry<K, V>>[] temp = new TSBArrayList[newLength];
        for (int j = 0; j < temp.length; j++) {
            temp[j] = new TSBArrayList<>();
        }

        // notificación fail-fast iterator... la tabla cambió su estructura...
        this.modCount++;

        // recorrer el viejo arreglo y redistribuir los objetos que tenia...
        for (TSBArrayList<Map.Entry<K, V>> entries : this.internalTable) {
            // entrar en la lista numero i, y recorrerla con su iterador...
            for (Map.Entry<K, V> x : entries) {
                // obtener un objeto de la vieja lista...
                // obtener su nuevo valor de dispersión para el nuevo arreglo...
                K key = x.getKey();
                int y = this.hashIt(key, temp.length);

                // insertarlo en el nuevo arreglo, en la lista numero "y"...
                temp[y].add(x);
            }
        }

        // cambiar la referencia internalTable para que apunte a temp...
        this.internalTable = temp;
    }


    //************************ Métodos privados.

    /*
     * Función hash. Toma una clave entera k y calcula y retorna un índice
     * válido para esa clave para entrar en la tabla.
     */
    private int hashIt(int k) {
        return hashIt(k, this.internalTable.length);
    }

    /*
     * Función hash. Toma un objeto key que representa una clave y calcula y
     * retorna un índice válido para esa clave para entrar en la tabla.
     */
    private int hashIt(K key) {
        return hashIt(key.hashCode(), this.internalTable.length);
    }

    /*
     * Función hash. Toma un objeto key que representa una clave y un tamaño de
     * tabla t, y calcula y retorna un índice válido para esa clave dedo ese
     * tamaño.
     */
    private int hashIt(K key, int t) {
        return hashIt(key.hashCode(), t);
    }

    /*
     * Función hash. Toma una clave entera k y un tamaño de tabla t, y calcula y
     * retorna un índice válido para esa clave dado ese tamaño.
     */
    private int hashIt(int k, int t) {
        if (k < 0) k *= -1;
        return k % t;
    }

    /**
     * Calcula la longitud promedio de las listas de la tabla.
     *
     * @return la longitud promedio de la listas contenidas en la tabla.
     */
    private int averageLength() {
        return this.count / this.internalTable.length;
    }

    /*
     * Busca en la lista bucket un objeto Entry cuya clave coincida con key.
     * Si lo encuentra, retorna ese objeto Entry. Si no lo encuentra, retorna
     * null.
     */
    private Map.Entry<K, V> searchForEntry(K key, TSBArrayList<Map.Entry<K, V>> bucket) {
        for (Map.Entry<K, V> entry : bucket) {
            if (key.equals(entry.getKey())) return entry;
        }
        return null;
    }

    /*
     * Busca en la lista bucket un objeto Entry cuya clave coincida con key.
     * Si lo encuentra, retorna su posicíón. Si no lo encuentra, retorna -1.
     */
    private int searchForIndex(K key, TSBArrayList<Map.Entry<K, V>> bucket) {
        Iterator<Map.Entry<K, V>> it = bucket.iterator();
        for (int i = 0; it.hasNext(); i++) {
            Map.Entry<K, V> entry = it.next();
            if (key.equals(entry.getKey())) return i;
        }
        return -1;
    }


    //************************ Clases Internas.

    private class Tomb<K, V> implements Map.Entry<K, V> {

        @Override
        public K getKey() {
            return null;
        }

        @Override
        public V getValue() {
            return null;
        }

        @Override
        public V setValue(V value) {
            return null;
        }
    }

    /*
     * Clase interna que representa los pares de objetos que se almacenan en la
     * tabla hash: son instancias de esta clase las que realmente se guardan en
     * en cada una de las listas del arreglo internalTable que se usa como soporte de
     * la tabla. Lanzará una IllegalArgumentException si alguno de los dos
     * parámetros es null.
     */
    private class Entry<K, V> implements Map.Entry<K, V> {
        private K key;
        private V value;

        Entry(K key, V value) {
            if (key == null || value == null) {
                throw new IllegalArgumentException("Entry(): parámetro null...");
            }
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
            if (value == null) {
                throw new IllegalArgumentException("setValue(): parámetro null...");
            }

            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 61 * hash + Objects.hashCode(this.key);
            hash = 61 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }

            final Entry other = (Entry) obj;
            if (!Objects.equals(this.key, other.key)) {
                return false;
            }
            return Objects.equals(this.value, other.value);
        }

        @Override
        public String toString() {
            return "(" + key.toString() + ", " + value.toString() + ")";
        }
    }

    /*
     * Clase interna que representa una vista de todas los Claves mapeadas en la
     * tabla: si la vista cambia, cambia también la tabla que le da respaldo, y
     * viceversa. La vista es stateless: no mantiene estado alguno (es decir, no
     * contiene datos ella misma, sino que accede y gestiona directamente datos
     * de otra fuente), por lo que no tiene atributos y sus métodos gestionan en
     * forma directa el contenido de la tabla. Están soportados los metodos para
     * eliminar un objeto (remove()), eliminar todo el contenido (clear) y la
     * creación de un Iterator (que incluye el método Iterator.remove()).
     */
    private class KeySet extends AbstractSet<K> {
        @Override
        public Iterator<K> iterator() {
            return new KeySetIterator();
        }

        @Override
        public int size() {
            return TSB_OAHashtable.this.count;
        }

        @Override
        public boolean contains(Object o) {
            return TSB_OAHashtable.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return (TSB_OAHashtable.this.remove(o) != null);
        }

        @Override
        public void clear() {
            TSB_OAHashtable.this.clear();
        }

        private class KeySetIterator implements Iterator<K> {
            // índice de la lista actualmente recorrida...
            private int current_bucket;

            // índice de la lista anterior (si se requiere en remove())...
            private int last_bucket;

            // índice del elemento actual en el iterador (el que fue retornado 
            // la última vez por next() y será eliminado por remove())...
            private int current_entry;

            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;

            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;

            /*
             * Crea un iterador comenzando en la primera lista. Activa el
             * mecanismo fail-fast.
             */
            KeySetIterator() {
                current_bucket = 0;
                last_bucket = 0;
                current_entry = -1;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya
             * sido retornado por next().
             */
            @Override
            public boolean hasNext() {
                // variable auxiliar t para simplificar accesos...
                TSBArrayList<Map.Entry<K, V>> t[] = TSB_OAHashtable.this.internalTable;

                if (TSB_OAHashtable.this.isEmpty()) {
                    return false;
                }
                if (current_bucket >= t.length) {
                    return false;
                }

                // bucket actual vacío o listo?...
                if (t[current_bucket].isEmpty() || current_entry >= t[current_bucket].size() - 1) {
                    // ... -> ver el siguiente bucket no vacío...
                    int next_bucket = current_bucket + 1;
                    while (next_bucket < t.length && t[next_bucket].isEmpty()) {
                        next_bucket++;
                    }
                    return next_bucket < t.length;
                }

                // en principio alcanza con esto... revisar...    
                return true;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public K next() {
                // control: fail-fast iterator...
                if (TSB_OAHashtable.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if (!hasNext()) {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }

                // variable auxiliar t para simplificar accesos...
                TSBArrayList<Map.Entry<K, V>> t[] = TSB_OAHashtable.this.internalTable;

                // se puede seguir en el mismo bucket?...
                TSBArrayList<Map.Entry<K, V>> bucket = t[current_bucket];
                if (!t[current_bucket].isEmpty() && current_entry < bucket.size() - 1) {
                    current_entry++;
                } else {
                    // si no se puede...
                    // ...recordar el índice del bucket que se va a abandonar..
                    last_bucket = current_bucket;

                    // buscar el siguiente bucket no vacío, que DEBE existir, ya 
                    // que se hasNext() retornó true...
                    current_bucket++;
                    while (t[current_bucket].isEmpty()) {
                        current_bucket++;
                    }

                    // actualizar la referencia bucket con el núevo índice...
                    bucket = t[current_bucket];

                    // y posicionarse en el primer elemento de ese bucket...
                    current_entry = 0;
                }

                // avisar que next() fue invocado con éxito...
                next_ok = true;

                // y retornar la clave del elemento alcanzado...
                return bucket.get(current_entry).getKey();
            }

            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la
             * posición anterior al que fue removido. El elemento removido es el
             * que fue retornado la última vez que se invocó a next(). El método
             * sólo puede ser invocado una vez por cada invocación a next().
             */
            @Override
            public void remove() {
                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                // eliminar el objeto que retornó next() la última vez...
                Map.Entry<K, V> garbage = TSB_OAHashtable.this.internalTable[current_bucket].remove(current_entry);

                // quedar apuntando al anterior al que se retornó...                
                if (last_bucket != current_bucket) {
                    current_bucket = last_bucket;
                    current_entry = TSB_OAHashtable.this.internalTable[current_bucket].size() - 1;
                }

                // avisar que el remove() válido para next() ya se activó...
                next_ok = false;

                // la tabla tiene un elementon menos...
                TSB_OAHashtable.this.count--;

                // fail_fast iterator: todo en orden...
                TSB_OAHashtable.this.modCount++;
                expected_modCount++;
            }
        }
    }

    /*
     * Clase interna que representa una vista de todos los PARES mapeados en la
     * tabla: si la vista cambia, cambia también la tabla que le da respaldo, y
     * viceversa. La vista es stateless: no mantiene estado alguno (es decir, no
     * contiene datos ella misma, sino que accede y gestiona directamente datos
     * de otra fuente), por lo que no tiene atributos y sus métodos gestionan en
     * forma directa el contenido de la tabla. Están soportados los metodos para
     * eliminar un objeto (remove()), eliminar todo el contenido (clear) y la
     * creación de un Iterator (que incluye el método Iterator.remove()).
     */
    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntrySetIterator();
        }

        /*
         * Verifica si esta vista (y por lo tanto la tabla) contiene al par
         * que entra como parámetro (que debe ser de la clase Entry).
         */
        @Override
        public boolean contains(Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof Entry)) {
                return false;
            }

            Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
            K key = entry.getKey();
            int index = TSB_OAHashtable.this.hashIt(key);

            TSBArrayList<Map.Entry<K, V>> bucket = TSB_OAHashtable.this.internalTable[index];
            return bucket.contains(entry);
        }

        /*
         * Elimina de esta vista (y por lo tanto de la tabla) al par que entra
         * como parámetro (y que debe ser de tipo Entry).
         */
        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("remove(): parámetro null");
            }
            if (!(o instanceof Entry)) {
                return false;
            }

            Map.Entry<K, V> entry = (Map.Entry<K, V>) o;
            K key = entry.getKey();
            int index = TSB_OAHashtable.this.hashIt(key);
            TSBArrayList<Map.Entry<K, V>> bucket = TSB_OAHashtable.this.internalTable[index];

            if (bucket.remove(entry)) {
                TSB_OAHashtable.this.count--;
                TSB_OAHashtable.this.modCount++;
                return true;
            }
            return false;
        }

        @Override
        public int size() {
            return TSB_OAHashtable.this.count;
        }

        @Override
        public void clear() {
            TSB_OAHashtable.this.clear();
        }

        private class EntrySetIterator implements Iterator<Map.Entry<K, V>> {
            // índice de la lista actualmente recorrida...
            private int current_bucket;

            // índice de la lista anterior (si se requiere en remove())...
            private int last_bucket;

            // índice del elemento actual en el iterador (el que fue retornado 
            // la última vez por next() y será eliminado por remove())...
            private int current_entry;

            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;

            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;

            /*
             * Crea un iterador comenzando en la primera lista. Activa el
             * mecanismo fail-fast.
             */
            EntrySetIterator() {
                current_bucket = 0;
                last_bucket = 0;
                current_entry = -1;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya
             * sido retornado por next().
             */
            @Override
            public boolean hasNext() {
                // variable auxiliar t para simplificar accesos...
                TSBArrayList<Map.Entry<K, V>> t[] = TSB_OAHashtable.this.internalTable;

                if (TSB_OAHashtable.this.isEmpty()) {
                    return false;
                }
                if (current_bucket >= t.length) {
                    return false;
                }

                // bucket actual vacío o listo?...
                if (t[current_bucket].isEmpty() || current_entry >= t[current_bucket].size() - 1) {
                    // ... -> ver el siguiente bucket no vacío...
                    int next_bucket = current_bucket + 1;
                    while (next_bucket < t.length && t[next_bucket].isEmpty()) {
                        next_bucket++;
                    }
                    return next_bucket < t.length;
                }

                // en principio alcanza con esto... revisar...    
                return true;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public Map.Entry<K, V> next() {
                // control: fail-fast iterator...
                if (TSB_OAHashtable.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if (!hasNext()) {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }

                // variable auxiliar t para simplificar accesos...
                TSBArrayList<Map.Entry<K, V>> t[] = TSB_OAHashtable.this.internalTable;

                // se puede seguir en el mismo bucket?...
                TSBArrayList<Map.Entry<K, V>> bucket = t[current_bucket];
                if (!t[current_bucket].isEmpty() && current_entry < bucket.size() - 1) {
                    current_entry++;
                } else {
                    // si no se puede...
                    // ...recordar el índice del bucket que se va a abandonar..
                    last_bucket = current_bucket;

                    // buscar el siguiente bucket no vacío, que DEBE existir, ya 
                    // que se hasNext() retornó true...
                    current_bucket++;
                    while (t[current_bucket].isEmpty()) {
                        current_bucket++;
                    }

                    // actualizar la referencia bucket con el núevo índice...
                    bucket = t[current_bucket];

                    // y posicionarse en el primer elemento de ese bucket...
                    current_entry = 0;
                }

                // avisar que next() fue invocado con éxito...
                next_ok = true;

                // y retornar el elemento alcanzado...
                return bucket.get(current_entry);
            }

            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la
             * posición anterior al que fue removido. El elemento removido es el
             * que fue retornado la última vez que se invocó a next(). El método
             * sólo puede ser invocado una vez por cada invocación a next().
             */
            @Override
            public void remove() {
                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                // eliminar el objeto que retornó next() la última vez...
                Map.Entry<K, V> garbage = TSB_OAHashtable.this.internalTable[current_bucket].remove(current_entry);

                // quedar apuntando al anterior al que se retornó...                
                if (last_bucket != current_bucket) {
                    current_bucket = last_bucket;
                    current_entry = TSB_OAHashtable.this.internalTable[current_bucket].size() - 1;
                }

                // avisar que el remove() válido para next() ya se activó...
                next_ok = false;

                // la tabla tiene un elementon menos...
                TSB_OAHashtable.this.count--;

                // fail_fast iterator: todo en orden...
                TSB_OAHashtable.this.modCount++;
                expected_modCount++;
            }
        }
    }

    /*
     * Clase interna que representa una vista de todos los VALORES mapeados en
     * la tabla: si la vista cambia, cambia también la tabla que le da respaldo,
     * y viceversa. La vista es stateless: no mantiene estado alguno (es decir,
     * no contiene datos ella misma, sino que accede y gestiona directamente los
     * de otra fuente), por lo que no tiene atributos y sus métodos gestionan en
     * forma directa el contenido de la tabla. Están soportados los metodos para
     * eliminar un objeto (remove()), eliminar todo el contenido (clear) y la
     * creación de un Iterator (que incluye el método Iterator.remove()).
     */
    private class ValueCollection extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return new ValueCollectionIterator();
        }

        @Override
        public int size() {
            return TSB_OAHashtable.this.count;
        }

        @Override
        public boolean contains(Object o) {
            return TSB_OAHashtable.this.containsValue(o);
        }

        @Override
        public void clear() {
            TSB_OAHashtable.this.clear();
        }

        private class ValueCollectionIterator implements Iterator<V> {
            // índice de la lista actualmente recorrida...
            private int current_bucket;

            // índice de la lista anterior (si se requiere en remove())...
            private int last_bucket;

            // índice del elemento actual en el iterador (el que fue retornado 
            // la última vez por next() y será eliminado por remove())...
            private int current_entry;

            // flag para controlar si remove() está bien invocado...
            private boolean next_ok;

            // el valor que debería tener el modCount de la tabla completa...
            private int expected_modCount;

            /*
             * Crea un iterador comenzando en la primera lista. Activa el
             * mecanismo fail-fast.
             */
            ValueCollectionIterator() {
                current_bucket = 0;
                last_bucket = 0;
                current_entry = -1;
                next_ok = false;
                expected_modCount = TSB_OAHashtable.this.modCount;
            }

            /*
             * Determina si hay al menos un elemento en la tabla que no haya
             * sido retornado por next().
             */
            @Override
            public boolean hasNext() {
                // variable auxiliar t para simplificar accesos...
                TSBArrayList<Map.Entry<K, V>>[] t = TSB_OAHashtable.this.internalTable;

                if (TSB_OAHashtable.this.isEmpty()) {
                    return false;
                }
                if (current_bucket >= t.length) {
                    return false;
                }

                // bucket actual vacío o listo?...
                if (t[current_bucket].isEmpty() || current_entry >= t[current_bucket].size() - 1) {
                    // ... -> ver el siguiente bucket no vacío...
                    int next_bucket = current_bucket + 1;
                    while (next_bucket < t.length && t[next_bucket].isEmpty()) {
                        next_bucket++;
                    }
                    return next_bucket < t.length;
                }

                // en principio alcanza con esto... revisar...    
                return true;
            }

            /*
             * Retorna el siguiente elemento disponible en la tabla.
             */
            @Override
            public V next() {
                // control: fail-fast iterator...
                if (TSB_OAHashtable.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if (!hasNext()) {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }

                // variable auxiliar t para simplificar accesos...
                TSBArrayList<Map.Entry<K, V>>[] t = TSB_OAHashtable.this.internalTable;

                // se puede seguir en el mismo bucket?...
                TSBArrayList<Map.Entry<K, V>> bucket = t[current_bucket];
                if (!t[current_bucket].isEmpty() && current_entry < bucket.size() - 1) {
                    current_entry++;
                } else {
                    // si no se puede...
                    // ...recordar el índice del bucket que se va a abandonar..
                    last_bucket = current_bucket;

                    // buscar el siguiente bucket no vacío, que DEBE existir, ya 
                    // que se hasNext() retornó true...
                    current_bucket++;
                    while (t[current_bucket].isEmpty()) {
                        current_bucket++;
                    }

                    // actualizar la referencia bucket con el núevo índice...
                    bucket = t[current_bucket];

                    // y posicionarse en el primer elemento de ese bucket...
                    current_entry = 0;
                }

                // avisar que next() fue invocado con éxito...
                next_ok = true;

                // y retornar la clave del elemento alcanzado...
                return bucket.get(current_entry).getValue();
            }

            /*
             * Remueve el elemento actual de la tabla, dejando el iterador en la
             * posición anterior al que fue removido. El elemento removido es el
             * que fue retornado la última vez que se invocó a next(). El método
             * sólo puede ser invocado una vez por cada invocación a next().
             */
            @Override
            public void remove() {
                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                // eliminar el objeto que retornó next() la última vez...
                Map.Entry<K, V> garbage = TSB_OAHashtable.this.internalTable[current_bucket].remove(current_entry);

                // quedar apuntando al anterior al que se retornó...                
                if (last_bucket != current_bucket) {
                    current_bucket = last_bucket;
                    current_entry = TSB_OAHashtable.this.internalTable[current_bucket].size() - 1;
                }

                // avisar que el remove() válido para next() ya se activó...
                next_ok = false;

                // la tabla tiene un elementon menos...
                TSB_OAHashtable.this.count--;

                // fail_fast iterator: todo en orden...
                TSB_OAHashtable.this.modCount++;
                expected_modCount++;
            }
        }
    }
}
