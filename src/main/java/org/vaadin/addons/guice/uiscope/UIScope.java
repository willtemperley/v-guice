/*
 * Copyright 2012 Vaadin Community.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.vaadin.addons.guice.uiscope;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.servlet.ServletScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.addons.guice.ui.ScopedUI;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Vaadin UI visibility level, similar to {@link ServletScopes#SESSION}.
 *
 * @author David Sowerby
 */
public class UIScope implements Scope {


    static Logger logger = LoggerFactory.getLogger(UIScope.class);

    private static volatile UIScope current;

    private final Map<UIKey, Map<Key<?>, Object>> cache = new TreeMap<UIKey, Map<Key<?>, Object>>();

    <T> Map<Key<?>, Object> getScopedObjectMap(UIKey uiKey) {
        // return an existing cache instance
        if (cache.containsKey(uiKey)) {
            Map<Key<?>, Object> scopedObjects = cache.get(uiKey);
            logger.debug(String.format("Scope cache retrieved for UI key: [%s]", uiKey));
            return scopedObjects;
        } else {
            return createCacheEntry(uiKey);
        }
    }

    private Map<Key<?>, Object> createCacheEntry(UIKey uiKey) {
        Map<Key<?>, Object> uiEntry = new HashMap<Key<?>, Object>();
        cache.put(uiKey, uiEntry);
        logger.debug(String.format("Created a scope cache for UIScope with key: [%s]", uiKey));
        return uiEntry;
    }

    public boolean isCacheHasEntryFor(ScopedUI ui) {
        return isCacheHasEntryFor(ui.getInstanceKey());
    }

    public void startScope(UIKey uiKey) {
        if (!isCacheHasEntryFor(uiKey)) {
            createCacheEntry(uiKey);
        }
    }

    public boolean isCacheHasEntryFor(UIKey uiKey) {
        return cache.containsKey(uiKey);
    }

    public void releaseScope(UIKey uiKey) {
        cache.remove(uiKey);
    }

    public static UIScope getCurrent() {
        // double-checked locking with volatile
        UIScope scope = current;
        if (scope == null) {
            synchronized (UIScope.class) {
                scope = current;
                if (scope == null) {
                    current = new UIScope();
                    scope = current;
                }
            }
        }
        return scope;
    }

    /**
     * Removes all entries in the cache
     */
    public void flush() {
        cache.clear();
    }

    @Override
    public <T> Provider<T> scope(Key<T> key, Provider<T> unscoped) {
        return new UIScopeProvider<T>(this, key, unscoped);
    }
}