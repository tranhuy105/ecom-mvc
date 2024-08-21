package com.tranhuy105.admin.category;

import com.tranhuy105.admin.utils.FileUploadUtil;
import com.tranhuy105.common.entity.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public static final int PAGE_SIZE = 30;
    private static final String CATEGORY_LEVEL_PREFIX = "--";
    private final Map<String, List<Category>> cache = new ConcurrentHashMap<>();

    public Page<Category> findAll(Integer page, String search) {
        page = page != null ? page : 1;
        page = page < 1 ? 1 : page;
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);

        List<Category> allCategories = findAllWithHierarchy();
        List<Category> filteredCategories = allCategories.stream()
                .filter(category -> search == null || search.isEmpty() || category.getName().toLowerCase().contains(search.toLowerCase()))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredCategories.size());

        return new PageImpl<>(filteredCategories.subList(start, end), pageable, filteredCategories.size());
    }
    public Category findById(Integer id) {
        return categoryRepository.findByIdWithChildrenAndParent(id).orElse(null);
    }

    @Transactional
    public Category save(Category newCategory, MultipartFile image) throws IOException {
        validateCategoryNameAndAlias(newCategory);
        handleSaveImage(newCategory, image);
        return categoryRepository.save(newCategory);
    }

    public List<Category> findAllWithHierarchy() {
        return cache.computeIfAbsent(getCacheKey(), key -> {
            List<Category> categories = categoryRepository.findAllWithChildrenAndParent();

            return getSortedRootCategoriesWithChildren(categories);
        });
    }

    private void handleSaveImage(Category category, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty() && category.getImage() != null) {
            String fileName = FileUploadUtil.validateAndGetImageFilename(image);
            category.setImage(fileName);
            // if is creating new
            if (category.getId() == null) {
                category = categoryRepository.save(category);
            }
            String uploadDir = "category-images/" + category.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, image);
        }
    }

    public void updateCategoryInCache(Category newCategory) {
        List<Category> cachedCategories = cache.get(getCacheKey());
        if (cachedCategories == null) {
            cachedCategories = findAllWithHierarchy();
        } else {
            // Remove the category if it already exists in the cache (for update scenarios)
            cachedCategories.removeIf(c -> c.getId().equals(newCategory.getId()));

            // If it has a parent, find the parent's children and add it to the correct place
            if (newCategory.getParent() != null) {
                for (Category category : cachedCategories) {
                    if (category.getId().equals(newCategory.getParent().getId())) {
                        category.getChildren().add(newCategory);
                        break;
                    }
                }
            }
        }


        List<Category> sortedCategories = getSortedRootCategoriesWithChildren(cachedCategories);
        cache.put(getCacheKey(), sortedCategories);
    }

    private List<Category> getSortedRootCategoriesWithChildren(List<Category> cachedCategories) {
        List<Category> rootCategories = new ArrayList<>();
        for (Category category : cachedCategories) {
            if (category.getParent() == null) {
                rootCategories.add(category);
            }
        }

        List<Category> sortedCategories = new ArrayList<>();
        for (Category rootCategory : rootCategories) {
            addCategoryWithChildren(sortedCategories, rootCategory, 0);
        }

        return sortedCategories;
    }

    private void addCategoryWithChildren(List<Category> sortedCategories, Category category, int level) {
        // Adjust the category name based on its level in the hierarchy
        category.setName(CATEGORY_LEVEL_PREFIX.repeat(level) + category.getName());

        //  Add the current category to the sorted list
        sortedCategories.add(category);

        // Recursively process the children
        for (Category child : category.getChildren()) {
            addCategoryWithChildren(sortedCategories, child, level + 1);
        }
    }

    private void validateCategoryNameAndAlias(Category newCategory) {
        if (newCategory.getName() == null ||
                newCategory.getName().trim().isEmpty() ||
                newCategory.getAlias() == null || newCategory.getAlias().trim().isEmpty()) {
            throw new IllegalArgumentException("Name Or Alias Can Not Be Empty.");
        }

        if (!isUrlFriendly(newCategory.getAlias())) {
            throw new IllegalArgumentException("Alias is not URL-friendly. It should only contain lowercase letters, numbers, hyphens, and underscores.");
        }

        Optional<Integer> idDB = categoryRepository.findByNameOrAlias(newCategory.getName(), newCategory.getAlias());
        if (idDB.isPresent()) {
            // create
            if (newCategory.getId() == null) {
                throw new IllegalArgumentException("Name Or Alias Already exists.");
            }

            // update
            if (!newCategory.getId().equals(idDB.get())) {
                throw new IllegalArgumentException("Name Or Alias Already exists.");
            }
        }
    }

    private boolean isUrlFriendly(String alias) {
        return alias.matches("^[a-z0-9-_]+$");
    }

    public boolean delete(Integer id) {
        Category deleted = findById(id);
        if (deleted == null) {
            return false;
        }
        try {
            categoryRepository.delete(deleted.getId());
        } catch (Exception exception) {
            log.error("error deleting category", exception);
            return false;
        }
        cache.remove(getCacheKey());
        return true;
    }

    private String getCacheKey() {
        return "categoryList";
    }
}
