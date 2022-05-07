package net.crossager.botutils.utils;

import com.google.gson.internal.reflect.ReflectionHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import sun.reflect.ReflectionFactory;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Utils {
    private static final Random random = new Random();
    public static MessageEmbed error(Member member, String message) {
        return error(member, message, "An error occurred");
    }
    public static MessageEmbed error(Member member, String title, String message) {
        return newCommandEmbed(member).addField(title, message, false).setColor(Color.RED).build();
    }
    public static EmbedBuilder newCommandEmbed(Member member){
        return new EmbedBuilder().setFooter(getEffectiveName(member), member.getUser().getAvatarUrl());
    }
    public static EmbedBuilder newCommandEmbed(Member member, String name){
        return new EmbedBuilder().setFooter(name, member.getUser().getAvatarUrl());
    }
    public static boolean doWithPercentChance(int percent){
        return random.nextInt(100) <= percent;
    }
    public static String getEffectiveName(Member member) {
        return member.getNickname() != null ? (member.getNickname().length() > 20 ? member.getNickname().substring(0, 20) : member.getNickname()) : member.getUser().getAsTag();
    }
    public static String[] argsOf(String s){
        String[] args = Arrays.copyOfRange(s.split(" "), 1, s.split(" ").length);
        int amount = 0;
        for (String arg : args) {
            if (!arg.isEmpty()) amount++;
        }
        String[] newArgs = new String[amount];
        int index = 0;
        for (String arg : args) {
            if (!arg.isEmpty()) {
                newArgs[index] = arg;
                index++;
            }
        }
        return newArgs;
    }
    public static long parsePing(String string){
        if (string.startsWith("<")) string = string.substring(2, string.length() - 1);
        return Long.parseLong(string);
    }
    @SafeVarargs
    public static <T> T[] asArray(T... t){
        return t;
    }
    public static String loadToken(Object obj) throws IOException {
        return new String(obj.getClass().getResourceAsStream("token.txt").readAllBytes());
    }
}
