package yukon.runforlife;

public class User {
    private String username, email, partnerUser;


    private User(UserBuilder userbuilder) {
        this.username = userbuilder.username;
        this.email = userbuilder.email;
        this.partnerUser = userbuilder.partnerUser;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPartnerUser() {
        return partnerUser;
    }

    static class UserBuilder {
        private final String email;
        private final String username;
        private String partnerUser;

        UserBuilder(String email, String username) {
            this.email = email;
            this.username = username;
        }

        public UserBuilder partnerUser(String partnerUser) {
            this.partnerUser = partnerUser;
            return this;
        }

        User build() {
            return new User(this);
        }
    }
}
