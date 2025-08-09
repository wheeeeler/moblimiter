package handl.interactor.voodoo.dispatch;

public interface EventDispatcher {

    <E> void dispatch(E event);
}
