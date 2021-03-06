package dev.laarryy.clippyv2.commands;

import com.fasterxml.jackson.databind.JsonNode;
import de.btobastian.sdcf4j.Command;
import de.btobastian.sdcf4j.CommandExecutor;
import dev.laarryy.clippyv2.util.BStatsUtil;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.util.logging.ExceptionLogger;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GithubCommand implements CommandExecutor {

    Map<String, String> shortcuts = new HashMap<>();

    String repos = "https://api.github.com/repos/%s";
    String issuerepos = "https://api.github.com/repos/%s/issues";
    String closedIssues = "https://api.github.com/search/issues?q=repo:%s/+type:issue+state:closed";
    String openIssues = "https://api.github.com/search/issues?q=repo:%s/+type:issue+state:open";

    public GithubCommand() {
        shortcuts.put("lp", "lucko/LuckPerms");
        shortcuts.put("lperms", "lucko/LuckPerms");
        shortcuts.put("luckperms", "lucko/Luckperms");
        shortcuts.put("lpw", "lucko/LuckPermsWeb");
        shortcuts.put("lpweb", "lucko/LuckPermsWeb");
        shortcuts.put("luckpermsweb", "lucko/LuckPermsWeb");
        shortcuts.put("vcf", "lucko/VaultChatFormatter");
        shortcuts.put("vaultchatformatter", "lucko/VaultChatFormatter");
        shortcuts.put("ec", "LuckPerms/ExtraContexts");
        shortcuts.put("extracontexts", "LuckPerms/ExtraContexts");
        shortcuts.put("cookbook", "LuckPerms/api-cookbook");
        shortcuts.put("clippy", "LuckPerms/clippy");
    }

    @Command(aliases = {"!github", "!gh"}, usage = "!github <username/repo> [issue #]", description = "Shows some stats about the given repository.")
    public void onCommand(DiscordApi api, TextChannel channel, String[] args) {
        if (args.length == 1) {
            if (shortcuts.containsKey(args[0])) {
                channel.sendMessage(makeInfoEmbed(api, shortcuts.get(args[0])));
            } else {
                channel.sendMessage(makeInfoEmbed(api, args[0]));
            }
        }

        if (args.length == 2) {
            if (shortcuts.containsKey(args[0])) {
                channel.sendMessage(makeIssueEmbed(api, shortcuts.get(args[0]), args[1]));
            } else {
                channel.sendMessage(makeIssueEmbed(api, args[0], args[1]));
            }
        }

        if (args.length == 0) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setDescription("**Usage**: !github <username|repo> <issue #>")
                    .setColor(Color.GREEN);
            channel.sendMessage(embed)
                    .exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
        }
    }

    @Command(aliases = {".luck", ".lucko"}, usage = ".luck", description = "Something for Luck to brag about.")
    public void onluck(DiscordApi api, TextChannel channel, String[] args) {
        try {
            JsonNode repo = new BStatsUtil(api).makeRequest("https://api.github.com/search/issues?q=repo:lucko/LuckPerms/+type:issue+state:closed");
            channel.sendMessage(new EmbedBuilder().setColor(Color.GREEN).setTitle(String.format("LuckPerms has %s closed issues", repo.get("total_count").asText())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Command(aliases = {".issuesclosed", ".ic", "!ic", "!issuesclosed"}, usage = "!issuesclosed", description = "Shows developer commitment to a repo")
    public void onIc(DiscordApi api, TextChannel channel, String[] args) {
        if (args.length == 1 && shortcuts.containsKey(args[0].toLowerCase())) {
            try {
                JsonNode repo = new BStatsUtil(api).makeRequest(String.format(closedIssues, shortcuts.get(args[0].toLowerCase())));
                channel.sendMessage(new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle(String.format(shortcuts.get(args[0]) + " has %s closed issues", repo.get("total_count").asText()))
                        .setUrl("https://github.com/" + shortcuts.get(args[0]))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                JsonNode repo = new BStatsUtil(api).makeRequest(String.format(closedIssues, args[0]));
                channel.sendMessage(new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle(String.format(args[0] + " has %s closed issues", repo.get("total_count").asText()))
                        .setUrl("https://github.com/" + args[0])
                );
            } catch (NullPointerException ex) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Repository not found!");
                embed.setColor(new Color(0xFF0000));
                channel.sendMessage(embed);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Command(aliases = {".issuesopen", ".io", "!io", "!issuesopen"}, usage = "!issuesclosed", description = "Shows dark side of developer commitment to a repo")
    public void onIo(DiscordApi api, TextChannel channel, String[] args) {
        if (args.length == 1 && shortcuts.containsKey(args[0].toLowerCase())) {
            try {
                JsonNode repo = new BStatsUtil(api).makeRequest(String.format(openIssues, shortcuts.get(args[0].toLowerCase())));
                channel.sendMessage(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle(String.format(shortcuts.get(args[0]) + " has %s open issues", repo.get("total_count").asText()))
                        .setUrl("https://github.com/" + shortcuts.get(args[0]))
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                JsonNode repo = new BStatsUtil(api).makeRequest(String.format(openIssues, args[0]));
                channel.sendMessage(new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle(String.format(args[0] + " has %s open issues", repo.get("total_count").asText()))
                        .setUrl("https://github.com/" + args[0])
                );
            } catch (NullPointerException ex) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Repository not found!");
                embed.setColor(new Color(0xFF0000));
                channel.sendMessage(embed);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public EmbedBuilder makeInfoEmbed(DiscordApi api, String repository) {
        BStatsUtil bStatsUtil = new BStatsUtil(api);
        EmbedBuilder embed = new EmbedBuilder();
        try {
            JsonNode repo = bStatsUtil.makeRequest(String.format(repos, repository));
            JsonNode issues = bStatsUtil.makeRequest(String.format(issuerepos, repository));

            embed.setTitle(repo.get("name").asText());
            embed.setUrl(repo.get("html_url").asText());
            embed.setColor(Color.YELLOW);
            embed.setDescription(repo.get("description").asText());
            embed.setThumbnail(repo.get("owner").get("avatar_url").asText());

            embed.addInlineField("\uD83C\uDF1F Stars", String.format("```%s```", repo.get("stargazers_count").asText()));
            embed.addInlineField("\u203C Issues", String.format("```%s```", repo.get("open_issues_count").asText()));

            StringBuilder issuenames = new StringBuilder();
            for (int i = 0; i < 3; i++) {
                if (issues.has(i)) {
                    issuenames.append("[#"+issues.get(i).get("number").asText()+"]").append("("+issues.get(i).get("html_url").asText()+") ")
                            .append("```" + issues.get(i).get("title").asText() + "```");
                }
            }

            embed.addField("Current issues", issuenames.toString().isEmpty() ? "None!" : issuenames.toString());

            embed.setTimestampToNow();

            return embed;
        } catch (Exception e) {
            e.printStackTrace();
            embed.setTitle("Unknown repo!").setColor(Color.RED);
        }
        return embed;
    }

    public EmbedBuilder makeIssueEmbed(DiscordApi api, String repository, String issuenum) {
        BStatsUtil bStatsUtil = new BStatsUtil(api);
        EmbedBuilder embed = new EmbedBuilder();
        try {
            JsonNode issue = bStatsUtil.makeRequest(String.format(issuerepos, repository)+"/"+issuenum);

            embed.setAuthor(issue.get("user").get("login").asText());
            embed.setTitle(issue.get("title").asText());
            embed.setUrl(issue.get("html_url").asText());
            embed.setColor(issue.get("state").asText().equals("closed") ? Color.RED : Color.YELLOW);
            embed.setThumbnail(issue.get("user").get("avatar_url").asText());

            embed.addInlineField("Status", String.format("```%s```", issue.get("state").asText()));
            embed.addInlineField("Comments", String.format("```%s```", issue.get("comments").asText()));

            String body = issue.get("body").asText();
            int maxLength = (body.length() < 1020) ? body.length() : 1020;

            embed.addField("Issue", issue.get("body").asText().substring(0, maxLength) + " ...");

            embed.setTimestampToNow();

            return embed;
        } catch (Exception e) {
            e.printStackTrace();
            embed.setTitle("Issue not found!").setColor(Color.RED);
        }
        return embed;
    }

}
