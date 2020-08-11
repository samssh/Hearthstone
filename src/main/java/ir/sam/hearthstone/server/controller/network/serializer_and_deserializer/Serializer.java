package ir.sam.hearthstone.server.controller.network.serializer_and_deserializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import static ir.sam.hearthstone.server.controller.network.serializer_and_deserializer.SerializerAndDeserializerConstants.CLASSNAME;
import static ir.sam.hearthstone.server.controller.network.serializer_and_deserializer.SerializerAndDeserializerConstants.INSTANCE;

public class Serializer<T> implements JsonSerializer<T> {

    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject retValue = new JsonObject();
        String className = src.getClass().getSimpleName();
        retValue.addProperty(CLASSNAME, className);
        JsonElement elem = context.serialize(src);
        retValue.add(INSTANCE, elem);
        return retValue;
    }
}