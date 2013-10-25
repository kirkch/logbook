package framework.river.request;

import java.util.Objects;

/**
 *
 */
public class UserDTO {

    private String id;
    private String name;

    public UserDTO( String id, String name ) {
        this.id   = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals( Object o ) {
        if ( !(o instanceof UserDTO) ) {
            return false;
        } else if ( o == this ) {
            return true;
        }

        UserDTO other = (UserDTO) o ;
        return Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name);
    }

    public String toString() {
        return name;
    }

}
