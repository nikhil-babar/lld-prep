package Cache;

public class CacheValue {
    private final Object value;

    public CacheValue(Object value){
        this.value = value;
    }

    public boolean isNumeric(){
        return value instanceof Number;
    }

    public <T> T getValue(Class<T> type){
        return type.cast(this.value);
    }
}
