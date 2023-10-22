package vc;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;
import vc.api.VcApi;
import vc.command.SeenCommand;

public class VcPlugin extends Plugin {
    @Override
    public void onLoad() {
        this.getLogger().info(this.getName() + " loaded!");
        final VcApi vcApi = new VcApi(getLogger());
        //creating and registering a new command
        final SeenCommand seenCommand = new SeenCommand(vcApi);
        RusherHackAPI.getCommandManager().registerFeature(seenCommand);
    }

    @Override
    public void onUnload() {
    }

    @Override
    public String getName() {
        return "2b2t.vc";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getDescription() {
        return "2b2t Data and Statistics API";
    }

    @Override
    public String[] getAuthors() {
        return new String[]{"rfresh2"};
    }
}
