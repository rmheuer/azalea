package com.github.rmheuer.azalea.event;

import java.lang.reflect.Method;
import java.util.*;

public final class EventBus {
    private static final class HandlerFn<E extends Event> implements Comparable<HandlerFn<?>> {
        private final Listener listener;
        private final Method method;
        private final EventPriority priority;

        public HandlerFn(Listener listener, Method method, EventPriority priority) {
            this.listener = listener;
            this.method = method;
            this.priority = priority;
        }

        public void invoke(Event event) {
            try {
                method.invoke(listener, event);
            } catch (ReflectiveOperationException e) {
                System.err.println("Failed to invoke event handler " + method);
                e.printStackTrace();
            }
        }

        @Override
        public int compareTo(HandlerFn<?> o) {
            // Comparison intentionally backwards, this makes higher priority
            // handlers happen earlier
            return Integer.compare(o.priority.getLevel(), this.priority.getLevel());
        }

        @Override
        public String toString() {
            return method.getDeclaringClass().getSimpleName() + "#" + method.getName();
        }
    }

    private static final class HandlerSet<E extends Event> {
        private final List<HandlerFn<E>> handlers;

        public HandlerSet() {
            handlers = new ArrayList<>();
        }

        public void add(HandlerFn<E> handler) {
            handlers.add(handler);
        }

        public List<HandlerFn<E>> getHandlers() {
            return handlers;
        }
    }

    private final Map<Class<? extends Event>, HandlerSet<? extends Event>> handlerSets;

    public EventBus() {
        handlerSets = new HashMap<>();
    }

    /**
     * Call handlers for event type and event's superclasses.
     * Superclasses are called first, then handlers are called going down the
     * type hierarchy.
     *
     * @param type type of handler to dispatch
     * @param event event to dispatch. Must be an instance of type
     */
    private void dispatch(Class<?> type, Event event) {
        // Collect handlers for event and all supertypes of event
        List<HandlerFn<?>> handlers = new ArrayList<>();
        while (Event.class.isAssignableFrom(type)) {
            // Put them at the beginning so superclasses get called first
            HandlerSet<?> set = handlerSets.get(type);
            if (set != null)
                handlers.addAll(0, set.getHandlers());

            type = type.getSuperclass();
        }

        // Put in priority order
        handlers.sort(Comparator.naturalOrder());

        // Call them
        for (HandlerFn<?> fn : handlers) {
            if (event.isCancelled())
                break;
            fn.invoke(event);
        }
    }

    public void dispatchEvent(Event event) {
        dispatch(event.getClass(), event);
    }

    public void registerListener(Listener listener) {
        for (Method method : listener.getClass().getMethods()) {
            EventHandler annotation = method.getAnnotation(EventHandler.class);
            if (annotation == null)
                continue;

            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1 || !Event.class.isAssignableFrom(params[0])) {
                System.err.println("Invalid event listener: " + method);
                continue;
            }

            handlerSets.computeIfAbsent(params[0].asSubclass(Event.class), (t) -> new HandlerSet<>())
                    .add(new HandlerFn<>(listener, method, annotation.priority()));
        }
    }

    public void unregisterListener(Listener listener) {
        // TODO
    }
}
