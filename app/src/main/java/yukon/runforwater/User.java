package yukon.runforwater;

public class User {
    private String username, email, partnerUser, bio;
    private Boolean kenyan;

    public User() {
    }

    private User(UserBuilder userbuilder) {
        this.kenyan = userbuilder.kenyan;
        this.username = userbuilder.username;
        this.email = userbuilder.email;
        this.partnerUser = userbuilder.partnerUser;
        this.bio = userbuilder.bio;
    }

    public Boolean getKenyan() {
        return kenyan;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    String getBio() {
        return bio;
    }

    public String getPartnerUser() {
        return partnerUser;
    }

    static class UserBuilder {
        private final String email;
        private final Boolean kenyan;
        private final String username;
        private String partnerUser;
        private String bio;

        UserBuilder(String email, Boolean kenyan, String username) {
            this.email = email;
            this.kenyan = kenyan;
            this.username = username;
        }

        public UserBuilder partnerUser(String partnerUser) {
            this.partnerUser = partnerUser;
            return this;
        }

        public UserBuilder bio(String bio) {
            this.bio = bio;
            return this;
        }

        User build() {
            return new User(this);
        }
    }
}
