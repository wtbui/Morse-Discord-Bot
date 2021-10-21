import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ValProfile {
    private String username;
    private String name;
    private String tag;
    private String baseUrl;
    private String rank;
    private String kd;
    private String winrate;
    private boolean compStatus;
    private String agentMatches1;
    private String agentWR1;
    private String agentKD1;
    private String agentMatches2;
    private String agentWR2;
    private String agentKD2;
    private String agentMatches3;
    private String agentWR3;
    private String agentKD3;
    private String playTime;
    private String rankEmoji;
    private String mostPlayedAgent1;
    private String mostPlayedAgent2;
    private String mostPlayedAgent3;
    private String kdEmote;
    private String winRateEmote;
    private String agentEmote1;
    private String agentEmote2;
    private String agentEmote3;
    private String matches;

    public ValProfile(String username, WebClient client) {
        this.username = username;

        // Parses Username
        parseUsername(this.username);

        // Parses Url
        parseUrl();
    }

    private void parseUsername(String username) {
        String[] valUser = username.split("#", 2);
        name = valUser[0];
        tag = valUser[1];
    }

    private void parseUrl() {
        String urlName = name;

        if (name.contains(" ")) {
            urlName = name.replace(" ", "%20");
        }

        baseUrl = "https://tracker.gg/valorant/profile/riot/" + urlName + "%23" + tag + "/overview";
    }

    // Gets HTML Elements from Tracker Site
    public void initializeWebscraper(WebClient client) throws IOException, FailingHttpStatusCodeException {
        HtmlPage page = client.getPage(baseUrl);
        client.waitForBackgroundJavaScript(10000);

        // Get stats
        List<HtmlElement> HtmlMostPlayedAgents = page.getByXPath("//span[@class='agent__name']");
        List<HtmlElement> valueClass = page.getByXPath("//span[@class='value']");
        List<HtmlElement> highlightedStat = page.getByXPath("//span[@class='valorant-highlighted-stat__value']");
        List<HtmlElement> nameClass = page.getByXPath("//span[@class='name']");
        HtmlElement HtmlPlaytime = page.getFirstByXPath("//span[@class='playtime']");
        HtmlElement HtmlMatches = page.getFirstByXPath("//span[@class='matches']");
        HtmlElement HtmlWinsClass = page.getFirstByXPath("//span[@class='wins']");
        HtmlElement HtmlLossesClass = page.getFirstByXPath("//span[@class='losses']");
        List<HtmlElement> HtmlMutedClass = page.getByXPath("//span[@class='muted']");

        // Gets Winrate, Initializes Rank and KD HTML ELEMENTS
        HtmlElement HtmlWinrate = valueClass.get(6);
        HtmlElement HtmlRank = null;
        HtmlElement HtmlKd = null;

        // Checks if stats are empty, else gets rank and kd.
        if (highlightedStat.isEmpty()) {
            compStatus = false;
        } else {
            HtmlRank = highlightedStat.get(0);
            HtmlKd = highlightedStat.get(1);
        }

        // Agent Stats
        agentMatches1 = nameClass.get(25).asNormalizedText();
        agentWR1 = nameClass.get(26).asNormalizedText();
        agentKD1 = nameClass.get(27).asNormalizedText();
        agentMatches2 = nameClass.get(30).asNormalizedText();
        agentWR2 = nameClass.get(31).asNormalizedText();
        agentKD2 = nameClass.get(32).asNormalizedText();
        agentMatches3 = nameClass.get(35).asNormalizedText();
        agentWR3 = nameClass.get(36).asNormalizedText();
        agentKD3 = nameClass.get(37).asNormalizedText();
        mostPlayedAgent1 = HtmlMostPlayedAgents.get(0).asNormalizedText();
        mostPlayedAgent2 = HtmlMostPlayedAgents.get(1).asNormalizedText();
        mostPlayedAgent3 = HtmlMostPlayedAgents.get(2).asNormalizedText();
        matches = HtmlMatches.asNormalizedText();

        // Fix Playtime
        playTime = HtmlPlaytime.asNormalizedText().replace("Play Time", "");

        // Get Rank
        rank = HtmlRank.asNormalizedText();
        kd = HtmlKd.asNormalizedText();
        winrate = HtmlWinrate.asNormalizedText();

        // Get Emojis
        getEmojis();

    }

    // Gets Emoji Ids
    public void getEmojis() {
        // Emoji Map
        HashMap<String, String> emojiMap = new HashMap<String, String>();
        emojiMap.put("iron1", "<:iron1:845205668541104128>");
        emojiMap.put("iron2", "<:iron2:845205668398497813>");
        emojiMap.put("iron3", "<:iron3:845205668713201665>");
        emojiMap.put("bronze1", "<:bronze1:845205668993433622>");
        emojiMap.put("bronze2", "<:bronze2:845205670084739072>");
        emojiMap.put("bronze3", "<:bronze3:845205669899927553>");
        emojiMap.put("silver1", "<:silver1:845205669300011019>");
        emojiMap.put("silver2", "<:silver2:845205670100860988>");
        emojiMap.put("silver3", "<:silver3:845205670106365962>");
        emojiMap.put("gold1", "<:gold1:845205669790875658>");
        emojiMap.put("gold2", "<:gold2:845205670058917898>");
        emojiMap.put("gold3", "<:gold3:845205669691129868>");
        emojiMap.put("plat1", "<:plat1:845205669568577567>");
        emojiMap.put("plat2", "<:plat2:845205669962973235>");
        emojiMap.put("plat3", "<:plat3:845205670139265024>");
        emojiMap.put("diamond1", "<:diamond1:845205669769904168>");
        emojiMap.put("diamond2", "<:diamond2:845205670030737449>");
        emojiMap.put("diamond3", "<:diamond3:845205670420938772>");
        emojiMap.put("radiant", "<:radiant:845205670365757460>");
        emojiMap.put("immortal", "<:immortal:845205670319226880>");
        emojiMap.put("bad", "<:poopy:845240577679425558>");
        emojiMap.put("crowny", "<:crowny:845241528771149854>");
        emojiMap.put("redcircle", "<:redcircle:845242344067301396>");
        emojiMap.put("greencircle", "<:greencircle:845242343975419934>");
        emojiMap.put("reyna", "<:reyna:845248264788901889>");
        emojiMap.put("omen", "<:omen:845248266216013855>");
        emojiMap.put("breach", "<:breach:845248267251613716>");
        emojiMap.put("raze", "<:raze:845248267394613268>");
        emojiMap.put("jett", "<:jett:845248266744234015>");
        emojiMap.put("sova", "<:sova:845248267301945354>");
        emojiMap.put("sage", "<:sage:845248267344150539>");
        emojiMap.put("cypher", "<:cypher:845248267049631766>");
        emojiMap.put("skye", "<:skye:845248267683758080>");
        emojiMap.put("brimstone", "<:brimstone:845248267226447882>");
        emojiMap.put("astra", "<:astra:845248267431444500>");
        emojiMap.put("killjoy", "<:killjoy:845248267201806346>");
        emojiMap.put("viper", "<:viper:845248267046748191>");
        emojiMap.put("yoru", "<:yoru:845248267432493086>");
        emojiMap.put("phoenix", "<:pheonix:845248267129192448>");
        emojiMap.put("kay/o", "<:kayo:900630792743575572>");

        // Finding Rank Emoji
        String rankEmojiKey = "";
        if (rank.contains("Iron")) {
            rankEmojiKey = "iron";
        } else if (rank.contains("Bronze")) {
            rankEmojiKey = "bronze";
        } else if (rank.contains("Silver")) {
            rankEmojiKey = "silver";
        } else if (rank.contains("Gold")) {
            rankEmojiKey = "gold";
        } else if (rank.contains("Plat")) {
            rankEmojiKey = "plat";
        } else if (rank.contains("Diamond")) {
            rankEmojiKey = "diamond";
        } else if (rank.contains("Immortal")) {
            rankEmojiKey = "immortal";
        } else if (rank.contains("Radiant")) {
            rankEmojiKey = "radiant";
        }

        if (!rank.contains("Immo") || !rank.contains("Radia")) {
            if (rank.contains("1")) {
                rankEmojiKey = rankEmojiKey + "1";
            } else if (rank.contains("2")) {
                rankEmojiKey = rankEmojiKey + "2";
            } else {
                rankEmojiKey = rankEmojiKey + "3";
            }
        }

        rankEmoji = emojiMap.get(rankEmojiKey);

        // Agent Emotes
        agentEmote1 = emojiMap.get(mostPlayedAgent1.toLowerCase());
        agentEmote2 = emojiMap.get(mostPlayedAgent2.toLowerCase());
        agentEmote3 = emojiMap.get(mostPlayedAgent3.toLowerCase());

        // Other Emotes
        kdEmote = emojiMap.get("crowny");
        if (Double.parseDouble(kd) < 1.0) {
            kdEmote = emojiMap.get("bad");
        }

        winRateEmote = emojiMap.get("greencircle");
        if (Double.parseDouble(winrate.replace("%", "")) < 50) {
            winRateEmote = emojiMap.get("redcircle");
        }
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getTag() {
        return tag;
    }

    public String getUrl() {
        return baseUrl;
    }

    public String getRank() {
        return rank;
    }

    public String getKd() {
        return kd;
    }

    public String getWinrate() {
        return winrate;
    }

    public boolean isCompStatus() {
        return compStatus;
    }

    public String getAgentMatches1() {
        return agentMatches1;
    }

    public String getAgentWR1() {
        return agentWR1;
    }

    public String getAgentKD1() {
        return agentKD1;
    }

    public String getAgentMatches2() {
        return agentMatches2;
    }

    public String getAgentWR2() {
        return agentWR2;
    }

    public String getAgentKD2() {
        return agentKD2;
    }

    public String getAgentMatches3() {
        return agentMatches3;
    }

    public String getAgentWR3() {
        return agentWR3;
    }

    public String getAgentKD3() {
        return agentKD3;
    }

    public String getPlayTime() {
        return playTime;
    }

    public String getRankEmoji() {
        return rankEmoji;
    }

    public String getMostPlayedAgent1() {
        return mostPlayedAgent1;
    }

    public String getMostPlayedAgent2() {
        return mostPlayedAgent2;
    }

    public String getMostPlayedAgent3() {
        return mostPlayedAgent3;
    }

    public String getKdEmote() {
        return kdEmote;
    }

    public String getWinRateEmote() {
        return winRateEmote;
    }

    public String getMatches() {
        return matches;
    }

    public String getAgentEmote1() {
        return agentEmote1;
    }

    public String getAgentEmote2() {
        return agentEmote2;
    }

    public String getAgentEmote3() {
        return agentEmote3;
    }
}


