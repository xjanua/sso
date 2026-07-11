package me.xjanua.spring.backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.model.User;

/**
 * Resolver để map scope code -> user field value
 * Mỗi scope sẽ quyết định lấy thông tin gì từ User
 */
@Service
@RequiredArgsConstructor
public class ScopeResolver {

    /**
     * Resolve danh sách scopes thành map thông tin user
     *
     * @param user           User cần lấy thông tin
     * @param scopeCodes     Danh sách scope codes được yêu cầu
     * @return Map chứa thông tin user theo các scope
     */
    public Map<String, Object> resolve(User user, List<String> scopeCodes) {
        Map<String, Object> result = new HashMap<>();

        for (String scopeCode : scopeCodes) {
            Map<String, Object> scopeData = resolveScope(user, scopeCode);
            if (scopeData != null) {
                result.putAll(scopeData);
            }
        }

        return result;
    }

    /**
     * Resolve một scope cụ thể
     */
    private Map<String, Object> resolveScope(User user, String scopeCode) {
        return switch (scopeCode.toLowerCase()) {
            case "profile" -> resolveProfile(user);
            case "email" -> resolveEmail(user);
            case "avatar" -> resolveAvatar(user);
            case "phone" -> resolvePhone(user);
            case "basic" -> resolveBasic(user);
            default -> null;
        };
    }

    private Map<String, Object> resolveProfile(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("first_name", user.getFirstName());
        data.put("last_name", user.getLastName());
        data.put("full_name", user.getFullName());
        return data;
    }

    private Map<String, Object> resolveEmail(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("email", user.getEmail());
        return data;
    }

    private Map<String, Object> resolveAvatar(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("avatar_url", user.getAvatarUrl());
        return data;
    }

    private Map<String, Object> resolvePhone(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("phone_number", user.getPhoneNumber());
        return data;
    }

    private Map<String, Object> resolveBasic(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("user_id", user.getId());
        data.put("username", user.getUsername());
        return data;
    }
}
