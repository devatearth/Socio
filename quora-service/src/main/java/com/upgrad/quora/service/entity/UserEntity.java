package com.upgrad.quora.service.entity;


import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "getUserByUserName",query = "Select u from UserEntity u where u.userName=:userName"),
        @NamedQuery(name = "getUserByUserId",query = "Select u from UserEntity u where u.uuid=:userId"),
        @NamedQuery(name = "getUserByEmail",query = "select u from UserEntity u where u.email=:email")
})
public class UserEntity {

    //id column is primary key
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //uuid column is universal unique identity field
    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    //firstname column will be mapped to first name of user
    @Column(name = "firstname")
    @Size(max = 30)
    private String firstName;

    //lastname column will be mapped to lastname of user
    @Column(name = "lastname")
    @Size(max = 30)
    private String lastName;

    //username will be mapped to the username of the user
    @Column(name = "username")
    @Size(max = 30)
    private String userName;

    //email will be mapped to Email id of the user
    @Column(name = "email")
    @Size(max = 50)
    private String email;

    //password will be mapped to password provided by the user [Encrypted]
    @Column(name = "password")
    @Size(max = 255)
    private String password;

    //salt column will have the auto generated salt
    @Column(name = "salt")
    @Size(max = 200)
    private String salt;

    //country will be mapped to the country of user
    @Column(name = "country")
    @Size(max = 30)
    private String country;

    //aboutme will be mapped to the about me details provided by user at the time of registration
    @Column(name = "aboutme")
    @Size(max = 50)
    private String aboutMe;

    //dob will be mapped to D.O.B.
    @Column(name = "dob")
    @Size(max = 30)
    private String dob;

    //role will be mapped to role of user.
    @Column(name = "role")
    @Size(max = 30)
    private String role;

    //contactnumber will be mapped to contact number of user.
    @Column(name = "contactnumber")
    @Size(max = 30)
    private String contactNumber;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}

