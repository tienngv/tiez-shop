package com.tiezshop.configurations.runner;

import com.tiezshop.entity.Brand;
import com.tiezshop.entity.Category;
import com.tiezshop.entity.Role;
import com.tiezshop.entity.User;
import com.tiezshop.repository.BrandRepository;
import com.tiezshop.repository.CategoryRepository;
import com.tiezshop.repository.RoleRepository;
import com.tiezshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class InitDataRunner implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        initCategories();
        initBrands();
        initUsers();
        log.info("Sample data initialization completed");
    }

    private void initCategories() {
        if (categoryRepository.count() == 0) {
            log.info("Creating sample categories...");
            
            // Tạo categories gốc
            Category clothing = Category.builder()
                    .name("Quần áo")
                    .description("Quần áo thể thao và thời trang")
                    .imageUrl("https://via.placeholder.com/300x200?text=Clothing")
                    .isActive(true)
                    .build();
            categoryRepository.save(clothing);

            Category shoes = Category.builder()
                    .name("Giày dép")
                    .description("Giày thể thao, giày chạy bộ")
                    .imageUrl("https://via.placeholder.com/300x200?text=Shoes")
                    .isActive(true)
                    .build();
            categoryRepository.save(shoes);

            Category accessories = Category.builder()
                    .name("Phụ kiện")
                    .description("Túi xách, mũ, kính")
                    .imageUrl("https://via.placeholder.com/300x200?text=Accessories")
                    .isActive(true)
                    .build();
            categoryRepository.save(accessories);

            // Tạo subcategories
            Category tops = Category.builder()
                    .name("Áo")
                    .description("Áo thun, áo khoác thể thao")
                    .imageUrl("https://via.placeholder.com/300x200?text=Tops")
                    .isActive(true)
                    .parentCategory(clothing)
                    .build();
            categoryRepository.save(tops);

            Category bottoms = Category.builder()
                    .name("Quần")
                    .description("Quần short, quần dài thể thao")
                    .imageUrl("https://via.placeholder.com/300x200?text=Bottoms")
                    .isActive(true)
                    .parentCategory(clothing)
                    .build();
            categoryRepository.save(bottoms);
        }
    }

    private void initBrands() {
        if (brandRepository.count() == 0) {
            log.info("Creating sample brands...");
            
            Brand nike = Brand.builder()
                    .name("Nike")
                    .description("Just Do It - Thương hiệu thể thao hàng đầu thế giới")
                    .logoUrl("https://via.placeholder.com/200x100?text=Nike")
                    .country("USA")
                    .isActive(true)
                    .build();
            brandRepository.save(nike);

            Brand adidas = Brand.builder()
                    .name("Adidas")
                    .description("Impossible is Nothing - Thương hiệu thể thao Đức")
                    .logoUrl("https://via.placeholder.com/200x100?text=Adidas")
                    .country("Germany")
                    .isActive(true)
                    .build();
            brandRepository.save(adidas);

            Brand puma = Brand.builder()
                    .name("Puma")
                    .description("Forever Faster - Thương hiệu thể thao năng động")
                    .logoUrl("https://via.placeholder.com/200x100?text=Puma")
                    .country("Germany")
                    .isActive(true)
                    .build();
            brandRepository.save(puma);

            Brand converse = Brand.builder()
                    .name("Converse")
                    .description("All Star - Thương hiệu giày cổ điển")
                    .logoUrl("https://via.placeholder.com/200x100?text=Converse")
                    .country("USA")
                    .isActive(true)
                    .build();
            brandRepository.save(converse);

            Brand vans = Brand.builder()
                    .name("Vans")
                    .description("Off The Wall - Thương hiệu giày skateboard")
                    .logoUrl("https://via.placeholder.com/200x100?text=Vans")
                    .country("USA")
                    .isActive(true)
                    .build();
            brandRepository.save(vans);
        }
    }

    private void initUsers() {
        if (userRepository.count() == 0) {
            log.info("Creating demo user...");
            
            // Tạo user demo
            User demoUser = new User();
            demoUser.setId("demo-user-id");
            demoUser.setUsername("demo@tiezshop.com");
            demoUser.setEmail("demo@tiezshop.com");
            demoUser.setFirstName("Demo");
            demoUser.setLastName("User");
            demoUser.setFullName("Demo User");
            demoUser.setStatus(User.UserStatus.ACTIVE);
            
            // Gán role CUSTOMER
            demoUser.setRoles(List.of(roleRepository.findByName("CUSTOMER").orElse(null)));
            userRepository.save(demoUser);
            
            log.info("Demo user created: demo@tiezshop.com / demo123");
        }
    }
}