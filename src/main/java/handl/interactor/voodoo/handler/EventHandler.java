package handl.interactor.voodoo.handler;

import handl.interactor.voodoo.filter.EventFilter;

public interface EventHandler extends Comparable<EventHandler> {

    <E> void handle(final E event);

    Object getListener();

    ListenerPriority getPriority();

    Iterable<EventFilter> getFilters();
}
