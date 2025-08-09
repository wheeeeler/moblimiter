package handl.interactor.voodoo.filter;

import handl.interactor.voodoo.handler.EventHandler;

public interface EventFilter<E> {

    boolean test(EventHandler eventHandler, E event);
}
