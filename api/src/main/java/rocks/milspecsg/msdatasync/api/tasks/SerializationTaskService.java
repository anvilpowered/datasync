package rocks.milspecsg.msdatasync.api.tasks;

public interface SerializationTaskService {


    /**
     * Starts serialization task
     */
    void startSerializationTask();

    /**
     * Stops serialization task
     */
    void stopSerializationTask();

    /**
     *
     * @return Serialization task
     */
    Runnable getSerializationTask();

}
