package pink.zak.discord.utils.tuple;

public class MutablePair<K, V> {
    private K key;
    private V value;

    public MutablePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <S, U> MutablePair<S, U> of(S key, U value) {
        return new MutablePair<>(key, value);
    }

    public K key() {
        return this.key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V value() {
        return this.value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
