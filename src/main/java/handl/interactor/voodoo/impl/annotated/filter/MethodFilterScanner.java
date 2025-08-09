package handl.interactor.voodoo.impl.annotated.filter;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import handl.interactor.voodoo.filter.EventFilter;
import handl.interactor.voodoo.filter.EventFilterScanner;
import handl.interactor.voodoo.impl.annotated.handler.annotation.Listener;

public final class MethodFilterScanner implements EventFilterScanner<Method> {

    @Override
    public Set<EventFilter> scan(final Method listener) {
        if (!listener.isAnnotationPresent(Listener.class))
            return Collections.emptySet();

        final Set<EventFilter> filters = new HashSet<>();

        for (final Class<? extends EventFilter> filter : listener
                .getDeclaredAnnotation(Listener.class).filters())
            try {
                filters.add(filter.newInstance());
            } catch (final Exception exception) {
                exception.printStackTrace();
            }

        return filters;
    }
}
