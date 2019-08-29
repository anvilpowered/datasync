package rocks.milspecsg.msdatasync.commands;

@FunctionalInterface
public interface CommandManager {

    void register(Object plugin);

}
