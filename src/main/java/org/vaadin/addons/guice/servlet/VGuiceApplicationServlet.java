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
package org.vaadin.addons.guice.servlet;

import org.vaadin.addons.guice.ui.ScopedUIProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServlet;

/**
 * A {@link VaadinServlet} which on session initialisation sets the
 * {@link UIProvider} to the {@link ScopedUIProvider}
 * 
 * @author Will Temperley
 * 
 */
@Singleton
public class VGuiceApplicationServlet extends VaadinServlet implements
        SessionInitListener {

    private final ScopedUIProvider applicationProvider;

    @Inject
    public VGuiceApplicationServlet(ScopedUIProvider applicationProvider) {
        this.applicationProvider = applicationProvider;
    }

    @Override
    protected void servletInitialized() {
        getService().addSessionInitListener(this);
    }

    @Override
    public void sessionInit(SessionInitEvent event) throws ServiceException {
        event.getSession().addUIProvider(applicationProvider);
    }

}
