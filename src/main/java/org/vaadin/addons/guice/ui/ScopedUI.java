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
package org.vaadin.addons.guice.ui;

import org.vaadin.addons.guice.uiscope.UIKey;
import org.vaadin.addons.guice.uiscope.UIScope;

import com.vaadin.ui.UI;

/**
 * Base class for all Guice-Vaadin-MVP-based applications.
 * 
 * @author David Sowerby
 *
 */
public abstract class ScopedUI extends UI {

    private UIKey instanceKey;
    private UIScope uiScope;

    protected void setInstanceKey(UIKey instanceKey) {
        this.instanceKey = instanceKey;
    }

    public UIKey getInstanceKey() {
        return instanceKey;
    }

    protected void setScope(UIScope uiScope) {
        this.uiScope = uiScope;
    }

    @Override
    public void detach() {
        if (uiScope != null) {
            uiScope.releaseScope(instanceKey);
        }
        super.detach();
    }
}