import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ValGunProfile extends ValProfile {
    private String bestRifle;
    private String bestEco;
    private String bestHeavy;
    private String bestSidearm;
    private String topName;

    private String rifleK;
    private String ecoK;
    private String heavyK;
    private String sidearmK;
    private String meleeK;
    private String topK;

    private String rifleHs;
    private String ecoHs;
    private String heavyHs;
    private String sidearmHs;

    private String rifleHsp;
    private String ecoHsp;
    private String heavyHsp;
    private String sidearmHsp;
    private String topHsp;

    private String rifleLongest;
    private String ecoLongest;
    private String heavyLongest;
    private String sidearmLongest;

    private String rifleEmote;
    private String ecoEmote;
    private String heavyEmote;
    private String sidearmEmote;
    private String meleeEmote;
    private String topEmote;

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

    public void initializeWebscraper(WebClient client) throws IOException, FailingHttpStatusCodeException, NoCompStatsException {
        HashMap<String, String> bestWepMap = new HashMap<String, String>();
        HtmlPage page = client.getPage(baseUrl);
        client.waitForBackgroundJavaScript(10000);
        String titleElementString;

        // Get Html Elements
        HtmlElement HtmlImageDiv = page.getFirstByXPath("//div[@class='ph-avatar']"); // FOR PICTURE
        List<HtmlElement> tableElements = page.getByXPath("//table[@class='trn-table trn-table--alternating']/tbody/tr");
        bestWepMap = parseMap(tableElements);
        HtmlElement titleElement = page.getFirstByXPath("//header[@class='segment-used__header']/h2");
        titleElementString = titleElement.asNormalizedText();

        // Checks Comp Status
        if (titleElementString.contains("Unrated")) {
            throw new NoCompStatsException();
        }

        // Creates String Arrays
        String[] rifleStats = bestWepMap.get("rifle").split(" ");
        String[] heavyStats = bestWepMap.get("heavy").split(" ");
        String[] ecoStats = bestWepMap.get("eco").split(" ");
        String[] sidearmStats = bestWepMap.get("sidearm").split(" ");
        String[] topStats = bestWepMap.get("top").split(" ");

        // Puts Strings into Data Fields
        this.bestRifle = rifleStats[0];
        this.bestHeavy = heavyStats[0];
        this.bestEco = ecoStats[0];
        this.bestSidearm = sidearmStats[0];
        this.topName = topStats[0];

        this.rifleK = rifleStats[1];
        this.ecoK = ecoStats[1];
        this.heavyK = heavyStats[1];
        this.sidearmK = sidearmStats[1];
        this.topK = topStats[1];

        this.rifleHs = rifleStats[2];
        this.ecoHs = ecoStats[2];
        this.heavyHs = heavyStats[2];
        this.sidearmHs = sidearmStats[2];

        this.rifleHsp = rifleStats[3];
        this.ecoHsp = ecoStats[3];
        this.heavyHsp = heavyStats[3];
        this.sidearmHsp = sidearmStats[3];
        this.topHsp = topStats[2];

        this.rifleLongest = rifleStats[4];
        this.ecoLongest = ecoStats[4];
        this.heavyLongest = heavyStats[4];
        this.sidearmLongest = sidearmStats[4];


        // Gets Icon Url
        DomNode node = HtmlImageDiv.querySelector("image");
        if(node.getAttributes().getNamedItem("href") !=null) {
            iconUrl = page.getFullyQualifiedUrl(node.getAttributes().getNamedItem("href").getNodeValue()).toString().toLowerCase();
        }

        // Get Emojis
        getEmojis();
    }

    public HashMap<String, String> parseMap(List<HtmlElement> tableElements) {
        HashMap<String, String> bestWepMap = new HashMap<String, String>();

        bestWepMap.put("rifle", "");
        bestWepMap.put("heavy", "");
        bestWepMap.put("eco", "");
        bestWepMap.put("sidearm", "");
        bestWepMap.put("top", "");

        HtmlElement weaponNameElement;
        String weaponName;
        HtmlElement kElement;
        String kString;
        HtmlElement hsElement;
        String hsString;
        HtmlElement hspElement;
        String hspString;
        HtmlElement longestElement;
        String longestString;

        for (HtmlElement element: tableElements) {
            List<HtmlElement> individualTableElements = element.getByXPath("*//span[@class='segment-used__tp-name']");
            weaponNameElement = element.getFirstByXPath("*//span[@class='segment-used__tp-name']");
            weaponName = weaponNameElement.asNormalizedText();
            kElement = individualTableElements.get(1);
            kString = kElement.asNormalizedText();
            hsElement = individualTableElements.get(3);
            hsString = hsElement.asNormalizedText();
            hspElement = individualTableElements.get(4);
            hspString = hspElement.asNormalizedText();
            longestElement = individualTableElements.get(7);
            longestString = longestElement.asNormalizedText();



            if (checkWepMap(bestWepMap)) {
                switch (weaponName) {
                    case "Phantom":
                    case "Vandal":
                    case "Bulldog":
                    case "Guardian":
                        if (checkWep(bestWepMap, "rifle")) {
                            bestWepMap.put("rifle", weaponName + " " + kString + " " + hsString
                                    + " " + hspString + " " + longestString);
                        }

                        if (checkWep(bestWepMap, "top")) {
                            bestWepMap.put("top", weaponName + " " + kString + " " +  hspString);
                        }
                        break;
                    case "Operator":
                    case "Odin":
                        if (checkWep(bestWepMap, "heavy")) {
                            bestWepMap.put("heavy", weaponName + " " + kString + " " + hsString
                                    + " " + hspString + " " + longestString);
                        }

                        if (checkWep(bestWepMap, "top")) {
                            bestWepMap.put("top", weaponName + " " + kString + " " +  hspString);
                        }
                        break;
                    case "Spectre":
                    case "Ares":
                    case "Stinger":
                    case "Marshal":
                    case "Judge":
                    case "Bucky":
                        if (checkWep(bestWepMap, "eco")) {
                            bestWepMap.put("eco", weaponName + " " + kString + " " + hsString
                                    + " " + hspString + " " + longestString);
                        }

                        if (checkWep(bestWepMap, "top")) {
                            bestWepMap.put("top", weaponName + " " + kString + " " +  hspString);
                        }
                        break;
                    case "Shorty":
                    case "Classic":
                    case "Sheriff":
                    case "Ghost":
                    case "Frenzy":
                        if (checkWep(bestWepMap, "sidearm")) {
                            bestWepMap.put("sidearm", weaponName + " " + kString + " " + hsString
                                    + " " + hspString + " " + longestString);
                        }

                        if (checkWep(bestWepMap, "top")) {
                            bestWepMap.put("top", weaponName + " " + kString + " " +  hspString);
                        }
                        break;
                    default:
                        this.meleeK = kString;
                        break;
                }
            } else {
                break;
            }
        }
        return bestWepMap;
    }

    public boolean checkWepMap(HashMap<String, String> bestWepMap) {
        if (bestWepMap.get("rifle").equals("") || bestWepMap.get("heavy").equals("")
                || bestWepMap.get("eco").equals("") || bestWepMap.get("sidearm").equals("") || meleeK == null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkWep(HashMap<String, String> bestWepMap, String key) {
        if (bestWepMap.get(key).equals("")) {
            return true;
        } else {
            return false;
        }
    }

    protected void getEmojis() {
        HashMap<String, String> emojiMap = new HashMap<String, String>();
        emojiMap.put("classic", "<:classic:903567897098588181>");
        emojiMap.put("shorty", "<:shorty:903567897048272957>");
        emojiMap.put("frenzy", "<:frenzy:903567897383800862>");
        emojiMap.put("ghost", "<:ghostv:903567897379622972>");
        emojiMap.put("sheriff", "<:sheriff:903567897274757121>");
        emojiMap.put("phantom", "<:phantom:903567897501253654>");
        emojiMap.put("vandal", "<:vandal:903567897446731817>");
        emojiMap.put("bulldog", "<:bulldog:903567897434148874>");
        emojiMap.put("guardian", "<:guardian:903567897438347304>");
        emojiMap.put("odin", "<:odin:903567897354448926>");
        emojiMap.put("op", "<:op:903567897408995329>");
        emojiMap.put("marshal", "<:marshal:903567897367031809>");
        emojiMap.put("stinger", "<:stinger:903567897539014707>");
        emojiMap.put("spectre", "<:spectre:903567897031503875>");
        emojiMap.put("bucky", "<:bucky:903567897295736853>");
        emojiMap.put("judge", "<:judgev:903567897597722664>");
        emojiMap.put("ares", "<:ares:903567896960192553>");
        emojiMap.put("melee", "<:melee:903567896951816193>");

        this.rifleEmote = emojiMap.get(getBestRifle().toLowerCase());
        this.heavyEmote = emojiMap.get(getBestHeavy().toLowerCase());
        this.ecoEmote = emojiMap.get(getBestEco().toLowerCase());
        this.sidearmEmote = emojiMap.get(getBestSidearm().toLowerCase());
        this.topEmote = emojiMap.get(getTopName().toLowerCase());
        this.meleeEmote = emojiMap.get("melee");
    }

    public String getBestRifle() {
        return bestRifle;
    }

    public String getBestEco() {
        return bestEco;
    }

    public String getBestHeavy() {
        return bestHeavy;
    }

    public String getBestSidearm() {
        return bestSidearm;
    }

    public String getRifleK() {
        return rifleK;
    }

    public String getEcoK() {
        return ecoK;
    }

    public String getHeavyK() {
        return heavyK;
    }

    public String getSidearmK() {
        return sidearmK;
    }

    public String getRifleHs() {
        return rifleHs;
    }

    public String getEcoHs() {
        return ecoHs;
    }

    public String getHeavyHs() {
        return heavyHs;
    }

    public String getSidearmHs() {
        return sidearmHs;
    }

    public String getRifleHsp() {
        return rifleHsp;
    }

    public String getEcoHsp() {
        return ecoHsp;
    }

    public String getHeavyHsp() {
        return heavyHsp;
    }

    public String getSidearmHsp() {
        return sidearmHsp;
    }

    public String getRifleLongest() {
        return rifleLongest;
    }

    public String getEcoLongest() {
        return ecoLongest;
    }

    public String getHeavyLongest() {
        return heavyLongest;
    }

    public String getSidearmLongest() {
        return sidearmLongest;
    }

    public String getMeleeK() {
        return meleeK;
    }

    public String getRifleEmote() {
        return rifleEmote;
    }

    public String getEcoEmote() {
        return ecoEmote;
    }

    public String getHeavyEmote() {
        return heavyEmote;
    }

    public String getSidearmEmote() {
        return sidearmEmote;
    }

    public String getMeleeEmote() {
        return meleeEmote;
    }

    public String getTopName() {
        return topName;
    }

    public String getTopK() {
        return topK;
    }

    public String getTopHsp() {
        return topHsp;
    }

    public String getTopEmote() {
        return topEmote;
    }
}


