package fi.delektre.ringa.ring_thesis.data;

import androidx.annotation.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

public final class RxBus {
    private static PublishSubject<Object> bus = PublishSubject.create();

    public RxBus(){

    }

    public static Disposable subscribe(@NonNull Consumer<Object> action){
        return bus.subscribe(action);
    }

    public static void publish(@NonNull Object message){
        bus.onNext(message);
    }
}
