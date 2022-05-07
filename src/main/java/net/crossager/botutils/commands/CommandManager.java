package net.crossager.botutils.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

public class CommandManager {
    private String prefix = null;
    private final Collection<TextCommand> textCommands = new HashSet<>();
    private final Collection<SlashCommand> slashCommands = new HashSet<>();
    private final Collection<User> admins = new ArrayList<>();
    private final JDA jda;
    private Predicate<MessageReceivedEvent> allowMessage = event -> {
        if (event.getAuthor().isBot()) return false;
        if (!event.getChannel().getName().toLowerCase().contains("bot")) return false;
        return true;
    };

    public void setAllowMessage(Predicate<MessageReceivedEvent> allowMessage) {
        this.allowMessage = allowMessage;
    }

    public CommandManager(JDA jda){
        jda.addEventListener(new Listeners());
        this.jda = jda;
    }

    public void addAdmin(User user){
        admins.add(user);
    }
    public void removeAdmin(User user){
        admins.remove(user);
    }

    public void registerTextCommand(TextCommand command){
        textCommands.add(command);
    }
    public void registerTextCommands(TextCommand... commands){
        for (TextCommand commandExecutor : commands) {
            registerTextCommand(commandExecutor);
        }
    }

    public <C extends TextCommand & SlashCommand> void registerCommand(C command) {
        registerTextCommand(command);
        registerSlashCommand(command);
    }
    @SafeVarargs
    public final <C extends TextCommand & SlashCommand> void registerCommands(C... commands){
        for (C commandExecutor : commands) {
            registerCommand(commandExecutor);
        }
    }

    public void registerSlashCommand(SlashCommand command){
        slashCommands.add(command);
        CommandDataImpl data = new CommandDataImpl(command.getName(), command.getDescription());
        data.addOptions(command.getOptions());
        for (Guild guild : jda.getGuilds()) {
            guild.updateCommands().addCommands(data).queue();
        }
    }

    public void registerSlashCommands(SlashCommand... commands){
        for (SlashCommand commandExecutor : commands) {
            registerSlashCommand(commandExecutor);
        }
    }

    public Collection<TextCommand> getTextCommands() {
        return textCommands;
    }

    public Collection<SlashCommand> getSlashCommands() {
        return slashCommands;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    private class Listeners extends ListenerAdapter {
        @Override
        public void onMessageReceived(@NotNull MessageReceivedEvent event) {
            if (!event.isFromGuild()) return;
            if (event.getAuthor() == jda.getSelfUser()) return;
            if (prefix == null) return;
            if (!event.getMessage().getContentRaw().startsWith(prefix)) return;
            if (!allowMessage.test(event)) return;

            String newLabel = event.getMessage().getContentRaw().substring(prefix.length());
            String[] labels = newLabel.split("\\s+");
            TextCommand command = getTextCommand(labels[0].toLowerCase());
            if(command == null) return;
            if (!event.getMember().hasPermission(command.getPermissions()) && !admins.contains(event.getAuthor())) return;
            if (command.runAsync())
                new Thread(command.getName() + " worker thread") {
                    @Override
                    public void run() {
                        command.onCommand(event.getMember(), newLabel, event.getMessage(), event.getChannel());
                    }
                }.start();
                else
                    command.onCommand(event.getMember(), newLabel, event.getMessage(), event.getChannel());
        }

        private TextCommand getTextCommand(String name){
            TextCommand command = null;
            for (TextCommand textCommand : textCommands) {
                if (textCommand.getNames().contains(name)) command = textCommand;
            }
            return command;
        }

        @Override
        public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
            SlashCommand slashCommand = getSlashCommand(event.getName());
            if(slashCommand == null) return;
            if (!event.getMember().hasPermission(slashCommand.getPermissions()) && !admins.contains(event.getUser())) return;
            if (slashCommand.runAsync())
                new Thread(slashCommand.getName() + " worker thread") {
                    @Override
                    public void run() {
                        slashCommand.onCommand(event);
                    }
                }.start();
            else
                slashCommand.onCommand(event);
        }
        private SlashCommand getSlashCommand(String name){
            SlashCommand slashCommand = null;
            for (SlashCommand cmd : slashCommands) {
                if (cmd.getName().equals(name)) slashCommand = cmd;
            }
            return slashCommand;
        }
    }
}
