import org.bukkit.plugin.java.JavaPlugin;
// Imports
import org.bukkit.*;
import org.spigotmc.*;
%s

// Plugin class
public final class %s extends JavaPlugin implements Listener {

    // Fields
%s

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
