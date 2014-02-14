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

import java.util.Map;

import org.vaadin.addons.guice.ui.ScopedUI;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

/**
 * @author David Sowerby
 *
 * @param <T>
 */
class UIScopeProvider<T> implements Provider<T> {

    private final UIScope uiScope;
    private final Key<T> key;
    private final Provider<T> unscoped;

    UIScopeProvider(UIScope uiScope, Key<T> key, Provider<T> unscoped) {
        this.uiScope = uiScope;
        this.key = key;
        this.unscoped = unscoped;
    }

    @Override
    public T get() {
        // get the scope cache for the current UI
        UIScope.logger.debug(String.format("Looking for a UIScoped instance of: [%s]", key));

        // get the current UIKey. It should always be there, as it is created before the UI
        UIKey uiKey = CurrentInstance.get(UIKey.class);
        // this may be null if we are in the process of constructing the UI
        ScopedUI currentUI = (ScopedUI) UI.getCurrent();
        String msg = "This should not be possible, unless perhaps you are testing and have not set up the test fixture correctly.  Try sub-classing AbstractMVPTestBase and run it with subclass of AbstractMVPApplicationTestModule.  If you are not testing please report a bug";
        if (uiKey == null) {
            if (currentUI == null) {
                throw new UIScopeException("ERROR: UI and uiKey are null. " + msg);
            } else {
                // this can happen when the framework switches UIs
                uiKey = currentUI.getInstanceKey();
                if (uiKey == null) {
                    throw new UIScopeException("ERROR: uiKey is null and cannot be obtained from the UI. " + msg);
                }
            }
        }

        // currentUI may be null if we are in the process of constructing the UI
        // if not null just check that it hasn't got out of sync with its uikey
        if (currentUI != null) {
            if (!uiKey.equals(currentUI.getInstanceKey())) {
                throw new UIScopeException(
                        "ERROR: The UI and its UIKey have got out of sync.  Results are unpredictable. " + msg);
            }
        }

        UIScope.logger.debug(String.format("Looking for cache for key: [%s]", uiKey));
        Map<Key<?>, Object> scopedObjects = this.uiScope.getScopedObjectMap(uiKey);

        // retrieve an existing instance if possible

        @SuppressWarnings("unchecked")
        T current = (T) scopedObjects.get(key);

        if (current != null) {
            UIScope.logger.debug(String.format("Returning existing instance of [%s]", getInstanceSimpleClassName(current)));
            return current;
        }

        // or create the first instance and cache it
        current = unscoped.get();
        scopedObjects.put(key, current);
        UIScope.logger.debug(String.format("New instance of [%s] created, as none in cache", getInstanceSimpleClassName(current)));
        return current;
    }

    private String getInstanceSimpleClassName(Object instance) {
        String simpleName = instance.getClass().getSimpleName();
        return simpleName.isEmpty() ? instance.getClass().getName() : simpleName;
    }
}