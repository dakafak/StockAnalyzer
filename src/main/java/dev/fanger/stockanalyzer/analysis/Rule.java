package dev.fanger.stockanalyzer.analysis;

import dev.fanger.stockanalyzer.data.StockData;

import static dev.fanger.stockanalyzer.analysis.Rule.Condition.EQUAL;
import static dev.fanger.stockanalyzer.analysis.Rule.Condition.GREATER;
import static dev.fanger.stockanalyzer.analysis.Rule.Condition.LESS;

public class Rule {

    public enum Action {
        BUY,
        SELL
    }

    public enum Condition {
        EQUAL,
        GREATER,
        LESS
    }

    private Action action;
    private Condition condition;
    private double percentageDifference;
    private int shareAmount;

    public Rule(Condition condition, double percentageDifference, Action action, int shareAmount) {
        this.action = action;
        this.condition = condition;
        this.percentageDifference = percentageDifference;
        this.shareAmount = shareAmount;
    }

    public boolean applies(StockData previousData, StockData recentData) {
        if(EQUAL.equals(condition)) {
            return recentData.getClose() == previousData.getClose();
        } else if(GREATER.equals(condition)) {
            double goalAmount = previousData.getClose() * (1 + percentageDifference);
            return recentData.getClose() > goalAmount;
        } else if(LESS.equals(condition)) {
            double goalAmount = previousData.getClose() * (1 - percentageDifference);
            return recentData.getClose() < goalAmount;
        }

        return false;
    }

    public Action getAction() {
        return action;
    }

    public int getShareAmount() {
        return shareAmount;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "action=" + action +
                ", condition=" + condition +
                ", percentageDifference=" + percentageDifference +
                ", shareAmount=" + shareAmount +
                '}';
    }

    public static Rule from(Condition condition, double percentageDifference, Action action, int shareAmount) {
        return new Rule(condition, percentageDifference, action, shareAmount);
    }
}
