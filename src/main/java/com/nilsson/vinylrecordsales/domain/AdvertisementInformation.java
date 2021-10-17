package com.nilsson.vinylrecordsales.domain;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.Objects;

import com.nilsson.vinylrecordsales.advertisement.AdvertisementDescription;

public class AdvertisementInformation {
	private final String folderId;
	private final String title;
	private final String description;
	private final Condition condition;
	private final Integer tax;
	private final Quantity quantityInStock;
	private final BigDecimal purchasePrice;
	private final ShippingInformation shippingInformation;
	private final BigDecimal auctionPrice;

	public AdvertisementInformation(AdvertisementInformationBuilder advertisementInformation) {
		this.auctionPrice = requireNonNull(advertisementInformation.auctionPrice, "auctionPrice");
		this.quantityInStock = requireNonNull(advertisementInformation.quantityInStock, "quantityInStock");
		this.shippingInformation = requireNonNull(advertisementInformation.shippingInformation, "shippingInformation");
		this.condition = requireNonNull(advertisementInformation.condition, "condition");
		this.folderId = requireNonNull(advertisementInformation.folderId, "folderId");
		this.description = requireNonNull(advertisementInformation.description, "description");
		this.tax = requireNonNull(advertisementInformation.tax, "tax");
		this.purchasePrice = requireNonNull(advertisementInformation.purchasePrice, "purchasePrice");
		this.title = requireNonNull(advertisementInformation.title, "title");
	}

	public static AdvertisementInformationBuilder builder() {
		return new AdvertisementInformationBuilder();
	}

	public String getFolderId() {
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

	public Integer getTax() {
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

	public static final class AdvertisementInformationBuilder {
		private String folderId;
		private String title;
		private String description;
		private Condition condition;
		private Integer tax;
		private Quantity quantityInStock;
		private BigDecimal purchasePrice;
		private ShippingInformation shippingInformation;
		private BigDecimal auctionPrice;

		private AdvertisementInformationBuilder() {
		}



		public AdvertisementInformationBuilder withFolderId(String folderId) {
			this.folderId = folderId;
			return this;
		}

		public AdvertisementInformationBuilder withTitle(String title) {
			this.title = title;
			return this;
		}

		public AdvertisementInformationBuilder withDescription(String description) {
			this.description = description;
			return this;
		}

		public AdvertisementInformationBuilder withCondition(Condition condition) {
			this.condition = condition;
			return this;
		}

		public AdvertisementInformationBuilder withTax(Integer tax) {
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
			this.title=recordInformation.getTitle();
			this.description= AdvertisementDescription.getDescription(recordInformation);
			return this;
		}

		public AdvertisementInformation build() {
			return new AdvertisementInformation(this);
		}
	}

	@Override public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AdvertisementInformation that = (AdvertisementInformation) o;
		return Objects.equals(getFolderId(), that.getFolderId()) && Objects.equals(getTitle(),
				that.getTitle()) && Objects.equals(getDescription(), that.getDescription())
				&& getCondition() == that.getCondition() && Objects.equals(getTax(), that.getTax())
				&& Objects.equals(getQuantityInStock(), that.getQuantityInStock()) && Objects.equals(
				getPurchasePrice(), that.getPurchasePrice()) && Objects.equals(getShippingInformation(),
				that.getShippingInformation()) && Objects.equals(getAuctionPrice(), that.getAuctionPrice());
	}

	@Override public int hashCode() {
		return Objects.hash(getFolderId(), getTitle(), getDescription(), getCondition(), getTax(), getQuantityInStock(),
				getPurchasePrice(), getShippingInformation(), getAuctionPrice());
	}

	@Override public String toString() {
		return "AdvertisementInformation{" +
				"folderId='" + folderId + '\'' +
				", title='" + title + '\'' +
				", description='" + description + '\'' +
				", condition=" + condition +
				", tax=" + tax +
				", quantityInStock=" + quantityInStock +
				", purchasePrice=" + purchasePrice +
				", shippingInformation=" + shippingInformation +
				", auctionPrice=" + auctionPrice +
				'}';
	}
}