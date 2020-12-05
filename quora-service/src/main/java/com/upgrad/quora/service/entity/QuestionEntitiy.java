package com.upgrad.quora.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.time.ZonedDateTime;

//This model is implemented to map with question table in DB
@Entity
@Table(name = "question")
@NamedQueries({
        @NamedQuery(name = "getAllQuestions",query = "Select u from QuestionEntitiy u"),
        //Added by @github.com/vetrivel-muthusamy
        @NamedQuery(name = "getQuestionByUuid", query = "select u from QuestionEntitiy u where u.uuid=:uuid"),
        @NamedQuery(name = "getQuestionById", query = "select u from QuestionEntitiy u where u.userId=:user"),
        @NamedQuery(name = "questionByQUuid", query = "select q from QuestionEntitiy q where q.uuid =:uuid"),
       // @NamedQuery(name= "allQuestionsByUserId",query = "select qe from QuestionEntitiy qe inner join qe.user usr where usr.uuid = :uuid"),
})
public class QuestionEntitiy {

    //id field is primary key
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //uuid column is universal unique identity field
    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    //content column will contain the content of question
    @Column(name = "content")
    @Size(max = 500)
    private String content;

    //date column will contain the date at whichh the question is posted
    @Column(name = "date")
    private ZonedDateTime date;


    //user_id column will contain the user who posted the question
    @Column(name = "user_id")
    private Integer userId;


    /*
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "USER_ID")
    private UserEntity user;

     */

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /*
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
    */


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }






}
