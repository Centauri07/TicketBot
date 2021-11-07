package me.centauri07.ticketbot.utility.permission;

import com.github.stefan9110.dcm.permission.CustomPermission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuildPermission implements CustomPermission {
    List<Permission> permissions = new ArrayList<>();

    public GuildPermission(Permission... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
    }

    @Override
    public boolean hasPermission(Member member) {
        return member.getPermissions().stream().anyMatch(role -> permissions.contains(role));
    }

    @Override
    public String noPermissionMessage() {
        return null;
    }
}
