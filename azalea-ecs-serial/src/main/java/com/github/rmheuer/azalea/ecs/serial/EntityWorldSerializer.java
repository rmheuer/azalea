package com.github.rmheuer.azalea.ecs.serial;

import com.github.rmheuer.azalea.ecs.Component;
import com.github.rmheuer.azalea.ecs.Entity;
import com.github.rmheuer.azalea.ecs.EntityWorld;
import com.github.rmheuer.azalea.serialization.graph.ArrayNode;
import com.github.rmheuer.azalea.serialization.graph.DataNode;
import com.github.rmheuer.azalea.serialization.object.ObjectSerializer;
import com.github.rmheuer.azalea.serialization.object.SerializationException;
import com.github.rmheuer.azalea.serialization.object.ValueSerializer;

import java.util.Collection;

public final class EntityWorldSerializer implements ValueSerializer<EntityWorld> {
    @Override
    public EntityWorld deserialize(ObjectSerializer serializer, DataNode node) throws SerializationException {
        EntityWorld world = new EntityWorld();

        ArrayNode arr = node.getAsArrayNode();
        for (DataNode entityNode : arr) {
            Component[] components = serializer.deserialize(entityNode, Component[].class);
            if (components == null)
                throw new SerializationException("No components");
            world.createEntity(components);
        }

        return world;
    }

    @Override
    public DataNode serialize(ObjectSerializer serializer, EntityWorld value) throws SerializationException {
        ArrayNode arr = new ArrayNode();
        for (Entity entity : value.getAllEntities()) {
            ArrayNode components = new ArrayNode();
            Collection<Component> entityComponents = entity.getAllComponents();
            for (Component component : entityComponents) {
                if (serializer.isSerializable(component.getClass())) {
                    components.add(serializer.serialize(component, Component.class));
                }
            }
            arr.add(components);
        }
        return arr;
    }
}
