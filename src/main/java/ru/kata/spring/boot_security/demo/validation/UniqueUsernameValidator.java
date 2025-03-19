package ru.kata.spring.boot_security.demo.validation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.kata.spring.boot_security.demo.dao.UserRepo;
import ru.kata.spring.boot_security.demo.model.User;

@Component
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    @Autowired
    private UserRepo userRepo;

    public UniqueUsernameValidator() {
    }


    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if (userRepo == null || username == null) {
            return true;
        }

        int userId = getUserIdFromRequest ();

        // Если userId отсутствует (создание нового пользователя)
        if (userId == 0) {
            return !userRepo.existsByUsername (username);
        }

        // Если userId есть (обновление пользователя), проверяем, что это тот же самый пользователь
        User existingUser = userRepo.findById (userId).orElse (null);

        // Разрешаем, если username совпадает с уже существующим пользователем
        if (existingUser.getUsername ().equals (username)) {
            return true;
        }
        return !userRepo.existsByUsername (username);
    }


    private int getUserIdFromRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes ();
        if (attributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest ();
            String userIdParam = request.getParameter ("id");
            if (userIdParam != null) {
                try {
                    return Integer.parseInt (userIdParam);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }
}
