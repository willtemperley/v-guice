v-guice
=======

The machinery necessary to create Vaadin 7 applications using Guice for dependency injection.  This project is really a distilled version of https://github.com/davidsowerby/v7/.

To Guice-up your  it for your project, you'll need to do the following:

1. Create your injectable UI, by extending ScopedUI:

		@Theme("runo")
		public class GuicedUI extends ScopedUI  {

			@Inject
			public GuicedUI(InjectableObject obj) {

			}

			@Override
			protected void init(VaadinRequest request) {
			}
		}

2. Create your Guice servlet module (the web.xml replacement):

		public class GuicedServletModule extends ServletModule {

		    @Override
    		protected void configureServlets() {
    		
        		serve("/*").with(VGuiceApplicationServlet.class, getServletParams());
    		
    		}
		}
		

3. Create the listener which bootstraps the whole web app. Here you tell the UIScopeModule about your GuicedUI created in step 1.
See https://code.google.com/p/google-guice/wiki/Servlets for full instructions and background.

		public class GuiceContextListener extends GuiceServletContextListener {
			@Override
			protected Injector getInjector() {
				return Guice.createInjector(new UIScopeModule(GuicedUI.class), new GuicedServletModule());
			}
		}
		
		
Henceforth, any objects injected into your UI are also injectable. These can be scoped to the UI level or higher (e.g. Session scope or Singleton).
UIScope essentially means "Browser window scope". 
