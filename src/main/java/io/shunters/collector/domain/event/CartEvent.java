package io.shunters.collector.domain.event;

/**
 * Created by mykidong on 2017-12-05.
 */
public class CartEvent {

    public static enum PropertyName {

        baseProperties("baseProperties"),
        itemId("itemId"),
        quantity("quantity"),
        price("price");

        private final String propertyName;

        private PropertyName(String propertyName)
        {
            this.propertyName = propertyName;
        }

        public String getPropertyName()
        {
            return this.propertyName;
        }
    }

    private BaseProperties baseProperties;

    private String itemId;
    private int quantity;
    private int price;

    public BaseProperties getBaseProperties() {
        return baseProperties;
    }

    public void setBaseProperties(BaseProperties baseProperties) {
        this.baseProperties = baseProperties;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
