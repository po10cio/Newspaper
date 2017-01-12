package com.github.ayltai.newspaper.rss;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import android.support.annotation.NonNull;

import org.xmlpull.v1.XmlPullParserException;

import com.github.ayltai.newspaper.data.Feed;
import com.github.ayltai.newspaper.net.HttpClient;

import io.realm.RealmList;
import rx.Observable;

public final class Client implements Closeable {
    private final HttpClient client = new HttpClient();

    public Observable<Feed> get(@NonNull final String url) {
        return Observable.create(subscriber -> {
            InputStream inputStream = null;

            try {
                final RealmList<Item> items = new RealmList<>(Parser.parse(inputStream = this.client.download(url)).toArray(new Item[0]));
                Collections.sort(items);

                subscriber.onNext(new Feed(url, items));
            } catch (final XmlPullParserException | IOException e) {
                subscriber.onError(e);
            } finally {
                HttpClient.closeQuietly(inputStream);
            }
        });
    }

    @Override
    public void close() {
        this.client.close();
    }
}
