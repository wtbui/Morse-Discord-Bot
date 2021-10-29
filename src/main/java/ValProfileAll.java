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

    // Gets HTML Elements from Tracker Site
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
        HtmlElement titleElement = page.getFirstByXPath("//div[@class='details hasControls hasIcon']/h2"); // NEW
        titleElementString = titleElement.asNormalizedText(); // NEW

        // Gets Icon Url
        DomNode node = HtmlImageDiv.querySelector("image");
        if(node.getAttributes().getNamedItem("href") !=null) {
            iconUrl = page.getFullyQualifiedUrl(node.getAttributes().getNamedItem("href").getNodeValue()).toString().toLowerCase();
        }

        // Gets Winrate, Initializes Rank and KD HTML ELEMENTS
        HtmlElement HtmlWinrate = valueClass.get(6);
        HtmlElement HtmlRank = null;
        HtmlElement HtmlKd = null;

        // Checks if stats are empty, else gets rank and kd.
        if (highlightedStat.isEmpty() || titleElementString.contains("Unrated")) {
            throw new NoCompStatsException(); // NEW
        } else {
            HtmlRank = highlightedStat.get(0);
            HtmlKd = highlightedStat.get(1);
        }

        // Agent Stats into Strings
        agentMatches1 = nameClass.get(23).asNormalizedText();
        agentWR1 = nameClass.get(24).asNormalizedText();
        agentKD1 = nameClass.get(25).asNormalizedText();
        agentMatches2 = nameClass.get(28).asNormalizedText();agentWR2 = nameClass.get(29).asNormalizedText();
        agentKD2 = nameClass.get(30).asNormalizedText();
        agentMatches3 = nameClass.get(33).asNormalizedText();
        agentWR3 = nameClass.get(34).asNormalizedText();
        agentKD3 = nameClass.get(35).asNormalizedText();
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
}

