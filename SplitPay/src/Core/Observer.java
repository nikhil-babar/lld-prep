package Core;

public interface Observer<T> {
    void onAdd(T item);
    void onUpdate(T item);
}
