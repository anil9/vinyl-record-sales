package com.nilsson.vinylrecordsales.domain;

import com.nilsson.vinylrecordsales.advertisement.AdvertisementDescription;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class AdvertisementInformation {
    private final Integer folderId;
    private final String title;
    private final ProductCategory productCategory;
    private final String description;
    private final Condition condition;
    private final Tax tax;
    private final Quantity quantityInStock;
    private final BigDecimal purchasePrice;
    private final ShippingInformation shippingInformation;
    private final BigDecimal auctionPrice;
    private final Currency currency;
    private final TargetMarketplace targetMarketplace;

    private AdvertisementInformation(AdvertisementInformationBuilder advertisementInformation) {
        this.folderId = requireNonNull(advertisementInformation.folderId, "folderId");
        this.title = requireNonNull(advertisementInformation.title, "title");
        this.productCategory = requireNonNull(advertisementInformation.productCategory, "productCategory");
        this.auctionPrice = requireNonNull(advertisementInformation.auctionPrice, "auctionPrice");
        this.quantityInStock = requireNonNull(advertisementInformation.quantityInStock, "quantityInStock");
        this.shippingInformation = requireNonNull(advertisementInformation.shippingInformation, "shippingInformation");
        this.condition = requireNonNull(advertisementInformation.condition, "condition");
        this.description = requireNonNull(advertisementInformation.description, "description");
        this.tax = requireNonNull(advertisementInformation.tax, "tax");
        this.purchasePrice = requireNonNull(advertisementInformation.purchasePrice, "purchasePrice");
        this.currency = requireNonNull(advertisementInformation.currency, "currency");
        this.targetMarketplace = requireNonNull(advertisementInformation.targetMarketplace, "targetMarketplace");
    }

    public static AdvertisementInformationBuilder builder() {
        return new AdvertisementInformationBuilder();
    }

    public Integer getFolderId() {
        return folderId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Condition getCondition() {
        return condition;
    }

    public Tax getTax() {
        return tax;
    }

    public Quantity getQuantityInStock() {
        return quantityInStock;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public ShippingInformation getShippingInformation() {
        return shippingInformation;
    }

    public BigDecimal getAuctionPrice() {
        return auctionPrice;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public Currency getCurrency() {
        return currency;
    }

    public TargetMarketplace getTargetMarketplace() {
        return targetMarketplace;
    }

    public static final class AdvertisementInformationBuilder {
        private TargetMarketplace targetMarketplace;
        private Integer folderId;
        private String title;
        private ProductCategory productCategory;
        private String description;
        private Condition condition;
        private Tax tax;
        private Quantity quantityInStock;
        private BigDecimal purchasePrice;
        private ShippingInformation shippingInformation;
        private BigDecimal auctionPrice;
        private Currency currency;


        private AdvertisementInformationBuilder() {
        }


        public AdvertisementInformationBuilder withFolderId(Integer folderId) {
            this.folderId = folderId;
            return this;
        }

        public AdvertisementInformationBuilder withProductCategory(ProductCategory productCategory) {
            this.productCategory = productCategory;
            return this;
        }

        public AdvertisementInformationBuilder withCondition(Condition condition) {
            this.condition = condition;
            return this;
        }

        public AdvertisementInformationBuilder withTax(Tax tax) {
            this.tax = tax;
            return this;
        }

        public AdvertisementInformationBuilder withQuantityInStock(Quantity quantityInStock) {
            this.quantityInStock = quantityInStock;
            return this;
        }

        public AdvertisementInformationBuilder withPurchasePrice(BigDecimal purchasePrice) {
            this.purchasePrice = purchasePrice;
            return this;
        }

        public AdvertisementInformationBuilder withShippingInformation(ShippingInformation shippingInformation) {
            this.shippingInformation = shippingInformation;
            return this;
        }

        public AdvertisementInformationBuilder withAuctionPrice(BigDecimal auctionPrice) {
            this.auctionPrice = auctionPrice;
            return this;
        }

        public AdvertisementInformationBuilder withRecordInformation(RecordInformation recordInformation) {
            this.title = recordInformation.getTitle();
            this.description = AdvertisementDescription.getDescription(recordInformation);
            return this;
        }

        public AdvertisementInformationBuilder withCurrency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public AdvertisementInformationBuilder withTargetMarketplace(TargetMarketplace targetMarketplace) {
            this.targetMarketplace = targetMarketplace;
            return this;
        }

        public AdvertisementInformation build() {
            return new AdvertisementInformation(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdvertisementInformation that = (AdvertisementInformation) o;
        return Objects.equals(folderId, that.folderId) && Objects.equals(title, that.title) && productCategory == that.productCategory && Objects.equals(description, that.description) && condition == that.condition && Objects.equals(tax, that.tax) && Objects.equals(quantityInStock, that.quantityInStock) && Objects.equals(purchasePrice, that.purchasePrice) && Objects.equals(shippingInformation, that.shippingInformation) && Objects.equals(auctionPrice, that.auctionPrice) && Objects.equals(currency, that.currency) && targetMarketplace == that.targetMarketplace;
    }

    @Override
    public int hashCode() {
        return Objects.hash(folderId, title, productCategory, description, condition, tax, quantityInStock, purchasePrice, shippingInformation, auctionPrice, currency, targetMarketplace);
    }

    @Override
    public String toString() {
        return "AdvertisementInformation{" +
                "folderId=" + folderId +
                ", title='" + title + '\'' +
                ", productCategory=" + productCategory +
                ", description='" + description + '\'' +
                ", condition=" + condition +
                ", tax=" + tax +
                ", quantityInStock=" + quantityInStock +
                ", purchasePrice=" + purchasePrice +
                ", shippingInformation=" + shippingInformation +
                ", auctionPrice=" + auctionPrice +
                ", currency=" + currency +
                ", targetMarketplace=" + targetMarketplace +
                '}';
    }
}
