package fi.delektre.ringa.ring_thesis.util;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

public class RetryWithDelay implements io.reactivex.functions.Function<Observable<? extends Throwable>, Observable<?>> {
    private final int maxRetries;
    private final int retryDelayMillis;
    private int retryCount;

    public RetryWithDelay(final int maxRetries, final int retryDelayMillis){
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
        this.retryCount = 0;
    }


    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) throws Exception {
        return observable
                .flatMap((Function<Throwable, Observable<?>>) throwable -> {
                    if (++retryCount < maxRetries){
                        return Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
                    }
                    return Observable.error(throwable);
                });
    }
}
