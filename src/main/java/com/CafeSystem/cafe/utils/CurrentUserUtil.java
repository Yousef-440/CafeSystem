package com.CafeSystem.cafe.utils;

import com.CafeSystem.cafe.dto.PaginatedResponse;
import com.CafeSystem.cafe.model.User;
import com.CafeSystem.cafe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentUserUtil {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername());
        }

        return Optional.empty();
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_ADMIN"));
    }


    public boolean isUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equalsIgnoreCase("ROLE_USER"));
    }

    public String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        return null;
    }

    public <T> PaginatedResponse<T> buildPaginatedResponse(
            Page<T> page,
            int pageNumber,
            int pageSize,
            String baseUrl,
            String pageParam,
            String limitParam
    ) {
        String format = "%s?%s=%d&%s=%d";

        String nextUrl = page.hasNext() ? String.format(format, baseUrl, pageParam, pageNumber + 1, limitParam, pageSize) : null;
        String prevUrl = page.hasPrevious() ? String.format(format, baseUrl, pageParam, pageNumber - 1, limitParam, pageSize) : null;

        return PaginatedResponse.<T>builder()
                .content(page.getContent())
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .nextPageURL(nextUrl)
                .prevPageURL(prevUrl)
                .build();
    }


    public <T> PaginatedResponse<T> buildPaginatedResponseToSearchByName(
            Page<T> page,
            int pageNumber,
            int pageSize,
            String baseUrl,
            String pageParam,
            String limitParam,
            String additionalParams
    ) {
        String format = "%s?%s=%d&%s=%d%s";

        String nextUrl = page.hasNext() ? String.format(format, baseUrl, pageParam, pageNumber + 1, limitParam, pageSize, additionalParams) : null;
        String prevUrl = page.hasPrevious() ? String.format(format, baseUrl, pageParam, pageNumber - 1, limitParam, pageSize, additionalParams) : null;

        return PaginatedResponse.<T>builder()
                .content(page.getContent())
                .currentPage(page.getNumber() + 1)
                .totalPages(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .nextPageURL(nextUrl)
                .prevPageURL(prevUrl)
                .build();
    }


}
