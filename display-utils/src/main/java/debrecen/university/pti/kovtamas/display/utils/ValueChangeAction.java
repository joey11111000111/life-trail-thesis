package debrecen.university.pti.kovtamas.display.utils;

@FunctionalInterface
public interface ValueChangeAction<T> {

    void accept(T fromValue, T toValue);

}
