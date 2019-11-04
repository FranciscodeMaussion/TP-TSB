package com.tsb.soporte;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;


public class OAHashtableTest {

    private TSBHashtableDA<String, Integer> oaHashtable;

    @Before
    public void setUp() {
        oaHashtable = new TSBHashtableDA<>();

    }

    @Test
    public void size_ShouldReturnZero_WhenTableIsEmpty() {
        assertEquals(oaHashtable.size(), 0);
    }

    @Test
    public void size_ShouldReturnNumberOfContainedItems_WhenTableNotEmpty() {
        oaHashtable.put("1", 1);
        oaHashtable.put("2", 2);
        oaHashtable.put("3", 3);
        assertEquals(oaHashtable.size(), 3);
    }

    @Test
    public void isEmpty_ShouldReturnTrue_WhenTableIsEmpty() {
        assertTrue(oaHashtable.isEmpty());
    }

    @Test
    public void isEmpty_ShouldReturnFalse_WhenTableIsNotEmpty() {
        oaHashtable.put("3", 3);
        assertFalse(oaHashtable.isEmpty());
    }

    @Test
    public void containsKey_ShouldReturnFalse_WhenKeyIsNotContained() {
        assertFalse(oaHashtable.containsKey(""));
    }

    @Test
    public void containsKey_ShouldReturnTrue_WhenKeyIsContained() {
        oaHashtable.put("3", 3);
        assertTrue(oaHashtable.containsKey("3"));
    }

    @Test
    public void containsValue_ShouldReturnFalse_WhenValueIsNotContained() {
        assertFalse(oaHashtable.containsValue(5));
    }

    @Test
    public void containsValue_ShouldReturnTrue_WhenValueIsContained() {
        oaHashtable.put("3", 3);
        assertTrue(oaHashtable.containsValue(3));
    }

    @Test
    public void get_ShouldReturnNull_WhenTableIsEmpty() {
        assertNull(oaHashtable.get(""));
    }

    @Test
    public void get_ShouldReturnValue_WhenKeyIsFound() {
        int value = 3;
        String key = "3";
        oaHashtable.put(key, value);
        Integer result = oaHashtable.get(key);

        assertEquals(result.intValue(), value);
    }

    @Test
    public void get_ShouldWork_AfterRehash() {
        int value = 40;
        String key = "40";
        oaHashtable.put(key, value);


        int initialCapacity = oaHashtable.getCapacity();

        for (int i = 0; i < initialCapacity + 1; i++) {
            oaHashtable.put("" + i, i);
        }

        Integer result = oaHashtable.get(key);

        assertEquals(result.intValue(), value);
    }

    @Test(expected = NullPointerException.class)
    public void put_ShouldThrowNPE_WhenKeyIsNull() {
        oaHashtable.put(null, 5);
    }

    @Test(expected = NullPointerException.class)
    public void put_ShouldThrowNPE_WhenValueIsNull() {
        oaHashtable.put("", null);
    }

    @Test
    public void put_ShouldInsertCorrectly_WhenValuesWasNotPreviouslyInserted() {
        int value = 5;
        Integer result = oaHashtable.put("", value);

        assertNull(result);
        assertTrue(oaHashtable.containsKey(""));
        assertFalse(oaHashtable.isEmpty());
        assertEquals(oaHashtable.size(), 1);
    }

    @Test
    public void put_ShouldRehash_WhenTableCapacityIsExceeded() {
        int initialCapacity = oaHashtable.getCapacity();

        for (int i = 0; i < initialCapacity + 1; i++) {
            oaHashtable.put("" + i, i);
        }

        assertTrue(oaHashtable.getCapacity() > initialCapacity);
    }

    @Test(expected = NullPointerException.class)
    public void remove_ShouldThrowNPE_WhenKeyIsNull() {
        oaHashtable.remove(null);
    }

    @Test
    public void remove_ShouldReturnNull_WhenKeyIsNotFound() {
        oaHashtable.put("5", 5);

        Integer result = oaHashtable.remove("2");

        assertNull(result);
    }

    @Test
    public void remove_ShouldReturnRemovedValue_WhenKeyIsFound() {
        int value = 40;
        String key = "40";
        oaHashtable.put(key, value);

        Integer result = oaHashtable.remove(key);

        assertEquals(result.intValue(), value);

    }

    @Test
    public void remove_ShouldReduceSize_WhenKeyIsFound() {
        int value = 40;
        String key = "40";
        oaHashtable.put(key, value);
        int initialSize = oaHashtable.size();
        oaHashtable.remove(key);
        int afterSize = oaHashtable.size();

        assertEquals(afterSize, initialSize - 1);
    }

    @Test
    public void putAll_ShouldInsertAllElements_WhenCalled() {
        OAHashtable<String, Integer> secondOaHashtable = new OAHashtable<>();
        for (int i = 0; i < 4; i++) {
            secondOaHashtable.put("" + i, i);
        }
        oaHashtable.putAll(secondOaHashtable);

        for (int i = 0; i < 4; i++) {
            int result = oaHashtable.get("" + i);
            assertEquals(result, i);
        }
    }

    @Test
    public void clear_ShouldReduceSizeToZero_WhenCalled() {
        for (int i = 0; i < 4; i++) {
            oaHashtable.put("" + i, i);
        }
        oaHashtable.clear();

        assertEquals(oaHashtable.size(), 0);
    }

    @Test
    public void keySet_ShouldReturnAKeySetContainingAllValues_WhenCalled() {
        int[] array = {1, 2, 3, 4};
        for (int value : array) {
            oaHashtable.put("" + value, value);
        }
        Set<String> keySet = oaHashtable.keySet();

        assertTrue(keySet.containsAll(Arrays.asList("1", "2", "3", "4")));
    }

    @Test
    public void keySet_ShouldReturnAKeySetWithWorkingIterator_WhenCalled() {
        for (int i = 0; i < 4; i++) {
            oaHashtable.put("" + i, i);
        }
        Set<String> keySet = oaHashtable.keySet();
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
            oaHashtable.put("" + value, value);
        }

        Collection<Integer> collection = oaHashtable.values();

        collection.containsAll((Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    public void values_ShouldReturnACollectionWithAWorkingIterator_WhenCalled() {
        for (int i = 0; i < 4; i++) {
            oaHashtable.put("" + i, i);
        }
        Collection<Integer> collection = oaHashtable.values();
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
            oaHashtable.put("" + value, value);
        }
        Set<Map.Entry<String, Integer>> entrySet = oaHashtable.entrySet();
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
