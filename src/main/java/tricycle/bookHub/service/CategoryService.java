package tricycle.bookHub.service;

import org.springframework.stereotype.Service;
import tricycle.bookHub.repository.CategoryRepository;

@Service
public class CategoryService {

    private CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

}
