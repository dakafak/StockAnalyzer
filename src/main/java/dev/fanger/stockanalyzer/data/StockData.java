package dev.fanger.stockanalyzer.data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StockData {

    private static DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    private Date date;
    private String symbol;
    private double open;
    private double close;
    private double high;
    private double low;

    public StockData(String dateString, String symbol, double open, double close, double high, double low) throws ParseException {
        this.date = dateFormat.parse(dateString);
        this.symbol = symbol;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
    }

    public static DateFormat getDateFormat() {
        return dateFormat;
    }

    public Date getDate() {
        return date;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getOpen() {
        return open;
    }

    public double getClose() {
        return close;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    @Override
    public String toString() {
        return "StockData{" +
                "date=" + date +
                ", symbol='" + symbol + '\'' +
                ", open=" + open +
                ", close=" + close +
                ", high=" + high +
                ", low=" + low +
                '}';
    }
}
