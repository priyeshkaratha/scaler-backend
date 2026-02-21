package com.scaler.productcatalog.services;

import com.scaler.productcatalog.dtos.SearchRequestDto;
import com.scaler.productcatalog.models.Product;
import com.scaler.productcatalog.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class SearchServiceImpl implements SearchService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "title", "price", "createdAt", "updatedAt");

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> searchProducts(SearchRequestDto request) {
        int pageSize = request.getPageSize() > 0 ? request.getPageSize() : 10;
        int pageNumber = Math.max(request.getPageNumber(), 0);

        String sortBy = ALLOWED_SORT_FIELDS.contains(request.getSortBy()) ? request.getSortBy() : "id";
        String sortDirection = request.getSortDirection() == null ? "asc" : request.getSortDirection();
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));

        Specification<Product> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (request.getQuery() != null && !request.getQuery().isBlank()) {
                String text = "%" + request.getQuery().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), text),
                        cb.like(cb.lower(root.get("description")), text),
                        cb.like(cb.lower(root.join("category", jakarta.persistence.criteria.JoinType.LEFT).get("title")), text)
                ));
            }

            if (request.getCategory() != null && !request.getCategory().isBlank()) {
                String categoryText = "%" + request.getCategory().toLowerCase() + "%";
                predicates.add(cb.like(
                        cb.lower(root.join("category", jakarta.persistence.criteria.JoinType.LEFT).get("title")),
                        categoryText
                ));
            }

            if (request.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
            }

            if (request.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return productRepository.findAll(spec, pageable);
    }
}
