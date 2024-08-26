package util;

public class MutableBidAsk {

    int bid = 0;
    int ask = 0;

    //increment bid by volume v
    public void incrementBid(int v) {
        bid += v;
    }

    //increment ask by volume v
    public void incrementAsk(int v) {
        ask += v;
    }

    //constructor
    public MutableBidAsk(int bid, int ask) {
        this.bid = bid;
        this.ask = ask;
    }

    public int getBid() {
        return bid;
    }

    public int getAsk() {
        return ask;
    }

}
