package com.scaler.productcatalog.services;

import com.scaler.productcatalog.dtos.SearchRequestDto;
import com.scaler.productcatalog.exceptions.InvalidRequestException;
import com.scaler.productcatalog.models.Category;
import com.scaler.productcatalog.models.Product;
import com.scaler.productcatalog.repositories.ProductRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
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
    static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "title", "price", "createdAt", "updatedAt");

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> searchProducts(SearchRequestDto request) {
        if (request.getMinPrice() != null && request.getMaxPrice() != null
                && request.getMinPrice() > request.getMaxPrice()) {
            throw new InvalidRequestException("minPrice must be less than or equal to maxPrice");
        }
        return productRepository.findAll(buildSpecification(request), buildPageable(request));
    }

    Pageable buildPageable(SearchRequestDto request) {
        int pageSize = request.getPageSize() > 0 ? Math.min(request.getPageSize(), 100) : 10;
        int pageNumber = Math.max(request.getPageNumber(), 0);
        String sortBy = request.getSortBy() != null && ALLOWED_SORT_FIELDS.contains(request.getSortBy()) ? request.getSortBy() : "id";
        Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
    }

    Specification<Product> buildSpecification(SearchRequestDto request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            boolean hasQuery = request.getQuery() != null && !request.getQuery().isBlank();
            boolean hasCategory = request.getCategory() != null && !request.getCategory().isBlank();

            // A single LEFT JOIN shared by the keyword and category predicates
            Join<Product, Category> category = (hasQuery || hasCategory) ? root.join("category", JoinType.LEFT) : null;

            if (hasQuery) {
                String text = "%" + request.getQuery().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), text),
                        cb.like(cb.lower(root.get("description")), text),
                        cb.like(cb.lower(category.get("title")), text)
                ));
            }

            if (hasCategory) {
                // Exact (case-insensitive) match so the category_id index can be used
                predicates.add(cb.equal(cb.lower(category.get("title")), request.getCategory().trim().toLowerCase()));
            }

            if (request.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
            }

            if (request.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
