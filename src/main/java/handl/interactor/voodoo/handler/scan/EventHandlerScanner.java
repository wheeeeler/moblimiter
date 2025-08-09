package handl.interactor.voodoo.handler.scan;

import java.util.Map;
import java.util.Set;

import handl.interactor.voodoo.handler.EventHandler;

public interface EventHandlerScanner {

    Map<Class<?>, Set<EventHandler>> locate(Object listenerContainer);
}
