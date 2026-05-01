# 📈 Dow Jones Industrial Average — Live Tracker

A Java application that tracks the **Dow Jones Industrial Average** in real time
by querying the DIA ETF price from Alpha Vantage every 15 seconds and storing
readings in a queue.

## Features
- Fetches live DJIA price every 15 seconds via Alpha Vantage API
- Stores price history in a FIFO queue (last 10 readings)
- Displays timestamp with every reading
- Gracefully handles errors and retries automatically

## Tech Stack
- Java 17
- Gradle 8
- Alpha Vantage API

## How to Run
1. Get a free API key at https://www.alphavantage.co/support/#api-key
2. Paste your key in App.java where it says `YOUR_API_KEY`
3. Run:
```bash
git clone https://github.com/YOUR_USERNAME/dow-jones-tracker.git
cd dow-jones-tracker
gradle build
gradle run
```

