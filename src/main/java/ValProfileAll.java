import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.List;

public class ValProfileAll extends ValProfile {
    public ValProfileAll(String username) {
        this.username = username;

        // Parses Username
        parseUsername(this.username);
        this.season = "All Act";

        // Parses Url
        parseUrl();
    }

    protected void parseUrl() {
        String urlName = name;

        if (name.contains(" ")) {
            urlName = name.replace(" ", "%20");
        }

        baseUrl = "https://tracker.gg/valorant/profile/riot/" + urlName + "%23" + tag + "/overview?season=all";
    }

//    // Gets HTML Elements from Tracker Site
    public void initializeWebscraper(WebClient client) throws IOException, FailingHttpStatusCodeException, NoCompStatsException {
        HtmlPage page = client.getPage(baseUrl);
        client.waitForBackgroundJavaScript(10000);
        String titleElementString;

        // Get Html Elements
        List<HtmlElement> HtmlMostPlayedAgents = page.getByXPath("//span[@class='agent__name']");
        List<HtmlElement> valueClass = page.getByXPath("//span[@class='value']");
        List<HtmlElement> highlightedStat = page.getByXPath("//span[@class='valorant-highlighted-stat__value']");
        List<HtmlElement> nameClass = page.getByXPath("//span[@class='name']");
        HtmlElement HtmlPlaytime = page.getFirstByXPath("//span[@class='playtime']");
        HtmlElement HtmlMatches = page.getFirstByXPath("//span[@class='matches']");
        HtmlElement HtmlImageDiv = page.getFirstByXPath("//div[@class='ph-avatar']");
        HtmlElement titleElement = page.getFirstByXPath("//div[@class='details hasControls hasIcon']/h2");
        titleElementString = titleElement.asNormalizedText();
        List<HtmlElement> highlightedStatLabel = page.getByXPath("//span[@class='valorant-highlighted-stat__label']"); //NEW
        String statLabel = highlightedStatLabel.get(0).asNormalizedText(); // NEW

        // Gets Icon Url
        DomNode node = HtmlImageDiv.querySelector("image");
        if(node.getAttributes().getNamedItem("href") !=null) {
            iconUrl = page.getFullyQualifiedUrl(node.getAttributes().getNamedItem("href").getNodeValue()).toString().toLowerCase();
        }

        // Checks if stats are empty, else gets rank and kd.
        if (highlightedStat.isEmpty() || titleElementString.contains("Unrated")) {
            throw new NoCompStatsException(); // NEW
        }

        // Gets Winrate, Initializes Rank and KD HTML ELEMENTS
        HtmlElement HtmlWinrate = valueClass.get(6);
        HtmlElement HtmlRank = null;
        HtmlElement HtmlKd = null;
        HtmlElement HtmlHighRank = highlightedStat.get(1);

        // GETS RANK
        if (!statLabel.equalsIgnoreCase("Radiant") && !statLabel.contains("Imm")) {
            HtmlRank = highlightedStat.get(0);
            rank = HtmlRank.asNormalizedText();
        } else {
            rank = statLabel;
            highRank = HtmlHighRank.asNormalizedText();
        }

        HtmlKd = valueClass.get(4);

        // Agent Stats into Strings
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

        // Get Stats as Strings
        kd = HtmlKd.asNormalizedText();
        winrate = HtmlWinrate.asNormalizedText();

        // Get Emojis
        getEmojis();

        if (highRank != null) {
            rank = rank + " Rank " + highRank;
        }
    }
}

