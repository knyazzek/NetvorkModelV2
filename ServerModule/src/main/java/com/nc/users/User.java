package com.nc.users;

import com.nc.users.Role;
import org.mindrot.jbcrypt.BCrypt;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public class User implements Externalizable {
    private String login;
    private String password;
    private final Set<Role> roles;
    private static final long serialVersionUID = 15L;
    private boolean isActive;

    public User() {
        this(null, null, new HashSet<>());
    }

    public User(String login, String password, Set<Role> roles) {
        this.login = login;
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
        this.roles = roles;
    }

    public User(String login, String password, Role role) {
        this(login, password, new HashSet<>(Collections.singletonList(role)));
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return new ArrayList<>(roles);
    }

    public void addRole(Role roles) {
        this.roles.add(roles);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isPasswordEqualsTo(String password) {
        return BCrypt.checkpw(password, this.password);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(login);
        out.writeObject(password);

        out.writeInt(roles.size());

        for (Role role : roles) {
            out.writeObject(role);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.login = (String) in.readObject();
        this.password = (String) in.readObject();

        int rolesCount = in.readInt();

        for (int i = 0; i < rolesCount; i++) {
            roles.add((Role) in.readObject());
        }
    }
}
