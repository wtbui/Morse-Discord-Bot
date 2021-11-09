import com.gargoylesoftware.htmlunit.BrowserVersion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.Color;
import net.dv8tion.jda.api.entities.MessageEmbed;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import java.io.IOException;

public class Messages extends ListenerAdapter {
    long mId;

    public Messages() {
        System.out.println("MorseBot is online!");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        // Get Command Message
        String[] brokeString = e.getMessage().getContentRaw().split(" ");
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
        WebClient client = new WebClient(BrowserVersion.BEST_SUPPORTED);
        client.getOptions().setJavaScriptEnabled(true);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setUseInsecureSSL(true);

        // Commands
        try {
            if (opener.equals("#vstats")) {
                String reforgedUsername;
                MessageEmbed statsEmbed = eb.build();
                String valUrl;
                ValProfile profile = new ValProfile();

                // Profile Object
                if (!brokeString[1].equalsIgnoreCase("all") && !brokeString[1].equalsIgnoreCase("current")) {
                    throw new InvalidVStatsException();
                }

                reforgedUsername = brokeString[2];
                for (int i = 3; i < brokeString.length; i++) {
                    reforgedUsername = reforgedUsername + " " + brokeString[i];
                }

                if (brokeString[1].equalsIgnoreCase("all")) {
                    profile = new ValProfileAll(reforgedUsername);
                } else if (brokeString[1].equalsIgnoreCase("current")) {
                    profile = new ValProfile(reforgedUsername);
                } else {

                }

                valUrl = profile.getUrl();
                // LOADING EMBED MESSAGE
                eb.clear();
                eb.setAuthor("Loading Stats for " + profile.getName() + "#" + profile.getTag());
                eb.setThumbnail("https://preview.redd.it/buzyn25jzr761.png?width=1000&format=png&auto=webp&s=c8a55973b52a27e003269914ed1a883849ce4bdc");
                statsEmbed = eb.build();
                e.getChannel().sendMessage(statsEmbed).queue((message -> {
                    long statsMessageId = message.getIdLong();
                    getMId(statsMessageId);
                }));

                try {
                    try {
                        // Web Scrapes for Profile Data
                        profile.initializeWebscraper(client);

                        // Edits Original Embed Message
                        eb.setAuthor(profile.getSeason() + " Stats for " + profile.getName() + "#" + profile.getTag() + " (Click Here for Full Breakdown)", profile.getUrl());
                        eb.setColor(new Color(245, 59, 83));
                        //eb.addField("Last 20 Games:", winsClass.asNormalizedText() + " " + lossesClass.asNormalizedText() + " " + mutedClass.get(0).asNormalizedText() + "            ", true);
                        eb.addField("Total Playtime:", profile.getPlayTime() + " (" + profile.getMatches() + ") ", true);
                        eb.addBlankField(true);
                        eb.addField("Most Played Agents(G, WR, KD):", profile.getAgentEmote1() + " " + profile.getMostPlayedAgent1() + " "
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
                        eb.addField("Last Game on " + profile.getLastGameDay(), null + " **" + profile.getLastGameScore() + "** " + " | **Competitive** game as "
                                + null + " with a ** " + profile.getLastGameKd() + " kda**" + " on **" + profile.getLastGameMap() + "**", true);

                        // Adds Icon
                        eb.setThumbnail(profile.getIconUrl());

                        // Builds Embed
                        statsEmbed = eb.build();
                        e.getChannel().editMessageById(mId, statsEmbed).queue();

                    } catch (FailingHttpStatusCodeException ex) {
                        // Private Account
                        eb.clear();
                        eb.setAuthor("This Profile is Private or Does Not Exist(Click Here to Register Account)", valUrl);
                        eb.setThumbnail("https://preview.redd.it/buzyn25jzr761.png?width=1000&format=png&auto=webp&s=c8a55973b52a27e003269914ed1a883849ce4bdc");
                        statsEmbed = eb.build();
                        e.getChannel().editMessageById(mId, statsEmbed).queue();
                    } catch (NoCompStatsException ex) {
                        // Private Account
                        eb.clear();
                        eb.setAuthor(profile.getName() + profile.getTag() + " Does Not Have Any Competitive Stats!", valUrl);
                        eb.setThumbnail("https://preview.redd.it/buzyn25jzr761.png?width=1000&format=png&auto=webp&s=c8a55973b52a27e003269914ed1a883849ce4bdc");
                        statsEmbed = eb.build();
                        e.getChannel().editMessageById(mId, statsEmbed).queue();
                    }

                } catch (IOException ex) {
                    // Url doesn't work
                    System.out.println("NO URL FOUND");
                }
            }

            if (opener.equals("#vgstats")) {
                MessageEmbed statsEmbed = eb.build();
                String valUrl;
                ValGunProfile profile = new ValGunProfile(brokeString[1]);

                valUrl = profile.getUrl();
                // LOADING EMBED MESSAGE
                eb.clear();
                eb.setAuthor("Loading Gun Stats for " + profile.getName() + "#" + profile.getTag());
                eb.setThumbnail("https://preview.redd.it/buzyn25jzr761.png?width=1000&format=png&auto=webp&s=c8a55973b52a27e003269914ed1a883849ce4bdc");
                statsEmbed = eb.build();
                e.getChannel().sendMessage(statsEmbed).queue((message -> {
                    long statsMessageId = message.getIdLong();
                    getMId(statsMessageId);
                }));

                try {
                    try {
                        // Web Scrapes for Profile Data
                        profile.initializeWebscraper(client);

                        eb.setAuthor("Weapon Stats for " + profile.getName() + "#" + profile.getTag() + " (Click Here for Full Breakdown)", profile.getUrl());
                        eb.setColor(new Color(245, 59, 83));
                        eb.addField("Top Gun: " + profile.getTopName() + "  " + profile.getTopEmote(), profile.getTopK() + " ("
                                + profile.getTopHsp() + " Headshot %) Kills", true);
                        eb.addField(profile.getMeleeK() + " Melee Kills  " + profile.getMeleeEmote(), "", true);
                        eb.addField("Best Rifle: " + profile.getBestRifle() + "  " + profile.getRifleEmote(), profile.getRifleK() + " Kills - "
                                + profile.getRifleHs() + " (" + profile.getRifleHsp() + ")" + " Headshots - "
                                + profile.getRifleLongest() + "m Longest Kill", false);
                        eb.addField("Best Heavy: " + profile.getBestHeavy() + "  " + profile.getHeavyEmote(), profile.getHeavyK() + " Kills - "
                                + profile.getHeavyHs() + " (" + profile.getHeavyHsp() + ")" + " Headshots - "
                                + profile.getHeavyLongest() + "m Longest Kill", false);
                        eb.addField("Best Eco: " + profile.getBestEco() + "  " + profile.getEcoEmote(), profile.getEcoK() + " Kills - "
                                + profile.getEcoHs() + " (" + profile.getEcoHsp() + ")" + " Headshots - "
                                + profile.getEcoLongest() + "m Longest Kill", false);
                        eb.addField("Best Sidearm: " + profile.getBestSidearm() + "  " + profile.getSidearmEmote(), profile.getSidearmK() + " Kills - "
                                + profile.getSidearmHs() + " (" + profile.getSidearmHsp() + ")" + " Headshots - "
                                + profile.getSidearmLongest() + "m Longest Kill", false);

                        // Adds Icon
                        eb.setThumbnail(profile.getIconUrl());

                        // Builds Embed
                        statsEmbed = eb.build();
                        e.getChannel().editMessageById(mId, statsEmbed).queue();

                    } catch (FailingHttpStatusCodeException ex) {
                        // Private Account
                        eb.clear();
                        eb.setAuthor("This Profile is Private or Does Not Exist(Click Here to Register Account)", valUrl);
                        eb.setThumbnail("https://preview.redd.it/buzyn25jzr761.png?width=1000&format=png&auto=webp&s=c8a55973b52a27e003269914ed1a883849ce4bdc");
                        statsEmbed = eb.build();
                        e.getChannel().editMessageById(mId, statsEmbed).queue();
                    } catch (NoCompStatsException ex) {
                        // No Stats
                        eb.clear();
                        eb.setAuthor(profile.getName() + profile.getTag() + " Does Not Have Any Competitive Stats!", valUrl);
                        eb.setThumbnail("https://preview.redd.it/buzyn25jzr761.png?width=1000&format=png&auto=webp&s=c8a55973b52a27e003269914ed1a883849ce4bdc");
                        statsEmbed = eb.build();
                        e.getChannel().editMessageById(mId, statsEmbed).queue();
                    }

                } catch (IOException ex) {
                    // Url doesn't work
                    System.out.println("NO URL FOUND");
                }
            }
        } catch (InvalidVStatsException ex) {
            eb.clear();
            eb.setAuthor("Oops! Invalid Valorant Command Usage");
            eb.setColor(new Color(245, 59, 83));
            eb.setThumbnail("https://preview.redd.it/buzyn25jzr761.png?width=1000&format=png&auto=webp&s=c8a55973b52a27e003269914ed1a883849ce4bdc");
            eb.addField("To Get Current Act Stats, Use This: ", "``#vstats current NAME#TAG``", false);
            eb.addField("To Get All Act Stats, Use This: ", "``#vstats all NAME#TAG``", false);
            eb.addField("To Get Gun Stats, Use This: ", "``#vgstats NAME#TAG``", false);
            eb.addField("To Get Last Game Stats Use This: ", "``#vlgstats NAME#TAG``", false);
            e.getChannel().sendMessage(eb.build()).queue();
        }
    }

    public void getMId(long id) {
        mId = id;
    }

}
