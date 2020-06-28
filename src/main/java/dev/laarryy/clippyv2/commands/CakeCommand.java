package dev.laarryy.clippyv2.commands;

import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.clippyv2.Constants;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class CakeCommand implements CommandExecutor{

    List<String> responses = new ArrayList<>();
    String img = "https://i.imgur.com/253KTjq.gif";

    public CakeCommand() {
        try {
            File file = new File("data/cake_responses.txt");
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                responses.add(sc.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Command(aliases = {"!cake"}, usage = "!cake", description = "Maybe some cake?")
    public void onCommand(DiscordApi api, TextChannel channel, User user, String[] args) {
        if (!(channel.getIdAsString().equals(Constants.CHANNEL_OFFTOPIC) || channel.getIdAsString().equals(Constants.CHANNEL_PATREONS) || channel.getIdAsString().equals(Constants.CHANNEL_HELPFUL)))
            return;
        if (args.length == 13) {
            channel.sendMessage(user.getMentionTag() + " 13 Ingredients? No thank you!");
            return;
        }
        channel.sendMessage(new EmbedBuilder().setTitle("What's the cake?").setImage(img)).thenAcceptAsync(message -> {
            String[] answer = responses.get(ThreadLocalRandom.current().nextInt(responses.size() -1)).split("\\|");
            EmbedBuilder builder = new EmbedBuilder().setColor(Color.decode(answer[0])).setImage(answer[1]);
            message.getApi().getThreadPool().getScheduler().schedule(() -> message.edit(user.getMentionTag(), builder),4, TimeUnit.SECONDS);
        });

    }




}