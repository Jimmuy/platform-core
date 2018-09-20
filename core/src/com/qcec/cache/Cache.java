package com.qcec.cache;

/**
 * Cache interface
 *
 * 
 */

public interface Cache<K, V> {

	/**
	 * Puts value into cache by key
	 * 
	 * @return <b>true</b> - if value was put into cache successfully,
	 *         <b>false</b> - if value was <b>not</b> put into cache
	 */
	boolean put(K key, V value);

	/**
	 * Returns value by key. If there is no value for key then null will be
	 * returned.
	 */
	V get(K key);

	/** 
	 * Removes item by key 
	 */
	void remove(K key);

	/** 
	 * Remove all items from cache 
	 */
	void clear();
}
