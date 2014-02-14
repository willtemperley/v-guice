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

import org.vaadin.addons.guice.ui.ScopedUI;
import org.vaadin.addons.guice.ui.ScopedUIProvider;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.ui.UI;

/**
 * {@link UIScope} support module.
 * 
 * @authors David Sowerby, Will Temperley
 */
public class UIScopeModule extends AbstractModule {

    private final UIScope uiScope;

    private ScopedUIProvider uiProvider;

    /**
     * @param uiClazz
     *            The main application UI
     */
    public UIScopeModule(final Class<? extends ScopedUI> uiClazz) {
        uiScope = UIScope.getCurrent();

        this.uiProvider = new ScopedUIProvider() {
            @Override
            public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
                return uiClazz;
            }
        };

    }

    @Override
    public void configure() {
        // tell Guice about the scope
        bindScope(UIScoped.class, uiScope);

        // make our scope instance injectable
        bind(UIScope.class).annotatedWith(Names.named("UIScope")).toInstance(uiScope);

        bind(ScopedUIProvider.class).toInstance(uiProvider);
    }

    public UIScope getUIScope() {
        return uiScope;
    }

}