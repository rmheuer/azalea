package com.github.rmheuer.azalea.event;

import java.util.*;
import java.util.function.Consumer;

public final class EventBus {
    private static final class Handler<E extends Event> implements Comparable<Handler<?>> {
        private final Consumer<E> handler;
        private final EventPriority priority;

        public Handler(Consumer<E> handler, EventPriority priority) {
            this.handler = handler;
            this.priority = priority;
        }

        @Override
        public int compareTo(Handler<?> o) {
            // Comparison intentionally backwards, this makes higher priority
            // handlers happen earlier
            return Integer.compare(o.priority.getLevel(), this.priority.getLevel());
        }
    }

    public static final class HandlerSet {
        private final Map<Class<? extends Event>, List<Handler<?>>> handlers;

        public HandlerSet() {
            handlers = new HashMap<>();
        }

        public <E extends Event> void register(Class<E> type, Consumer<E> handler) {
            register(type, EventPriority.NORMAL, handler);
        }

        public <E extends Event> void register(Class<E> type, EventPriority priority, Consumer<E> handler) {
            handlers.computeIfAbsent(type, (t) -> new ArrayList<>())
                    .add(new Handler<>(handler, priority));
        }
    }

    private final Map<EventListener, HandlerSet> listenerHandlers;
    private final HandlerSet defaultHandlerSet;

    public EventBus() {
        listenerHandlers = new HashMap<>();
        defaultHandlerSet = new HandlerSet();
    }

    public <E extends Event> void dispatchEvent(E event) {
        // Collect handlers for event and all supertypes of event
        Class<?> type = event.getClass();
        List<Handler<?>> handlers = new ArrayList<>();
        while (Event.class.isAssignableFrom(type)) {
            for (HandlerSet handlerSet : listenerHandlers.values()) {
                // Put them at the beginning so superclasses get called first
                List<Handler<?>> set = handlerSet.handlers.get(type);
                if (set != null)
                    handlers.addAll(0, set);
            }

            List<Handler<?>> def = defaultHandlerSet.handlers.get(type);
            if (def != null)
                handlers.addAll(0, def);

            type = type.getSuperclass();
        }

        // Put in priority order
        handlers.sort(Comparator.naturalOrder());

        // Call them
        for (Handler<?> handler : handlers) {
            if (event.isCancelled())
                break;

            @SuppressWarnings("unchecked")
            Handler<? super E> eHandler = (Handler<? super E>) handler;

            eHandler.handler.accept(event);
        }
    }

    public void addListener(EventListener listener) {
        HandlerSet handlerSet = new HandlerSet();
        listener.registerEventHandlers(handlerSet);
        listenerHandlers.put(listener, handlerSet);
    }

    public void removeListener(EventListener listener) {
        listenerHandlers.remove(listener);
    }

    public <E extends Event> void addHandler(Class<E> type, Consumer<E> handler) {
        addHandler(type, EventPriority.NORMAL, handler);
    }

    public <E extends Event> void addHandler(Class<E> type, EventPriority priority, Consumer<E> handler) {
        defaultHandlerSet.register(type, priority, handler);
    }
}
