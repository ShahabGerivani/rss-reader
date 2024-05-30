// This class is used to represent blogs/websites in the app
public class Blog {
    private final String title;
    private final String url;
    private final String rss;

    public Blog(String title, String url, String rss) {
        this.title = title;
        this.url = url;
        this.rss = rss;
    }

    public String getTitle() {
        return title;
    }

    public String getURL() {
        return url;
    }

    public String getRSS() {
        return rss;
    }
}
