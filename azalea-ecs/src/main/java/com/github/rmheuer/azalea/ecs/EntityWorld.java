package com.github.rmheuer.azalea.ecs;

import java.util.*;

public final class EntityWorld {
    // Currently the bare minimum required - this could be made much more performant
    private final Map<Class<? extends Component>, Map<Entity, Component>> components =
            new HashMap<>();

    public Entity createEntity(Component... components) {
        Entity entity = new Entity(this);
        for (Component component : components) {
            entity.addComponent(component);
        }
        return entity;
    }

    public Collection<Entity> getAllEntities() {
        Set<Entity> entities = new HashSet<>();
        for (Map<Entity, Component> componentMap : components.values()) {
            entities.addAll(componentMap.keySet());
        }
        return entities;
    }

    void removeEntity(Entity entity) {
        for (Map<Entity, Component> componentMap : components.values()) {
            componentMap.remove(entity);
        }
    }

    void addComponent(Entity entity, Component component) {
        Map<Entity, Component> componentMap =
                components.computeIfAbsent(component.getClass(), (k) -> new HashMap<>());
        componentMap.put(entity, component);
    }

    <T extends Component> T removeComponent(Entity entity, Class<T> componentType) {
        Map<Entity, Component> componentMap = components.get(componentType);
        if (componentMap == null) return null;

        @SuppressWarnings("unchecked")
        T component = (T) componentMap.remove(entity);
        return component;
    }

    boolean hasComponent(Entity entity, Class<? extends Component> componentType) {
        return getComponent(entity, componentType) != null;
    }

    <T extends Component> T getComponent(Entity entity, Class<T> componentType) {
        Map<Entity, Component> componentMap = components.get(componentType);
        if (componentMap == null) return null;

        @SuppressWarnings("unchecked")
        T component = (T) componentMap.get(entity);
        return component;
    }

    public Collection<Component> getAllComponents(Entity entity) {
        List<Component> components = new ArrayList<>();
        for (Map<Entity, Component> componentMap : this.components.values()) {
            Component c = componentMap.get(entity);
            if (c != null)
                components.add(c);
        }
        return components;
    }

    @FunctionalInterface
    public interface ForEach1<C1> {
        void accept(Entity entity, C1 c1);
    }

    @FunctionalInterface
    public interface ForEach2<C1, C2> {
        void accept(Entity entity, C1 c1, C2 c2);
    }

    @FunctionalInterface
    public interface ForEach3<C1, C2, C3> {
        void accept(Entity entity, C1 c1, C2 c2, C3 c3);
    }

    @FunctionalInterface
    public interface ForEach4<C1, C2, C3, C4> {
        void accept(Entity entity, C1 c1, C2 c2, C3 c3, C4 c4);
    }

    @SafeVarargs
    private final Set<Entity> getEntitiesWith(
            Class<? extends Component> first, Class<? extends Component>... rest) {
        Map<Entity, Component> firstMap = components.get(first);
        if (firstMap == null) return Collections.emptySet();

        Set<Entity> entities = new HashSet<>(firstMap.keySet());
        for (Class<? extends Component> type : rest) {
            entities.removeIf((e) -> !hasComponent(e, type));
            if (entities.isEmpty()) break;
        }

        return entities;
    }

    public <C1 extends Component> void forEachWith(Class<C1> c1, ForEach1<C1> fn) {
        for (Entity e : getEntitiesWith(c1)) {
            fn.accept(e, e.getComponent(c1));
        }
    }

    public <C1 extends Component, C2 extends Component> void forEachWith(
            Class<C1> c1, Class<C2> c2, ForEach2<C1, C2> fn) {
        for (Entity e : getEntitiesWith(c1, c2)) {
            fn.accept(e, e.getComponent(c1), e.getComponent(c2));
        }
    }

    public <C1 extends Component, C2 extends Component, C3 extends Component> void forEachWith(
            Class<C1> c1, Class<C2> c2, Class<C3> c3, ForEach3<C1, C2, C3> fn) {
        for (Entity e : getEntitiesWith(c1, c2, c3)) {
            fn.accept(e, e.getComponent(c1), e.getComponent(c2), e.getComponent(c3));
        }
    }

    public <C1 extends Component, C2 extends Component, C3 extends Component, C4 extends Component>
            void forEachWith(
                    Class<C1> c1,
                    Class<C2> c2,
                    Class<C3> c3,
                    Class<C4> c4,
                    ForEach4<C1, C2, C3, C4> fn) {
        for (Entity e : getEntitiesWith(c1, c2, c3, c4)) {
            fn.accept(
                    e,
                    e.getComponent(c1),
                    e.getComponent(c2),
                    e.getComponent(c3),
                    e.getComponent(c4));
        }
    }
}
