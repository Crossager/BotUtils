package net.crossager.botutils.commands;

import net.dv8tion.jda.api.Permission;

import java.util.Collection;
import java.util.Collections;

public interface CommandExecutor {
    String getName();
    String getDescription();
    default Collection<Permission> getPermissions(){
        return Collections.emptyList();
    }
    default boolean runAsync() {
        return false;
    }
}
