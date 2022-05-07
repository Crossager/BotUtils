package net.crossager.botutils.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.List;

public interface TextCommand extends CommandExecutor {
    void onCommand(Member sender, String command, Message message, MessageChannel channel);
    default String getUsage(){
        return getName();
    }
    default List<String> getNames(){
        return List.of(getName());
    }
}
