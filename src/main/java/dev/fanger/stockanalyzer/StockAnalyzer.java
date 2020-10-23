package dev.fanger.stockanalyzer;

import dev.fanger.stockanalyzer.analysis.Pattern;
import dev.fanger.stockanalyzer.analysis.Rule;
import dev.fanger.stockanalyzer.data.StockData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockAnalyzer {

    public static void main(String[] args) {
        new StockAnalyzer();
    }

    private Map<String, List<StockData>> symbolToStockData;

    public StockAnalyzer() {
        List<String> symbols = Arrays.asList("UAL");
        File dataFolder = new File("recentData");
        setupData(dataFolder);

        List<Pattern> patterns = getPatterns();
        analyzePatterns(patterns, symbols);
    }

    private void setupData(File dataFolder) {
        symbolToStockData = new HashMap<>();

        File[] dataFiles = dataFolder.listFiles();
        for(int i = 0; i < dataFiles.length; i++) {
            File stockDataFile = dataFiles[i];
            System.out.println("Reading file " + stockDataFile.getName() + " (" + i + "/" + dataFiles.length + ")");
            String fixedFileName = stockDataFile.getName().substring(0, stockDataFile.getName().indexOf("."));

            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(stockDataFile))) {
                String[] splitColumnLine = bufferedReader.readLine().split(",");

                String readLine;
                while((readLine = bufferedReader.readLine()) != null) {
                    if(!readLine.isEmpty()) {
                        String[] splitReadLine = readLine.split(",", -1);

                        try {
                            StockData stockData = new StockData(
                                    fixedFileName,
                                    splitReadLine[1],
                                    Double.valueOf(splitReadLine[3]),
                                    Double.valueOf(splitReadLine[6]),
                                    Double.valueOf(splitReadLine[4]),
                                    Double.valueOf(splitReadLine[5])
                            );

                            if(!symbolToStockData.containsKey(stockData.getSymbol())) {
                                List<StockData> stockDataList = new ArrayList<>();
                                symbolToStockData.put(stockData.getSymbol(), stockDataList);
                            }

                            symbolToStockData.get(stockData.getSymbol()).add(stockData);
                        } catch (NumberFormatException e) {
                            System.out.println("Could not read line: " + readLine);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Sort data by date
        for(List<StockData> stockDataList : symbolToStockData.values()) {
            stockDataList.sort(Comparator.comparing(StockData::getDate));
        }
    }

    private void printSymbolDataOverTime(String symbol) {
        List<StockData> stockDataList = symbolToStockData.get(symbol);
        for(StockData stockData : stockDataList) {
            System.out.println(stockData);
        }
    }

    private List<Pattern> getPatterns() {
        List<Pattern> patterns = new ArrayList<>();

        Pattern buySellOnes = new Pattern(10_000, 0.5, Arrays.asList(
                Rule.from(Rule.Condition.LESS, .01, Rule.Action.BUY, 1),
                Rule.from(Rule.Condition.GREATER, .01, Rule.Action.SELL, 1)
        ));
        patterns.add(buySellOnes);

        Pattern buyTwosSellOnes = new Pattern(10_000, 0.5, Arrays.asList(
                Rule.from(Rule.Condition.LESS, .01, Rule.Action.BUY, 2),
                Rule.from(Rule.Condition.GREATER, .01, Rule.Action.SELL, 1)
        ));
        patterns.add(buyTwosSellOnes);

        Pattern buySellFives = new Pattern(10_000, 0.5, Arrays.asList(
                Rule.from(Rule.Condition.LESS, .05, Rule.Action.BUY, 1),
                Rule.from(Rule.Condition.GREATER, .05, Rule.Action.SELL, 1)
        ));
        patterns.add(buySellFives);

        Pattern buySellComplex = new Pattern(10_000, 0.5, Arrays.asList(
                Rule.from(Rule.Condition.LESS, .05, Rule.Action.BUY, 5),
                Rule.from(Rule.Condition.LESS, .02, Rule.Action.BUY, 3),
                Rule.from(Rule.Condition.GREATER, .025, Rule.Action.SELL, 3)
        ));
        patterns.add(buySellComplex);

        return patterns;
    }

    private void analyzePatterns(List<Pattern> patterns, List<String> symbols) {
        System.out.println("===== Analyze Patterns =====");

        for(String symbol : symbols) {
            System.out.println("Printing results for: " + symbol);

            for (Pattern pattern : patterns) {
                analyzePatternForSymbol(pattern, symbol);
            }
        }
    }

    private void analyzePatternForSymbol(Pattern pattern, String symbol) {
        List<StockData> stockDataOverTime = symbolToStockData.get(symbol);

        // Execute initial buy
        pattern.executeInitialBuy(stockDataOverTime.get(0));

        // Run pattern executions over data
        for(int i = 1; i < stockDataOverTime.size(); i++) {
            pattern.checkRulesOnNewData(stockDataOverTime.get(i - 1), stockDataOverTime.get(i));
        }

        // Print results
        System.out.println(
                pattern.getStartingCash() + "(0)" + " -> "
                + pattern.getAccountValue(stockDataOverTime.get(stockDataOverTime.size() - 1)) + "(" + pattern.getShares() + ")"
                + " | " + pattern.toString()
        );
    }
}
