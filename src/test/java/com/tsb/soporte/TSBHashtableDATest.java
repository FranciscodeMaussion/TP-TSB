package com.tsb.soporte;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;


public class TSBHashtableDATest {

    private TSBHashtableDA<String, Integer> tsbHashtableDA;

    @Before
    public void setUp() {
        tsbHashtableDA = new TSBHashtableDA<>();

    }

    @Test
    public void size_ShouldReturnZero_WhenTableIsEmpty() {
        assertEquals(tsbHashtableDA.size(), 0);
    }

    @Test
    public void size_ShouldReturnNumberOfContainedItems_WhenTableNotEmpty() {
        tsbHashtableDA.put("1", 1);
        tsbHashtableDA.put("2", 2);
        tsbHashtableDA.put("3", 3);
        assertEquals(tsbHashtableDA.size(), 3);
    }

    @Test
    public void isEmpty_ShouldReturnTrue_WhenTableIsEmpty() {
        assertTrue(tsbHashtableDA.isEmpty());
    }

    @Test
    public void isEmpty_ShouldReturnFalse_WhenTableIsNotEmpty() {
        tsbHashtableDA.put("3", 3);
        assertFalse(tsbHashtableDA.isEmpty());
    }

    @Test
    public void containsKey_ShouldReturnFalse_WhenKeyIsNotContained() {
        assertFalse(tsbHashtableDA.containsKey(""));
    }

    @Test
    public void containsKey_ShouldReturnTrue_WhenKeyIsContained() {
        tsbHashtableDA.put("3", 3);
        assertTrue(tsbHashtableDA.containsKey("3"));
    }

    @Test
    public void containsValue_ShouldReturnFalse_WhenValueIsNotContained() {
        assertFalse(tsbHashtableDA.containsValue(5));
    }

    @Test
    public void containsValue_ShouldReturnTrue_WhenValueIsContained() {
        tsbHashtableDA.put("3", 3);
        assertTrue(tsbHashtableDA.containsValue(3));
    }

    @Test
    public void get_ShouldReturnNull_WhenTableIsEmpty() {
        assertNull(tsbHashtableDA.get(""));
    }

    @Test
    public void get_ShouldReturnValue_WhenKeyIsFound() {
        int value = 3;
        String key = "3";
        tsbHashtableDA.put(key, value);
        Integer result = tsbHashtableDA.get(key);

        assertEquals(result.intValue(), value);
    }

    @Test
    public void get_ShouldWork_AfterRehash() {
        int value = 40;
        String key = "40";
        tsbHashtableDA.put(key, value);


        int initialCapacity = tsbHashtableDA.getCapacity();

        for (int i = 0; i < initialCapacity + 1; i++) {
            tsbHashtableDA.put("" + i, i);
        }

        Integer result = tsbHashtableDA.get(key);

        assertEquals(result.intValue(), value);
    }

    @Test(expected = NullPointerException.class)
    public void put_ShouldThrowNPE_WhenKeyIsNull() {
        tsbHashtableDA.put(null, 5);
    }

    @Test(expected = NullPointerException.class)
    public void put_ShouldThrowNPE_WhenValueIsNull() {
        tsbHashtableDA.put("", null);
    }

    @Test
    public void put_ShouldInsertCorrectly_WhenValuesWasNotPreviouslyInserted() {
        int value = 5;
        Integer result = tsbHashtableDA.put("", value);

        assertNull(result);
        assertTrue(tsbHashtableDA.containsKey(""));
        assertFalse(tsbHashtableDA.isEmpty());
        assertEquals(tsbHashtableDA.size(), 1);
    }

    @Test
    public void put_ShouldRehash_WhenTableCapacityIsExceeded() {
        int initialCapacity = tsbHashtableDA.getCapacity();

        for (int i = 0; i < initialCapacity + 1; i++) {
            tsbHashtableDA.put("" + i, i);
        }

        assertTrue(tsbHashtableDA.getCapacity() > initialCapacity);
    }

    @Test(expected = NullPointerException.class)
    public void remove_ShouldThrowNPE_WhenKeyIsNull() {
        tsbHashtableDA.remove(null);
    }

