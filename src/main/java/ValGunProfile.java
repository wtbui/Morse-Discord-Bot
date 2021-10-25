import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.List;

public class ValGunProfile extends ValProfile {
    public ValGunProfile(String username) {
        this.username = username;

        // Parses Username
        parseUsername(this.username);

        // Parses Url
        parseUrl();
    }

    protected void parseUrl() {
        String urlName = name;

        if (name.contains(" ")) {
            urlName = name.replace(" ", "%20");
        }

        baseUrl = "https://tracker.gg/valorant/profile/riot/" + urlName + "%23" + tag + "/weapons?season=all";
    }

    public void initializeWebscraper(WebClient client) throws IOException, FailingHttpStatusCodeException {
        HtmlPage page = client.getPage(baseUrl);
        client.waitForBackgroundJavaScript(10000);

        // Get Html Elements
        // List<HtmlElement> HtmlMostPlayedAgents = page.getByXPath("//span[@class='agent__name']"); EXAMPLE
        List<HtmlElement> highlightedStat = page.getByXPath("//span[@class='valorant-highlighted-stat__value']"); // TO CHECK COMP STATS
        HtmlElement HtmlImageDiv = page.getFirstByXPath("//div[@class='ph-avatar']"); // FOR PICTURE

        // Gets Icon Url
        DomNode node = HtmlImageDiv.querySelector("image");
        if(node.getAttributes().getNamedItem("href") !=null) {
            iconUrl = page.getFullyQualifiedUrl(node.getAttributes().getNamedItem("href").getNodeValue()).toString().toLowerCase();
        }

        // Checks if stats are empty, else gets rank and kd.
        if (highlightedStat.isEmpty()) {
            compStatus = false;
        } else {
            compStatus = true;
        }

        // Stats into Strings

        // Get Emojis
        getEmojis();

    }
}


