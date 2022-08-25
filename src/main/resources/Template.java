import org.bukkit.plugin.java.JavaPlugin;
// Imports
%s

// Plugin class
public final class %s extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
%s
    }

    // Listeners
%s

    // Methods
%s

}
