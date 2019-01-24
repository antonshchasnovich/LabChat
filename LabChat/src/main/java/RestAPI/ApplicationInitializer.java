package RestAPI;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class ApplicationInitializer implements WebApplicationInitializer {

    private final static String SERVLETNAME = "api";

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(WebConfig.class);
        servletContext.addListener(new ContextLoaderListener(ctx));
        ServletRegistration.Dynamic servlet = servletContext.addServlet(SERVLETNAME, new DispatcherServlet(ctx));
        ServletRegistration.Dynamic servlet2 = servletContext.addServlet("chat", new DispatcherServlet(ctx));
        servlet.addMapping("/");
        servlet2.addMapping("/");
        servlet.setLoadOnStartup(1);
        servlet2.setLoadOnStartup(2);
    }
}
