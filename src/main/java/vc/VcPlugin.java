package vc;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;
import vc.api.VcApi;
import vc.command.PlaytimeCommand;
import vc.command.QueueCommand;
import vc.command.SeenCommand;
import vc.command.StatsCommand;
import vc.hud.Queue2b2tHudElement;

public class VcPlugin extends Plugin {
    @Override
    public void onLoad() {
        final VcApi api = new VcApi(getLogger());
        RusherHackAPI.getCommandManager().registerFeature(new SeenCommand(api));
        RusherHackAPI.getCommandManager().registerFeature(new PlaytimeCommand(api));
        RusherHackAPI.getCommandManager().registerFeature(new QueueCommand(api));
        RusherHackAPI.getCommandManager().registerFeature(new StatsCommand(api));
        final Queue2b2tHudElement queueHud = new Queue2b2tHudElement(api);
        RusherHackAPI.getHudManager().registerFeature(queueHud);

        this.getLogger().info(this.getName() + " loaded!");
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
        return "1.1";
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
