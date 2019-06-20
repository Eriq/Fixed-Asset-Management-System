package Classes;

import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Eric on 08/12/2018.
 */
public class Depreciation {
    private SimpleStringProperty aged, month, bookValue, thisMonth, accDepre;

    public Depreciation(String month, String aged, String bookValue, String thisMonth, String accDepre) {
        this.aged = new SimpleStringProperty(aged);
        this.month= new SimpleStringProperty(month);
        this.bookValue= new SimpleStringProperty(bookValue);
        this.thisMonth= new SimpleStringProperty(thisMonth);
        this.accDepre= new SimpleStringProperty(accDepre);
    }

    public String getAged() {
        return aged.get();
    }

    public SimpleStringProperty agedProperty() {
        return aged;
    }

    public void setAged(String aged) {
        this.aged.set(aged);
    }

    public String getMonth() {
        return month.get();
    }

    public SimpleStringProperty monthProperty() {
        return month;
    }

    public void setMonth(String month) {
        this.month.set(month);
    }

    public String getBookValue() {
        return bookValue.get();
    }

    public SimpleStringProperty bookValueProperty() {
        return bookValue;
    }

    public void setBookValue(String bookValue) {
        this.bookValue.set(bookValue);
    }

    public String getThisMonth() {
        return thisMonth.get();
    }

    public SimpleStringProperty thisMonthProperty() {
        return thisMonth;
    }

    public void setThisMonth(String thisMonth) {
        this.thisMonth.set(thisMonth);
    }

    public String getAccDepre() {
        return accDepre.get();
    }

    public SimpleStringProperty accDepreProperty() {
        return accDepre;
    }

    public void setAccDepre(String accDepre) {
        this.accDepre.set(accDepre);
    }
}
