package tricycle.bookHub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tricycle.bookHub.model.Category;
import tricycle.bookHub.repository.CategoryRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository repository;

    public List<Category> getAll() {
        return repository.findAll();
    }

    public Category getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable : " + id));
    }

    public Category create(Category category) {
        return repository.save(category);
    }

    public Category update(Category category, Long id) {
        Category existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable : " + id));
        existing.setLibelle(category.getLibelle());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Catégorie introuvable : " + id);
        }
        repository.deleteById(id);
    }
}