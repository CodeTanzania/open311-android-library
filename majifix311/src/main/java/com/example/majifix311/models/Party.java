package com.example.majifix311.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Party is a convenient concept that allows us to deal with individuals or
 * groups as if they were the same kind of thing
 *
 * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
 * @version 0.1.0
 * @see <a href="http://tdan.com/a-universal-person-and-organization-data-model/5014">http://tdan.com/a-universal-person-and-organization-data-model/5014</a>
 * @see <a href="https://en.wikipedia.org/wiki/Generic_data_model">https://en.wikipedia.org/wiki/Generic_data_model</a>
 * @since 0.1.0
 */

public class Party implements Serializable {
    /**
     * Backend(API) unique identifier of a party
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    @Expose
    @SerializedName("_id")
    private String objectId;


    /**
     * A jurisdiction(area, branch, division etc) under which a party serving.
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    @Expose
    @SerializedName("jurisdiction")
    private String jurisdiction;


    /**
     * Human readable name used to identify a party.
     * It may be a person full name, company name etc.
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    @Expose
    @SerializedName("name")
    private String name;


    /**
     * Valid unique email address of  a party.
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    @Expose
    @SerializedName("email")
    private String email;


    /**
     * Valid existing phone number that can be used to contact a party directly
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    @Expose
    @SerializedName("phone")
    private String phone;


    /**
     * A base64 encode party avatar content.
     * It may be a person photo, company logo etc.
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    @Expose
    @SerializedName("avatar")
    private String avatar;

    /**
     * A set of allowed permissions for a party. Used to restrict what party can or can not
     * perform
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    @Expose
    @SerializedName("permissions")
    private Set<String> permissions;

    /**
     * A form of relationship a party established with high level jurisdiction
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    @Expose
    @SerializedName("relation")
    private Relation relation;

    /**
     * Backend(API) time when a party was registered
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    @Expose
    @SerializedName("createdAt")
    private Date createdAt;

    /**
     * Backend(API) time when a party was last updated
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    @Expose
    @SerializedName("updatedAt")
    private Date updatedAt;

    /**
     * Backend(API) time when a party was locked from using resources available
     *
     * @version 0.1.0
     * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
     * @since 0.1.0
     */
    @Expose
    @SerializedName("lockedAt")
    private Date lockedAt;

    public Party() {
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String _id) {
        this.objectId = _id;
    }

    public String getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(String jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getLockedAt() {
        return lockedAt;
    }

    public void setLockedAt(Date lockedAt) {
        this.lockedAt = lockedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Party party = (Party) o;

        if (objectId != null ? !objectId.equals(party.objectId) : party.objectId != null)
            return false;
        if (name != null ? !name.equals(party.name) : party.name != null) return false;
        if (email != null ? !email.equals(party.email) : party.email != null) return false;
        return phone != null ? phone.equals(party.phone) : party.phone == null;
    }

    @Override
    public int hashCode() {
        int result = objectId != null ? objectId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * A Party Relation
     */
    public static class Relation {
        /**
         * Human readable name of relationship established e.g Customer, Employee, Civilian etc
         *
         * @version 0.1.0
         * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
         * @since 0.1.0
         */
        @Expose
        @SerializedName("name")
        private String name;

        /**
         * Human readable name of type of relation formed
         *
         * @version 0.1.0
         * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
         * @since 0.1.0
         */
        @Expose
        @SerializedName("type")
        private String type;

        /**
         * Human readable name of a  workspace of relation formed e.g Call Center etc
         *
         * @version 0.1.0
         * @author lally elias <a href="mailto:lallyelias87@gmail.com">lallyelias87@gmail.com</a>
         * @since 0.1.0
         */
        @Expose
        @SerializedName("workspace")
        private String workspace;

        public Relation() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getWorkspace() {
            return workspace;
        }

        public void setWorkspace(String workspace) {
            this.workspace = workspace;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Relation relation = (Relation) o;

            if (name != null ? !name.equals(relation.name) : relation.name != null) return false;
            if (type != null ? !type.equals(relation.type) : relation.type != null) return false;
            return workspace != null ? workspace.equals(relation.workspace) : relation.workspace == null;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            result = 31 * result + (workspace != null ? workspace.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return this.name + "/" + this.type;
        }
    }
}