    @Test
    public void remove_ShouldReturnNull_WhenKeyIsNotFound() {
        tsbHashtableDA.put("5", 5);

        Integer result = tsbHashtableDA.remove("2");

        assertNull(result);
    }

    @Test
    public void remove_ShouldReturnRemovedValue_WhenKeyIsFound() {
        int value = 40;
        String key = "40";
        tsbHashtableDA.put(key, value);

        Integer result = tsbHashtableDA.remove(key);

        assertEquals(result.intValue(), value);

    }

    @Test
    public void remove_ShouldReduceSize_WhenKeyIsFound() {
        int value = 40;
        String key = "40";
        tsbHashtableDA.put(key, value);
        int initialSize = tsbHashtableDA.size();
        tsbHashtableDA.remove(key);
        int afterSize = tsbHashtableDA.size();

        assertEquals(afterSize, initialSize - 1);
    }

    @Test
    public void putAll_ShouldInsertAllElements_WhenCalled() {
        TSBHashtableDA<String, Integer> secondOaHashtable = new TSBHashtableDA<>();
        for (int i = 0; i < 4; i++) {
            secondOaHashtable.put("" + i, i);
        }
        tsbHashtableDA.putAll(secondOaHashtable);

        for (int i = 0; i < 4; i++) {
            int result = tsbHashtableDA.get("" + i);
            assertEquals(result, i);
        }
    }

    @Test
    public void clear_ShouldReduceSizeToZero_WhenCalled() {
        for (int i = 0; i < 4; i++) {
            tsbHashtableDA.put("" + i, i);
        }
        tsbHashtableDA.clear();

        assertEquals(tsbHashtableDA.size(), 0);
    }

    @Test
    public void keySet_ShouldReturnAKeySetContainingAllValues_WhenCalled() {
        int[] array = {1, 2, 3, 4};
        for (int value : array) {
            tsbHashtableDA.put("" + value, value);
        }
        Set<String> keySet = tsbHashtableDA.keySet();

        assertTrue(keySet.containsAll(Arrays.asList("1", "2", "3", "4")));
    }

    @Test
    public void keySet_ShouldReturnAKeySetWithWorkingIterator_WhenCalled() {
        for (int i = 0; i < 4; i++) {
            tsbHashtableDA.put("" + i, i);
        }
        Set<String> keySet = tsbHashtableDA.keySet();
        Iterator<String> iterator = keySet.iterator();

        int index = 0;
        while (iterator.hasNext()) {
            String key = iterator.next();
            assertEquals(key, "" + index);
            index++;
        }
    }

    @Test
    public void values_ShouldReturnACollectionContainingAllValues_WhenCalled() {
        int[] array = {1, 2, 3, 4};
        for (int value : array) {
            tsbHashtableDA.put("" + value, value);
        }

        Collection<Integer> collection = tsbHashtableDA.values();

        collection.containsAll((Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    public void values_ShouldReturnACollectionWithAWorkingIterator_WhenCalled() {
        for (int i = 0; i < 4; i++) {
            tsbHashtableDA.put("" + i, i);
        }
        Collection<Integer> collection = tsbHashtableDA.values();
        Iterator<Integer> iterator = collection.iterator();

        int index = 0;
        while (iterator.hasNext()) {
            Integer value = iterator.next();
            assertEquals(value.intValue(), index);
            index++;
        }
    }

    @Test
    public void entrySet_ShouldReturnAnEntrySetWithAWorkingIterator_WhenCalled() {
        int[] array = {1, 2, 3, 4};
        for (int value : array) {
            tsbHashtableDA.put("" + value, value);
        }
        Set<Map.Entry<String, Integer>> entrySet = tsbHashtableDA.entrySet();
        Iterator<Map.Entry<String, Integer>> iterator = entrySet.iterator();
        int index = 0;
        int[] valueArray = new int[4];
        String[] keyArray = new String[4];
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> current = iterator.next();
            valueArray[index] = current.getValue();
            keyArray[index] = current.getKey();
            index++;
        }
        Arrays.sort(valueArray);
        Arrays.sort(keyArray);

        assertArrayEquals(array, valueArray);

        for (int i = 0; i < keyArray.length; i++) {
            assertEquals("" + array[i], keyArray[i]);
        }

    }
}
