package com.github.rmheuer.azalea.event;

import java.util.*;
import java.util.function.Consumer;

/**
 * The system to dispatch events to listeners.
 */
public final class EventBus {
    private static final class Listener<E extends Event> implements Comparable<Listener<?>> {
        final Consumer<E> listener;
        final EventPriority priority;

        public Listener(Consumer<E> listener, EventPriority priority) {
            this.listener = listener;
            this.priority = priority;
        }

        @Override
        public int compareTo(Listener<?> o) {
            // Comparison intentionally backwards, this makes higher priority
            // handlers happen earlier
            return Integer.compare(o.priority.getLevel(), this.priority.getLevel());
        }
    }

    private final Map<Class<? extends Event>, List<Listener<? extends Event>>> listeners;

    public EventBus() {
        listeners = new HashMap<>();
    }

    /**
     * Call handlers for an event and its superclasses. Superclasses are called
     * first, then handlers are called going down the type hierarchy.
     *
     * @param event event to dispatch
     */
    public <E extends Event> void dispatchEvent(E event) {
        // Collect handlers for event and all supertypes of event
        Class<?> type = event.getClass();
        List<Listener<?>> handlers = new ArrayList<>();
        while (Event.class.isAssignableFrom(type)) {
            // Put them at the beginning so superclasses get called first
            List<Listener<?>> set = listeners.get(type);
            if (set != null)
                handlers.addAll(0, set);

            type = type.getSuperclass();
        }

        // Put in priority order
        handlers.sort(Comparator.naturalOrder());

        // Call them
        for (Listener<?> listener : handlers) {
            if (event.isCancelled())
                break;

            // Safety: listeners maps from event type to listeners of that type
            @SuppressWarnings("unchecked")
            Listener<? super E> eListener = (Listener<? super E>) listener;

            eListener.listener.accept(event);
        }
    }

    /**
     * Registers a listener to receive events with
     * {@link EventPriority#NORMAL} priority. The handler will be called for
     * events of the specified type or of subclasses of that type.
     *
     * @param type type of event to listen for
     * @param handler listener to register
     */
    public <E extends Event> void addListener(Class<E> type, Consumer<E> handler) {
        addListener(type, EventPriority.NORMAL, handler);
    }

    /**
     * Registers a listener to receive events with the specified priority. The
     * handler will be called for events of the specified type or of subclasses
     * of that type.
     *
     * @param type type of event to listen for
     * @param priority priority of the listener
     * @param handler listener to register
     */
    public <E extends Event> void addListener(Class<E> type, EventPriority priority, Consumer<E> handler) {
        listeners.computeIfAbsent(type, (t) -> new ArrayList<>())
                .add(new Listener<>(handler, priority));
    }
}
