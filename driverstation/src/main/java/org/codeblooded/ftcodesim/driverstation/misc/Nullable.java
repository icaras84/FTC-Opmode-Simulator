package org.codeblooded.ftcodesim.driverstation.misc;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Nullable<T> {

    private static final Nullable<?> EMPTY = new Nullable<>(null);

    private final T value;

    private Nullable(T value) {
        this.value = value;
    }

    public static <T> Nullable<T> of(T value){
        return value == null ? Nullable.empty() : new Nullable<>(value);
    }

    @SuppressWarnings("unchecked")
    public static <T> Nullable<T> empty(){
        return (Nullable<T>) Nullable.EMPTY;
    }

    public T get(){
        if (this.isNull()) throw new RuntimeException("Value is not present.");
        return this.value;
    }

    public T getOrDefault(T otherValue){
        return this.isPresent() ? this.value : otherValue;
    }

    public T getOrElse(Supplier<T> otherValueSupplier){
        return this.isPresent() ? this.value : otherValueSupplier.get();
    }

    public boolean isPresent(){
        return this.value != null;
    }

    public void ifPresent(Consumer<? super T> onPresent){
        if (this.isPresent()) onPresent.accept(this.value);
    }

    public void ifPresentOrElse(Consumer<? super T> onPresent, Runnable onElse){
        if (this.isPresent()) onPresent.accept(this.value);
        else onElse.run();
    }

    public boolean isNull(){
        return this.value == null;
    }

    public void ifNull(Runnable onNull){
        if (this.isNull()) onNull.run();
    }

    public void ifNullOrElse(Runnable onNull, Consumer<? super T> onElse){
        if (this.isNull()) onNull.run();
        else onElse.accept(this.value);
    }
}
