package net.crossager.botutils.commands;

import net.crossager.botutils.utils.Utils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Collection;
import java.util.Collections;

public interface SlashCommand extends CommandExecutor {
    void onCommand(SlashCommandInteraction command);
    default OptionData[] getOptions() {
        return Utils.asArray();
    }
    default Collection<SubcommandData> getSubcommands() {
        return Collections.emptyList();
    }
}
