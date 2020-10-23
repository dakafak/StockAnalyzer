package dev.fanger.stockanalyzer.analysis;

import dev.fanger.stockanalyzer.data.StockData;

import java.util.List;

public class Pattern {

    private List<Rule> rules;
    private double initialBuyingPercentage;
    private double startingCash;
    private double cash;
    private int shares;

    public Pattern(double startingCash, double initialBuyingPercentage, List<Rule> rules) {
        this.rules = rules;
        this.startingCash = startingCash;
        this.initialBuyingPercentage = initialBuyingPercentage;
        this.cash = startingCash;
    }

    public void executeInitialBuy(StockData stockData) {
        int sharesToBuy = (int) Math.floor((cash * initialBuyingPercentage) / stockData.getOpen());
        double buyingAmount = sharesToBuy * stockData.getOpen();
        shares += sharesToBuy;
        cash -= buyingAmount;
    }

    public void checkRulesOnNewData(StockData previousDay, StockData currentDay) {
        for(Rule rule : rules) {
            if(rule.applies(previousDay, currentDay)) {
                if(rule.getAction().equals(Rule.Action.BUY)) {
                    double buyingAmount = currentDay.getClose() * rule.getShareAmount();
                    if(cash >= buyingAmount) {
                        shares += rule.getShareAmount();
                        cash -= buyingAmount;
                    }
                } else if(rule.getAction().equals(Rule.Action.SELL)) {
                    double sellingAmount = currentDay.getClose() * rule.getShareAmount();
                    if(shares >= rule.getShareAmount()) {
                        shares -= rule.getShareAmount();
                        cash += sellingAmount;
                    }
                }

                break;
            }
        }
    }

    public double getStartingCash() {
        return startingCash;
    }

    public double getCash() {
        return cash;
    }

    public int getShares() {
        return shares;
    }

    public double getAccountValue(StockData currentDay) {
        return cash + (currentDay.getClose() * shares);
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "rules=" + rules +
                ", cash=" + cash +
                ", shares=" + shares +
                '}';
    }
}
