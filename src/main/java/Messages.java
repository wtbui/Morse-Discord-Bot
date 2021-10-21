import com.gargoylesoftware.htmlunit.BrowserVersion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.Color;
import net.dv8tion.jda.api.entities.MessageEmbed;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import java.util.*;
import java.io.IOException;

public class Messages extends ListenerAdapter {
    long mId;
    public Messages() {
        System.out.println("MoistBot is online!");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        // Get Command Message
        String[] brokeString = e.getMessage().getContentRaw().split(" ", 2);
        String opener = brokeString[0];
        EmbedBuilder eb = new EmbedBuilder();

        //Embedded Builder Settings
        eb.setTitle("DEFAULT TITLE");
        eb.setColor(Color.red);
        eb.setDescription("TEST DESCRIPTION");
        eb.addField("INLINE FIELD", "IN FIELD TEXT", true);
        eb.addBlankField(true);
        eb.addField("OUTLINE FIELD", "OUT FIELD TEXT", false);
        eb.setImage("https://preview.redd.it/buzyn25jzr761.png?width=1000&format=png&auto=webp&s=c8a55973b52a27e003269914ed1a883849ce4bdc");

        // Web Scraping
        WebClient client = new WebClient(BrowserVersion.FIREFOX);
        client.getOptions().setJavaScriptEnabled(true);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setUseInsecureSSL(true);

        // Commands
        if (opener.equals("#ping")) {
            e.getChannel().sendMessage("pong").queue();
        }

        // Stats command
        if (opener.equals("#vstats")) {
            MessageEmbed statsEmbed = eb.build();
            String valUrl;

            // Profile Object
            ValProfile profile = new ValProfile(brokeString[1], client);
            valUrl = profile.getUrl();

            try {
                try {
                    // LOADING EMBED MESSAGE
                    eb.clear();
                    eb.setAuthor("Loading stats for " + profile.getName() + "#" + profile.getTag());
                    eb.setThumbnail("https://preview.redd.it/buzyn25jzr761.png?width=1000&format=png&auto=webp&s=c8a55973b52a27e003269914ed1a883849ce4bdc");
                    statsEmbed = eb.build();
                    e.getChannel().sendMessage(statsEmbed).queue((message -> { long statsMessageId = message.getIdLong();
                        getMId(statsMessageId);
                    }));

                    // Web Scrapes for Profile Data
                    profile.initializeWebscraper(client);

                    // Embed Message
                    eb.setAuthor("Stats for " + profile.getName() + "#" + profile.getTag() + " (Click here for full breakdown)", profile.getUrl());
                    eb.setColor(new Color(245, 59, 83));
                    //eb.addField("Last 20 Games:", winsClass.asNormalizedText() + " " + lossesClass.asNormalizedText() + " " + mutedClass.get(0).asNormalizedText() + "            ", true);
                    eb.addField("Total Playtime:", profile.getPlayTime() + " (" + profile.getMatches() + ") ", true);
                    eb.addBlankField(true);
                    eb.addField("Most Played Agents:", profile.getAgentEmote1() + " " + profile.getMostPlayedAgent1() + " "
                            + profile.getAgentMatches1() + "G | " + profile.getAgentWR1() + " | " + profile.getAgentKD1() +
                            "\n" + profile.getAgentEmote2() + " " + profile.getMostPlayedAgent2()
                            + " " + profile.getAgentMatches2() + "G | " + profile.getAgentWR2() + " | " + profile.getAgentKD2() + "" +
                            "\n" + profile.getAgentEmote3() + " " + profile.getMostPlayedAgent3() + " "
                            + profile.getAgentMatches3() + "G | " + profile.getAgentWR3() + " | " + profile.getAgentKD3() + "", true);
                    eb.addField("Competitive Stats:", profile.getRankEmoji() + " " + profile.getRank() +
                            "\n" + profile.getKdEmote() + " " + profile.getKd() + " KD" +
                            "\n" + profile.getWinRateEmote() + " " + profile.getWinrate() + " Winrate", true);
                    eb.addBlankField(true);

                    //UNFINISHED FEATURE
                    eb.addField("Last Game on May 20:", null + " **13-4** | **Competitive** game as "
                            + null + " with a **17/8/4 kda**" + " on **Breeze**", true);

                    // Edits Original Embed Message
                    eb.setThumbnail("https://preview.redd.it/buzyn25jzr761.png?width=1000&format=png&auto=webp&s=c8a55973b52a27e003269914ed1a883849ce4bdc");
                    statsEmbed = eb.build();
                    e.getChannel().editMessageById(mId, statsEmbed).queue();

                } catch (FailingHttpStatusCodeException ex) {
                    // Private Account
                    e.getChannel().sendMessage("This profile is private, please register this account at " + valUrl).queue();
                }

            } catch (IOException ex) {
                // Url doesn't work
                System.out.println("NO URL FOUND");
            }
        }

        if (opener.equals("#rollcall")) {
            eb.clear();
            if (brokeString[1].equalsIgnoreCase("valorant")) {
                e.getChannel().sendMessage("@Valorant").queue();
                eb.setAuthor(e.getAuthor().getName() + " is doing roll call for Valorant",e.getAuthor().getAvatarUrl(), e.getAuthor().getAvatarUrl());
                eb.setColor(new Color(245, 59, 83));
                eb.setDescription("React below if you are planning on joining");
                eb.setThumbnail("https://preview.redd.it/buzyn25jzr761.png?width=1000&format=png&auto=webp&s=c8a55973b52a27e003269914ed1a883849ce4bdc");
            }

            if (brokeString[1].equalsIgnoreCase("league")) {
                e.getChannel().sendMessage("@League ").queue();
                eb.setAuthor(e.getAuthor().getName() + " is doing roll call for League",e.getAuthor().getAvatarUrl(), e.getAuthor().getAvatarUrl());
                eb.setColor(new Color(135, 206, 255));
                eb.setDescription("React below if you are planning on joining");
                eb.setThumbnail("https://i.imgur.com/49ojuYU.png");
            }

            e.getChannel().sendMessage(eb.build()).queue(message ->{
                eb.addField("\nAttendees", "", false);
                eb.setFooter("Roll call will end in ");
                message.editMessage(eb.build()).queue();
            });
        }

        if (opener.equals("#testfield")) {
            e.getChannel().sendMessage(eb.build()).queue();
        }

        if (e.getAuthor().isBot()) {

        }
    }

    public void getMId(long id) {
        mId = id;
    }

}
