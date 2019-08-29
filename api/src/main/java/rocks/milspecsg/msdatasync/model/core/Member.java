package rocks.milspecsg.msdatasync.model.core;

import org.mongodb.morphia.annotations.Entity;
import rocks.milspecsg.msrepository.model.Dbo;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

@Entity("members")
public class Member extends Dbo {

    public UUID userUUID;

    public ConcurrentMap<String, Object> keys;

}
