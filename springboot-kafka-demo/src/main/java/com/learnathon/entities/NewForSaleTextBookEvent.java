
package com.learnathon.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * NewForSaleTextBookEvent
 * <p>
 * Represents the event when a text book has been put up for sale
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "bookName",
    "price"
})
public class NewForSaleTextBookEvent {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("bookName")
    private String bookName;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("price")
    private Double price;

    /**
     * No args constructor for use in serialization
     * 
     */
    public NewForSaleTextBookEvent() {
    }

    /**
     * 
     * @param price
     * @param bookName
     */
    public NewForSaleTextBookEvent(String bookName, Double price) {
        super();
        this.bookName = bookName;
        this.price = price;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("bookName")
    public String getBookName() {
        return bookName;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("bookName")
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("price")
    public Double getPrice() {
        return price;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("price")
    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(NewForSaleTextBookEvent.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("bookName");
        sb.append('=');
        sb.append(((this.bookName == null)?"<null>":this.bookName));
        sb.append(',');
        sb.append("price");
        sb.append('=');
        sb.append(((this.price == null)?"<null>":this.price));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.bookName == null)? 0 :this.bookName.hashCode()));
        result = ((result* 31)+((this.price == null)? 0 :this.price.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof NewForSaleTextBookEvent) == false) {
            return false;
        }
        NewForSaleTextBookEvent rhs = ((NewForSaleTextBookEvent) other);
        return (((this.bookName == rhs.bookName)||((this.bookName!= null)&&this.bookName.equals(rhs.bookName)))&&((this.price == rhs.price)||((this.price!= null)&&this.price.equals(rhs.price))));
    }

}
