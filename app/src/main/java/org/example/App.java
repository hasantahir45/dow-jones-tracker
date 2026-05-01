
package dowjones;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;

public class App {

    static class StockEntry {
        String symbol;
        double price;
        String timestamp;

        StockEntry(String symbol, double price, String timestamp) {
            this.symbol    = symbol;
            this.price     = price;
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "[" + timestamp + "]  " + symbol + "  $" + String.format("%,.2f", price);
        }
    }

    static double fetchPrice(String symbol, String apiKey) throws Exception {
        String urlStr = "https://www.alphavantage.co/query"
                      + "?function=GLOBAL_QUOTE"
                      + "&symbol=" + symbol
                      + "&apikey=" + apiKey;

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();
        conn.disconnect();

        String json = sb.toString();
        String key = "\"05. price\": \"";
        int idx = json.indexOf(key);
        if (idx == -1) throw new Exception("Price not found in response: " + json);

        int start = idx + key.length();
        int end   = json.indexOf("\"", start);

        return Double.parseDouble(json.substring(start, end).trim());
    }

    public static void main(String[] args) {
        // DIA is the ETF that tracks the Dow Jones Industrial Average
        final String SYMBOL       = "DIA";
        final String API_KEY      = "YOUR_API_KEY"; // <-- your key here
        final int    INTERVAL_SEC = 15;
        final int    MAX_ENTRIES  = 10;

        Queue<StockEntry> priceQueue = new LinkedList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        System.out.println("================================================");
        System.out.println("  Dow Jones Industrial Average — Live Tracker");
        System.out.println("  Tracking via DIA ETF (mirrors Dow Jones)");
        System.out.println("  Fetching every " + INTERVAL_SEC + " seconds");
        System.out.println("  Press Ctrl+C to stop");
        System.out.println("================================================\n");

        while (true) {
            try {
                String now   = LocalDateTime.now().format(fmt);
                double price = fetchPrice(SYMBOL, API_KEY);

                StockEntry entry = new StockEntry(SYMBOL, price, now);
                priceQueue.add(entry);

                if (priceQueue.size() > MAX_ENTRIES) {
                    priceQueue.poll();
                }

                System.out.println("Fetched:  " + entry);
                System.out.println("Queue (" + priceQueue.size() + " entries stored):");
                int i = 1;
                for (StockEntry e : priceQueue) {
                    System.out.println("   " + i++ + ".  " + e);
                }
                System.out.println();

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println("Retrying in " + INTERVAL_SEC + " seconds...\n");
            }

            try {
                Thread.sleep(INTERVAL_SEC * 1000L);
            } catch (InterruptedException ie) {
                System.out.println("Stopped.");
                break;
            }
        }
    }
}