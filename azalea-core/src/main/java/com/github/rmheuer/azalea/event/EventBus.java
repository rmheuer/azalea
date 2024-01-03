package com.github.rmheuer.azalea.event;

import java.lang.reflect.Method;
import java.util.*;

/**
 * The system to dispatch events to listeners.
 */
public final class EventBus {
    private static final class HandlerFn implements Comparable<HandlerFn> {
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
        public int compareTo(HandlerFn o) {
            // Comparison intentionally backwards, this makes higher priority
            // handlers happen earlier
            return Integer.compare(o.priority.getLevel(), this.priority.getLevel());
        }

        @Override
        public String toString() {
            return method.getDeclaringClass().getSimpleName() + "#" + method.getName();
        }
    }

    private static final class HandlerSet {
        private final List<HandlerFn> handlers;

        public HandlerSet() {
            handlers = new ArrayList<>();
        }

        public void add(HandlerFn handler) {
            handlers.add(handler);
        }

        public List<HandlerFn> getHandlers() {
            return handlers;
        }
    }

    private final Map<Class<? extends Event>, HandlerSet> handlerSets;

    public EventBus() {
        handlerSets = new HashMap<>();
    }

    private void dispatch(Class<?> type, Event event) {
        // Collect handlers for event and all supertypes of event
        List<HandlerFn> handlers = new ArrayList<>();
        while (Event.class.isAssignableFrom(type)) {
            // Put them at the beginning so superclasses get called first
            HandlerSet set = handlerSets.get(type);
            if (set != null)
                handlers.addAll(0, set.getHandlers());

            type = type.getSuperclass();
        }

        // Put in priority order
        handlers.sort(Comparator.naturalOrder());

        // Call them
        for (HandlerFn fn : handlers) {
            if (event.isCancelled())
                break;
            fn.invoke(event);
        }
    }

    /**
     * Call handlers for an event and its superclasses. Superclasses are called
     * first, then handlers are called going down the type hierarchy.
     *
     * @param event event to dispatch
     */
    public void dispatchEvent(Event event) {
        dispatch(event.getClass(), event);
    }

    /**
     * Registers a listener to receive events. All methods in the listener
     * annotated with {@link EventHandler} are registered to handle events.
     *
     * @param listener listener to register
     */
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

            handlerSets.computeIfAbsent(params[0].asSubclass(Event.class), (t) -> new HandlerSet())
                    .add(new HandlerFn(listener, method, annotation.priority()));
        }
    }

    /**
     * Unregisters a listener from receiving events. After unregistering, it
     * will no longer receive events.
     *
     * @param listener listener to unregister
     */
    public void unregisterListener(Listener listener) {
        // TODO
    }
}
