package tricycle.bookHub.service;

import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private CategoryService repository;

    public CategoryService(CategoryService repository) {
        this.repository = repository;
    }


}
