package net.leidra.tracker.backend;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by afuentes on 22/09/16.
 */
public class Broadcaster {
    private static final List<BroadcastListener> listeners = new CopyOnWriteArrayList<BroadcastListener>();

    public static void register(BroadcastListener listener) {
        listeners.add(listener);
    }

    public static void unregister(BroadcastListener listener) {
        listeners.remove(listener);
    }

    public static void broadcast(User user) {
        listeners.parallelStream().filter(l -> l.getUserName().equals(user.getUsername()))
                .forEach(listener -> listener.receiveBroadcast());
    }

    public interface BroadcastListener {
        void receiveBroadcast();
        String getUserName();
    }

}