package fr.tokazio;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;

@Provider
public class StatusFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) {
        if (containerResponseContext.getStatus() == 200) {
            for (Annotation annotation : containerResponseContext.getEntityAnnotations()) {
                if (annotation instanceof ResponseStatus) {
                    containerResponseContext.setStatus(((ResponseStatus) annotation).value());
                    break;
                }
            }
        }
    }
}