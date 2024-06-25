package com.github.rmheuer.azalea.ecs;

import java.util.Collection;

public final class Entity {
    private final EntityWorld world;

    public Entity(EntityWorld world) {
        this.world = world;
    }

    public void addComponent(Component components) {
        world.addComponent(this, components);
    }

    public boolean hasComponent(Class<? extends Component> type) {
        return world.hasComponent(this, type);
    }

    public <T extends Component> T getComponent(Class<T> type) {
        return world.getComponent(this, type);
    }

    public <T extends Component> T removeComponent(Class<T> type) {
        return world.removeComponent(this, type);
    }

    public Collection<Component> getAllComponents() {
        return world.getAllComponents(this);
    }

    public void remove() {
        world.removeEntity(this);
    }
}
