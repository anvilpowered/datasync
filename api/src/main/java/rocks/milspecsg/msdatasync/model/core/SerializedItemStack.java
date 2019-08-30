package rocks.milspecsg.msdatasync.model.core;

import org.mongodb.morphia.annotations.Embedded;

import java.util.Map;

@Embedded
public class SerializedItemStack {

    public Map<String, Object> properties;

}
