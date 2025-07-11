package org.springboot.udemy.initial.category.service;

import com.embarkx.ecommerce.exception.APIException;
import com.embarkx.ecommerce.exception.ResourceNotFoundException;
import com.embarkx.ecommerce.model.Categories;
import com.embarkx.ecommerce.payload.CategoryDTO;
import com.embarkx.ecommerce.payload.CategoryResponse;
import com.embarkx.ecommerce.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String soryByOrder, String sortOrder) {

        Sort sortByOrd = sortOrder.equalsIgnoreCase("asc") ? Sort.by(soryByOrder).ascending() : Sort.by(soryByOrder).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByOrd);

        Page<Categories> categoriesPage = categoryRepository.findAll(pageable);
        List<Categories> categoriesList = categoriesPage.getContent();

        if(categoriesList.isEmpty())
            throw new APIException("No category created till now.");

        List<CategoryDTO> categoryDTOS = categoriesList.stream().map(categories ->
                modelMapper.map(categories, CategoryDTO.class)).toList();

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(categoriesPage.getNumber());
        categoryResponse.setPageSize(categoriesPage.getSize());
        categoryResponse.setTotalElements(categoriesPage.getTotalElements());
        categoryResponse.setTotalPages(categoriesPage.getTotalPages());
        categoryResponse.setLastPage(categoriesPage.isLast());
        return categoryResponse;

    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Categories categories = modelMapper.map(categoryDTO, Categories.class);
        Categories categoryFromDb = categoryRepository.findByCategoryName(categories.getCategoryName());

        if(categoryFromDb!=null){
            throw new APIException("Category with the name " + categories.getCategoryName() + " already exists !!!");
        }

//        else save the category
        Categories saveCategories = categoryRepository.save(categories);
        return modelMapper.map(saveCategories, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Categories category = categoryRepository.findById(Math.toIntExact(categoryId))
                .orElseThrow(() -> new ResourceNotFoundException("Category","categoryId",categoryId));

        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
        Categories savedCategories = categoryRepository.findById(Math.toIntExact(categoryId))
                .orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));

        Categories categories = modelMapper.map(categoryDTO, Categories.class);
        categories.setCategoryId(savedCategories.getCategoryId());

        savedCategories = categoryRepository.save(categories);
        return modelMapper.map(savedCategories, CategoryDTO.class);
    }
}
