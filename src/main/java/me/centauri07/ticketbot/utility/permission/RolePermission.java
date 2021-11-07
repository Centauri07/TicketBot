package me.centauri07.ticketbot.utility.permission;

import com.github.stefan9110.dcm.permission.CustomPermission;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class RolePermission implements CustomPermission {
    List<Long> roleIds = new ArrayList<>();

    public RolePermission(long... roleIds) {
        for (long roleId : roleIds) this.roleIds.add(roleId);
    }

    @Override
    public boolean hasPermission(Member member) {
        return member.getRoles().stream().anyMatch(role -> roleIds.contains(role.getIdLong()));
    }

    @Override
    public String noPermissionMessage() {
        return "You don't have the permission to execute this command!";
    }
}
