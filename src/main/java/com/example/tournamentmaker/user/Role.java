package com.example.tournamentmaker.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.tournamentmaker.user.PermissionEnum.*;

@Getter
@RequiredArgsConstructor
public enum Role {

    USER(Collections.emptySet()),
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            )
    ),
    MANAGER(
            Set.of(
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            )
    );
    private final Set<PermissionEnum> permissionEnums;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissionEnums()
                .stream()
                .map(permissionEnum -> new SimpleGrantedAuthority(permissionEnum.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}

