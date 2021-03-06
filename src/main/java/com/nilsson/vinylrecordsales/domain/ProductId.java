package com.nilsson.vinylrecordsales.domain;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class ProductId {
	private final Integer id;

	public ProductId(Integer id) {
		this.id = requireNonNull(id, "id");
	}

	public Integer getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ProductId productId = (ProductId) o;
		return id.equals(productId.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return String.valueOf(id);
	}
}
