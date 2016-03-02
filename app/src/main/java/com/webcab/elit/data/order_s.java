package com.webcab.elit.data;

import java.util.ArrayList;
import java.util.Comparator;

public class order_s implements Comparable<order_s> {
	//удачная поездка

	public final static String[] AUTO_CLASS = {"Бизнес", "Стандарт", "", "", "Премиум"};

	int id, Rate;
	String From, To, When, Price;
	boolean preorder;
    long whenDate;
	int orderID;
    ArrayList additionalServices = new ArrayList<>();
    int carClass;
	//car id which is used to track car waiting order
	private int callsignid;
	public order_s(int id, int rate, String from, String to, String when,
			String price, boolean preorder, long whenDate, ArrayList additionalServices, int orderID, int carClass) {
		super();
		this.id = id;
		Rate = rate;
		From = from;
		To = to;
		When = when;
		Price = price;
		this.preorder = preorder;
        this.whenDate = whenDate;
        this.additionalServices = additionalServices;
		this.orderID = orderID;
        this.carClass = carClass;
	}
	public int getId() {
		return id;
	}
	public int getRate() {
		return Rate;
	}
	public String getFrom() {
		return From;
	}
	public String getTo() {
		return To;
	}
	public String getWhen() {
		return When;
	}
	public String getPrice() {
		return Price;
	}
	public boolean getPreorderStatus() {return preorder;}
    public long getWhenDate() {return whenDate;}
    public ArrayList getAdditionalServices() {return additionalServices;}
	public int getOrderID() {return orderID;}
    public int getCarClass() {return carClass;}

	public int getCallsignid() {
		return callsignid;
	}

	public void setCallsignid(int callsignid) {
		this.callsignid = callsignid;
	}

	@Override
	public int compareTo(order_s another) {
		long compareDate = another.getWhenDate();
		return (int) (compareDate - this.whenDate);
	}

    public static Comparator<order_s> orderFromComparator = new Comparator<order_s>() {
        @Override
        public int compare(order_s lhs, order_s rhs) {
            return lhs.getFrom().toUpperCase().compareTo(rhs.getFrom().toUpperCase());
        }
    };

    public static Comparator<order_s> orderToComparator = new Comparator<order_s>() {
        @Override
        public int compare(order_s lhs, order_s rhs) {
            return lhs.getTo().toUpperCase().compareTo(rhs.getTo().toUpperCase());
        }
    };
}
