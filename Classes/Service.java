package Classes;

import javafx.beans.property.SimpleStringProperty;

import java.util.Date;

/**
 * Created by Eric on 08/09/2018.
 */
public class Service {
    private SimpleStringProperty assetName, provider, contract, description, warranty, servicedate, sercost;

    public Service(String assetName, String provider, String servicedate, String warranty, String contract, String cost, String description) {
        this.assetName = new SimpleStringProperty(assetName);
        this.provider = new SimpleStringProperty(provider);
        this.contract = new SimpleStringProperty(contract);
        this.description = new SimpleStringProperty(description);
        this.warranty = new SimpleStringProperty(warranty);
        this.servicedate = new SimpleStringProperty(servicedate);
        this.sercost = new SimpleStringProperty(cost);
    }

    public String getProvider() {return provider.get();}

    public String getAssetName() {return assetName.get();}

    public SimpleStringProperty assetNameProperty() {return assetName;}

    public void setAssetName(String assetName) {this.assetName.set(assetName);}

    public SimpleStringProperty providerProperty() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider.set(provider);
    }

    public String getContract() {
        return contract.get();
    }

    public SimpleStringProperty contractProperty() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract.set(contract);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public String getWarranty() {return warranty.get();}

    public SimpleStringProperty warrantyProperty() {return warranty;}

    public void setWarranty(String warranty) {this.warranty.set(warranty);}

    public String getServicedate() {return servicedate.get();}

    public SimpleStringProperty servicedateProperty() {return servicedate;}

    public void setServicedate(String servicedate) {this.servicedate.set(servicedate);}

    public String getSercost() {return sercost.get();}

    public SimpleStringProperty sercostProperty() {return sercost;}

    public void setSercost(String sercost) {this.sercost.set(sercost);}
}
