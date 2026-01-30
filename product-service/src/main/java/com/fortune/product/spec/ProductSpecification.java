package com.fortune.product.spec;

import com.fortune.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

        public static Specification<Product> hasName(String name) {
            return (root, query, cb) ->
                    name == null ? null :
                            cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        }

        public static Specification<Product> hasCategory(String category) {
            return (root, query, cb) ->
                    category == null ? null :
                            cb.equal(cb.lower(root.get("category")), category.toLowerCase());
        }

        public static Specification<Product> minPrice(Double minPrice) {
            return (root, query, cb) ->
                    minPrice == null ? null :
                            cb.greaterThanOrEqualTo(root.get("price"), minPrice);
        }

        public static Specification<Product> maxPrice(Double maxPrice) {
            return (root, query, cb) ->
                    maxPrice == null ? null :
                            cb.lessThanOrEqualTo(root.get("price"), maxPrice);
        }

}
