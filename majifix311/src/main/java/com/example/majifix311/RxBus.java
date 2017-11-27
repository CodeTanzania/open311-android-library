package com.example.majifix311;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Dave - Work on 11/27/2017.
 */

class RxBus {
    private static RxBus sInstance;
    private HashMap<String, PublishSubject<Object>> mRouter = new HashMap<>(10);

    private RxBus(){}

    public static RxBus getInstance() {
        if (sInstance == null) {
            sInstance = new RxBus();
        }
        return sInstance;
    }

    public Observable<Object> getStream(String key){
        if (mRouter.containsKey(key)) {
            return mRouter.get(key);
        } else {
            return mRouter.put(key, PublishSubject.create());
        }
    }

    public boolean publishStream(String key, Observable<?> stream){
        PublishSubject<Object> subject = mRouter.get(key);
        boolean wasNew = false;
        if (subject == null){
            subject = PublishSubject.create();
            mRouter.put(key, subject);
            wasNew = true;
        }
        stream.subscribe(subject);
        return wasNew;
    }

    public boolean publishObject(String key, Object object){
        PublishSubject<Object> subject = mRouter.get(key);
        boolean wasNew = false;
        if (subject == null){
            subject = PublishSubject.create();
            mRouter.put(key, subject);
            wasNew = true;
        }
        subject.onNext(object);
        return wasNew;
    }
}
