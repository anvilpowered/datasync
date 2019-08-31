package rocks.milspecsg.msdatasync.api.tasks;

public interface SerializationTaskService {


    /**
     * Starts serialization task
     */
    void startSerializationTask();


    /**
     *
     * @return Serialization task
     */
    Runnable getSerializationTask();

}
