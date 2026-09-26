package org.codeblooded.ftcodesim.driverstation.misc;

import java.util.Vector;
import java.util.function.Consumer;

public class Publisher<T> {

    private final Vector<Consumer<T>> subscribers;

    private Publisher(Vector<Consumer<T>> subscribers) {
        this.subscribers = subscribers;
    }

    public static <T> Publisher<T> of(Vector<Consumer<T>> initialSubscribers){
        return initialSubscribers == null || initialSubscribers.isEmpty() ? Publisher.empty() : new Publisher<>(initialSubscribers);
    }

    public static <T> Publisher<T> empty(){
        return new Publisher<>(new Vector<>());
    }

    public boolean registerSubscriber(Consumer<T> subscriber){
        if (subscriber == null) return false;
        this.subscribers.add(subscriber);
        return true;
    }

    public void updateAll(T value){
        this.subscribers.forEach(subscriber -> subscriber.accept(value));
    }
}
