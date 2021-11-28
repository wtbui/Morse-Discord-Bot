import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.parser.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ValProfileMatchStats {
    private String baseUrl;
    private String agent;
    private String won;
    private String loss;
    private String map;
    private String mode;
    private String kills;
    private String deaths;
    private String assists;

    public ValProfileMatchStats(String oldUrl) {
        parseUrl(oldUrl);
    }

    public void parseUrl(String oldUrl) {
        this.baseUrl = oldUrl.replace("https://tracker.gg/valorant/profile/", "https://api.tracker.gg/api/v2/valorant/standard/matches/");
        this.baseUrl = this.baseUrl.replace("?type=competitive", "");
        this.baseUrl = this.baseUrl.replace("?season=all", "");
        this.baseUrl = this.baseUrl.replace("/overview", "");
    }

    public void initializeWebScraper() throws IOException {
        WebClient client = new WebClient(BrowserVersion.BEST_SUPPORTED);
        client.getOptions().setJavaScriptEnabled(false);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setUseInsecureSSL(true);
        Page page = client.getPage(baseUrl);
        WebResponse response = page.getWebResponse();

        String lastMatch = response.getContentAsString();
        String[] matches = lastMatch.split("mmr");
        lastMatch = matches[0];
        lastMatch = lastMatch.replace("\"", " ");
        ArrayList<String> matchList = new ArrayList<>(Arrays.asList(lastMatch.split(" ")));
        agent = matchList.get(matchList.indexOf("agentName") + 2);
        mode = matchList.get(matchList.indexOf("modeName") + 2);
        map = matchList.get(matchList.indexOf("mapName") + 2);
        won = matchList.get(matchList.indexOf("roundsWon") + 25);
        loss = matchList.get(matchList.indexOf("roundsLost") + 25);
        kills = matchList.get(matchList.indexOf("kills") + 24);
        deaths = matchList.get(matchList.indexOf("deaths") + 24);
        assists = matchList.get(matchList.indexOf("assists") + 24);


        System.out.println(lastMatch);
    }

    public String getAgent() {
        return agent;
    }

    public String getWon() {
        return won;
    }

    public String getLoss() {
        return loss;
    }

    public String getMap() {
        return map;
    }

    public String getMode() {
        return mode;
    }

    public String getKills() {
        return kills;
    }

    public String getDeaths() {
        return deaths;
    }

    public String getAssists() {
        return assists;
    }
}
