/**
 * Copyright (C) Alejandro Ayuso
 *
 * This file is part of Amforeas. Amforeas is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * Amforeas is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Amforeas. If not, see <http://www.gnu.org/licenses/>.
 */

package amforeas.cache;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/**
 * A very simple cache implementation
 * @param <K>
 * @param <J>
 */
public class SimpleCache<K extends Comparable<K>, J> {

    private final Map<K, Item<J>> cache = Collections.synchronizedMap(new WeakHashMap<>());

    private final Integer ttl;

    /**
     * Create a cache with a default TTL of 30 minutes
     */
    public SimpleCache() {
        this.ttl = 1800000; // 30 minutes
    }

    /**
     * Create a cache with a custom TTL
     * @param ttl - in milliseconds
     */
    public SimpleCache(Integer ttl) {
        this.ttl = ttl;
    }

    public Item<J> put (K key, J value) {
        Item<J> i = new Item<J>(value, Instant.now().plusMillis(ttl));
        return cache.put(key, i);
    }

    public Optional<J> get (K key) {
        Item<J> i = cache.get(key);

        if (i == null) {
            return Optional.empty();
        }

        if (!i.isValid()) {
            return Optional.empty();
        }

        return Optional.of(i.getValue());
    }

    public J remove (K key) {
        Item<J> i = this.cache.remove(key);
        return i != null ? i.getValue() : null;
    }

    private class Item<V> {

        private final V value;
        private final Instant from;

        public Item(V value, Instant from) {
            super();
            this.value = value;
            this.from = from;
        }

        public V getValue () {
            return value;
        }

        public Instant getFrom () {
            return from;
        }

        public boolean isValid () {
            return this.getFrom().isAfter(Instant.now());
        }

    }
}
