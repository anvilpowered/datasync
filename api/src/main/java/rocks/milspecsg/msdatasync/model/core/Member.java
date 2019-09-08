package rocks.milspecsg.msdatasync.model.core;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import rocks.milspecsg.msrepository.model.Dbo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity("members")
public class Member extends Dbo {

    public UUID userUUID;

    public List<ObjectId> snapshotIds;

}
